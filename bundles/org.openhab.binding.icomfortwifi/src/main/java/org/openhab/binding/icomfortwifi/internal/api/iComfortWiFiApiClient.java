/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.icomfortwifi.internal.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.binding.icomfortwifi.internal.api.models.request.ReqSetAwayMode;
import org.openhab.binding.icomfortwifi.internal.api.models.request.ReqSetTStatInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.BuildingsInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.PreferedLanguage;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.RequestStatus;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.TempUnits;
import org.openhab.binding.icomfortwifi.internal.api.models.response.GatewayInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.GatewaysAlerts;
import org.openhab.binding.icomfortwifi.internal.api.models.response.OwnerProfileInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.SystemInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.SystemsInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.UserValidation;
import org.openhab.binding.icomfortwifi.internal.api.models.response.ZoneStatus;
import org.openhab.binding.icomfortwifi.internal.api.models.response.ZonesStatus;
import org.openhab.binding.icomfortwifi.internal.configuration.iComfortWiFiBridgeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import com.google.gson.Gson;

// import antlr.collections.List;

// import org.apache.commons.lang3.StringUtils;

/**
 * Implementation of the iComfortWiFi client api
 *
 * @author Konstantin Panchenko - Initial contribution
 * @auther Jason Kotan Updated imports. Modified Login and Validate user to work with API.
 */
@NonNullByDefault
public class iComfortWiFiApiClient {

    public final Logger logger = LoggerFactory.getLogger(iComfortWiFiApiClient.class);
    public final HttpClient httpClient;
    public final iComfortWiFiBridgeConfiguration configuration;
    public final ApiAccess apiAccess;
    private @Nullable BuildingsInfo buildingsInfo = new BuildingsInfo();
    private @Nullable OwnerProfileInfo ownerProfileInfo = new OwnerProfileInfo();
    private SystemsInfo systemsInfo = new SystemsInfo();
    private final Integer alertsCount = 20;

    /**
     * Creates a new API client based on the V1 API interface
     *
     * @param configuration The configuration of the account to use
     * @throws Exception
     */
    // @NonNullByDefault
    public iComfortWiFiApiClient(iComfortWiFiBridgeConfiguration configuration, HttpClient httpClient)
            throws Exception {
        this.configuration = configuration;
        // Uncomment this line to initialize httpClient
        this.httpClient = httpClient;
        apiAccess = new ApiAccess(httpClient);
    }

    /**
     * Closes the current connection to the API
     */
    public void close() {
        apiAccess.setUserCredentials("");
        ownerProfileInfo = null;
        buildingsInfo = null;
        systemsInfo = new SystemsInfo();

        if (httpClient.isStarted()) {
            try {
                httpClient.stop();
            } catch (Exception e) {
                logger.debug("Could not stop http client.", e);
            }
        }
    }

    // Initial talk to iComfortWiFi API service
    public boolean login() {
        boolean success = validateUsername();
        // If the authentication succeeded, gather the basic information as well
        if (success) {
            try {
                ownerProfileInfo = requestUserAccount(URLEncoder.encode(configuration.userName, "UTF-8"));
                buildingsInfo = requestBuildingsInfo(URLEncoder.encode(configuration.userName, "UTF-8"));
                systemsInfo = requestSystemsInfo(URLEncoder.encode(configuration.userName, "UTF-8"));
            } catch (TimeoutException e) {
                logger.warn("Timeout while retrieving user and location information. Failing loging.");
                success = false;
            } catch (UnsupportedEncodingException e) {
                logger.error("Credential conversion failed", e);
                success = false;
            }
            if (ownerProfileInfo == null) {
                logger.debug("Failed to get ownerProfileInfo");
                success = false;
            }
            if (buildingsInfo == null) {
                logger.debug("Failed to get system buildingsInfo");
                success = false;
            }
            if (ownerProfileInfo == null || buildingsInfo == null) {
                logger.debug("Failed to get system basic information");
                success = false;
            }
        } else {
            logger.debug("Authorization failed");
        }
        return success;
    }

    public void logout() {
        close();
    }

    public void update() {
        try {
            if (systemsInfo.returnStatus.equals(RequestStatus.SUCCESS)) {
                for (int i = 0; i < systemsInfo.systemInfo.size(); i++) {
                    SystemInfo system = systemsInfo.systemInfo.get(i);
                    // Initialize GatewayInfo if null
                    GatewayInfo gatewayInfo = system.getGatewayInfo();
                    // Use preferred temperature unit from gateway info
                    TempUnits prefTempCurrent = gatewayInfo.preferedTemperatureUnit; // Directly access without null
                                                                                     // check
                    GatewayInfo updatedGatewayInfo = requestGatewayInfo(system.gatewaySN, prefTempCurrent);
                    if (updatedGatewayInfo.returnStatus.equals(RequestStatus.SUCCESS)) {
                        updatedGatewayInfo.preferedTemperatureUnit = prefTempCurrent;
                        // Re-create SystemInfo with the updated GatewayInfo
                        system = new SystemInfo(updatedGatewayInfo, system.getGatewaysAlerts(),
                                system.getZonesStatus());
                    } else {
                        // If the request fails, continue without creating a new instance
                        continue;
                    }
                    // Update alerts
                    GatewaysAlerts gatewaysAlerts = requestGatewaysAlerts(system.gatewaySN,
                            system.getGatewayInfo().preferedLanguage, alertsCount);
                    // Handle alerts; initialize with default values if request fails
                    GatewaysAlerts updatedAlerts;
                    if (gatewaysAlerts.returnStatus.equals(RequestStatus.SUCCESS)) {
                        updatedAlerts = gatewaysAlerts;
                    } else {
                        // Create a new GatewaysAlerts instance with default values
                        updatedAlerts = new GatewaysAlerts(RequestStatus.UNKNOWN, new ArrayList<>());
                    }
                    // Re-create SystemInfo with the updated GatewaysAlerts
                    system = new SystemInfo(system.getGatewayInfo(), updatedAlerts, system.getZonesStatus());

                    // Update zones status
                    ZonesStatus zonesStatus = requestZonesStatus(system.gatewaySN, prefTempCurrent);
                    if (zonesStatus.returnStatus.equals(RequestStatus.SUCCESS)) {
                        for (ZoneStatus zone : zonesStatus.zoneStatus) {
                            zone.preferedTemperatureUnit = prefTempCurrent;
                        }
                        // Re-create SystemInfo with the updated ZonesStatus
                        system = new SystemInfo(system.getGatewayInfo(), system.getGatewaysAlerts(), zonesStatus);
                    }
                    // Update the list with the modified system object
                    systemsInfo.systemInfo.set(i, system); // Ensure you update the list with the new SystemInfo
                                                           // instance
                }
            }
        } catch (TimeoutException e) {
            logger.info("Timeout on update");
        }
    }

    public void update(TempUnits tempUnit) {
        try {
            if (systemsInfo.returnStatus.equals(RequestStatus.SUCCESS)) {
                for (int i = 0; i < systemsInfo.systemInfo.size(); i++) {
                    SystemInfo system = systemsInfo.systemInfo.get(i);
                    // Getting Gateway Information
                    GatewayInfo gatewayInfo = requestGatewayInfo(system.gatewaySN, tempUnit);
                    if (gatewayInfo.returnStatus.equals(RequestStatus.SUCCESS)) {
                        gatewayInfo.preferedTemperatureUnit = tempUnit;
                    } else {
                        logger.warn("Failed to retrieve GatewayInfo for gatewaySN: {}", system.gatewaySN);
                        continue; // Skip this iteration if gateway info retrieval fails
                    }
                    // Getting alerts
                    GatewaysAlerts gatewaysAlerts = requestGatewaysAlerts(system.gatewaySN,
                            gatewayInfo.preferedLanguage, alertsCount);
                    // Handle alerts; initialize with default values if request fails
                    GatewaysAlerts updatedAlerts;
                    if (gatewaysAlerts.returnStatus.equals(RequestStatus.SUCCESS)) {
                        updatedAlerts = gatewaysAlerts;
                    } else {
                        logger.warn("Failed to retrieve GatewaysAlerts for gatewaySN: {}", system.gatewaySN);
                        updatedAlerts = new GatewaysAlerts(RequestStatus.UNKNOWN, new ArrayList<>());
                    }
                    // Getting Zones Status
                    ZonesStatus zonesStatus = requestZonesStatus(system.gatewaySN, tempUnit);
                    if (zonesStatus.returnStatus.equals(RequestStatus.SUCCESS)) {
                        for (ZoneStatus zone : zonesStatus.zoneStatus) {
                            zone.preferedTemperatureUnit = tempUnit;
                        }
                    } else {
                        logger.warn("Failed to retrieve ZonesStatus for gatewaySN: {}", system.gatewaySN);
                        zonesStatus = new ZonesStatus(); // Use the default constructor
                    }

                    // Re-create SystemInfo with the valid information
                    system = new SystemInfo(gatewayInfo, updatedAlerts, zonesStatus);
                    // Update the list with the modified system object if necessary
                    systemsInfo.systemInfo.set(i, system); // Ensure you update the list with the new SystemInfo
                                                           // instance
                }
            }
        } catch (TimeoutException e) {
            logger.info("Timeout on update");
        }
    }

    public @Nullable OwnerProfileInfo getOwnerProfileInfo() {
        return ownerProfileInfo;
    }

    public @Nullable BuildingsInfo getBuildingsInfo() {
        return buildingsInfo;
    }

    public SystemsInfo getSystemsInfo() {
        return systemsInfo;
    }

    public void setZoneHeatingPoint(ZoneStatus zoneStatus, Double setPoint) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandSetTStatInfo();
        ReqSetTStatInfo requestSetInfo = new ReqSetTStatInfo(zoneStatus);
        requestSetInfo.heatSetPoint = setPoint;
        apiAccess.doAuthenticatedPut(url, requestSetInfo);
        update();
    }

    public void setZoneCoolingPoint(ZoneStatus zoneStatus, Double setPoint) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandSetTStatInfo();
        ReqSetTStatInfo requestSetInfo = new ReqSetTStatInfo(zoneStatus);
        requestSetInfo.coolSetPoint = setPoint;
        apiAccess.doAuthenticatedPut(url, requestSetInfo);
        update();
    }

    public void setZoneAwayMode(ZoneStatus zoneStatus, Integer awayMode) throws TimeoutException {
        ReqSetAwayMode requestSetAway = new ReqSetAwayMode(zoneStatus);
        requestSetAway.awayMode = awayMode;
        String url = iComfortWiFiApiCommands.getCommandSetAwayModeNew(requestSetAway);
        // Assuming the ZonesStatus object should be returned by the API call
        ZonesStatus newZonesStatus = apiAccess.doAuthenticatedPut(url, requestSetAway, ZonesStatus.class);
        // Check if newZonesStatus is null
        if (newZonesStatus == null) {
            logger.warn("Received null ZonesStatus from API call for away mode.");
            return; // Exit the method early if the status is null
        }
        // Updating status for changed system
        for (int i = 0; i < systemsInfo.systemInfo.size(); i++) {
            SystemInfo system = systemsInfo.systemInfo.get(i);
            if (system.getZonesStatus().zoneStatus.get(0).gatewaySN.equals(zoneStatus.gatewaySN)) {
                // Re-create SystemInfo with the updated ZonesStatus
                system = new SystemInfo(system.getGatewayInfo(), system.getGatewaysAlerts(), newZonesStatus);
                // Update the list with the modified system object
                systemsInfo.systemInfo.set(i, system);
                break;
            }
        }
    }

    public void setZoneOperationMode(ZoneStatus zoneStatus, Integer operationMode) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandSetTStatInfo();
        ReqSetTStatInfo requestSetInfo = new ReqSetTStatInfo(zoneStatus);
        requestSetInfo.operationMode = operationMode;
        apiAccess.doAuthenticatedPut(url, requestSetInfo);
        update();
    }

    public void setZoneFanMode(ZoneStatus zoneStatus, Integer fanMode) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandSetTStatInfo();
        ReqSetTStatInfo requestSetInfo = new ReqSetTStatInfo(zoneStatus);
        requestSetInfo.fanMode = fanMode;
        apiAccess.doAuthenticatedPut(url, requestSetInfo);
        update();
    }

    private @Nullable OwnerProfileInfo requestUserAccount(String userName) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandGetOwnerProfileInfo(userName);
        return apiAccess.doAuthenticatedGet(url, OwnerProfileInfo.class);
    }

    private @Nullable BuildingsInfo requestBuildingsInfo(String userName) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandGetBuildingsInfo(userName);
        return apiAccess.doAuthenticatedGet(url, BuildingsInfo.class);
    }

    private SystemsInfo requestSystemsInfo(String userName) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandGetSystemsInfo(userName);
        SystemsInfo status = apiAccess.doAuthenticatedGet(url, SystemsInfo.class);
        // Check for null and handle appropriately
        if (status == null) {
            throw new IllegalStateException("SystemsInfo could not be retrieved.");
        }
        return status;
    }

    private ZonesStatus requestZonesStatus(String gatewaySN, TempUnits tempUnit) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandGetTStatInfoList(gatewaySN, tempUnit.getTempUnitsValue());
        ZonesStatus zonesStatus = apiAccess.doAuthenticatedGet(url, ZonesStatus.class);
        // Check for null and handle it
        if (zonesStatus == null) {
            throw new IllegalStateException("ZonesStatus could not be retrieved.");
        }
        return zonesStatus;
    }

    private GatewayInfo requestGatewayInfo(String gatewaySN, TempUnits tempUnit) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandGetGatewayInfo(gatewaySN, tempUnit.getTempUnitsValue());
        GatewayInfo gatewayInfo = apiAccess.doAuthenticatedGet(url, GatewayInfo.class);
        // Check if the returned GatewayInfo is null
        if (gatewayInfo == null) {
            // Handle the null case, for example, throwing an exception or logging a message
            throw new IllegalStateException("Failed to retrieve GatewayInfo for gateway: " + gatewaySN);
        }
        return gatewayInfo;
    }

    private GatewaysAlerts requestGatewaysAlerts(String gatewaySN, PreferedLanguage languageNbr, Integer count)
            throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandGetGatewaysAlerts(gatewaySN,
                languageNbr.getPreferedLanguageValue().toString(), count.toString());
        GatewaysAlerts gatewaysAlerts = apiAccess.doAuthenticatedGet(url, GatewaysAlerts.class);

        // Check if the returned GatewaysAlerts is null
        if (gatewaysAlerts == null) {
            // Handle the null case, e.g., throw an exception or log a message
            throw new IllegalStateException("Failed to retrieve GatewaysAlerts for gateway: " + gatewaySN);
        }

        return gatewaysAlerts;
    }

    // User name and Password from configuration validation
    private boolean validateUsername() {
        UserValidation validation = null;
        String basicAuthentication = null;
        try {
            Map<String, String> headers = new HashMap<>();
            // Prepare and encode username (if necessary) and password
            String authString = (configuration.userName.contains(" ") || configuration.userName.contains(":"))
                    ? URLEncoder.encode(configuration.userName, "UTF-8")
                    : configuration.userName;
            authString += ":" + configuration.password;
            // Encode to Base64
            String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));
            basicAuthentication = "Basic " + encodedAuthString; // This is the full auth string
            // Set headers
            headers.put("Authorization", basicAuthentication);
            headers.put("Accept",
                    "application/json, application/xml, text/json, text/x-json, text/javascript, text/xml");
            // Validate user
            String encodedUsername = URLEncoder.encode(configuration.userName, "UTF-8");
            validation = apiAccess.doRequest(HttpMethod.PUT,
                    iComfortWiFiApiCommands.getCommandValidateUser(encodedUsername, 0), headers, null,
                    "application/x-www-form-urlencoded", UserValidation.class);
        } catch (TimeoutException | UnsupportedEncodingException e) {
            logger.error("Error during user validation", e);
        }
        if (validation != null && validation.msgCode.equals(RequestStatus.SUCCESS)) {
            apiAccess.setUserCredentials(basicAuthentication); // Store full credentials
            return true;
        } else {
            apiAccess.setUserCredentials("");
            return false;
        }
    }
}
