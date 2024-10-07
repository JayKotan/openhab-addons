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
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.binding.icomfortwifi.internal.api.models.request.ReqSetAwayMode;
import org.openhab.binding.icomfortwifi.internal.api.models.request.ReqSetTStatInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.BuildingsInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.PrefferedLanguage;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.RequestStatus;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.TempUnits;
import org.openhab.binding.icomfortwifi.internal.api.models.response.GatewayInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.GatewaysAlerts;
import org.openhab.binding.icomfortwifi.internal.api.models.response.Locations;
import org.openhab.binding.icomfortwifi.internal.api.models.response.LocationsStatus;
import org.openhab.binding.icomfortwifi.internal.api.models.response.OwnerProfileInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.SystemInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.SystemsInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.UserValidation;
import org.openhab.binding.icomfortwifi.internal.api.models.response.ZoneStatus;
import org.openhab.binding.icomfortwifi.internal.api.models.response.ZonesStatus;
import org.openhab.binding.icomfortwifi.internal.configuration.iComfortWiFiBridgeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

// import antlr.collections.List;

// import org.apache.commons.lang3.StringUtils;

/**
 * Implementation of the iComfortWiFi client api
 *
 * @author Konstantin Panchenko - Initial contribution
 *
 */
public class iComfortWiFiApiClient {

    public final Logger logger = LoggerFactory.getLogger(iComfortWiFiApiClient.class);
    public final HttpClient httpClient;
    public final iComfortWiFiBridgeConfiguration configuration;
    public final ApiAccess apiAccess;
    private final Gson gson = new Gson();
    private BuildingsInfo buildingsInfo = new BuildingsInfo();
    private OwnerProfileInfo ownerProfileInfo = new OwnerProfileInfo();
    private SystemsInfo systemsInfo = new SystemsInfo();
    private SystemInfo systemInfo = new SystemInfo();
    private Locations locations = new Locations();
    private @Nullable LocationsStatus locationsStatus;
    private final Integer alertsCount = 20;

    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public Locations getLocations() {
        return locations;
    }

    public @Nullable LocationsStatus getLocationsStatus() {
        return locationsStatus;
    }

    /**
     * Creates a new API client based on the V1 API interface
     *
     * @param configuration The configuration of the account to use
     * @throws Exception
     */
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
        systemsInfo = null;
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
            if (buildingsInfo == null || systemsInfo == null) {
                logger.debug("Failed to get system buildingsInfo");
                success = false;
            }
            if (systemsInfo == null) {
                logger.debug("Failed to get systemsInfo");
                success = false;
            }
            if (ownerProfileInfo == null || buildingsInfo == null || systemsInfo == null) {
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

    ///////// Added from EVOhome may not use
    // public Locations getInstallationInfo() {
    // return locations;
    // }

    // public @Nullable LocationsStatus getInstallationStatus() {
    // return locationsStatus;
    // }

    // public void setTcsMode(String tcsId, String mode) throws TimeoutException {
    // String url = String.format(
    // iComfortWiFiBindingConstants.URL_V2_BASE + iComfortWiFiBindingConstants.ZONE_OPERATION_MODE_CHANNEL,
    // tcsId);
    // Mode modeCommand = new ModeBuilder().setMode(mode).build();
    // apiAccess.doAuthenticatedPut(url, modeCommand);
    // }

    // public void setHeatingZoneOverride(String zoneId, double setPoint) throws TimeoutException {
    // HeatSetPoint setPointCommand = new HeatSetPointBuilder().setSetPoint(setPoint).build();
    // setHeatingZoneOverride(zoneId, setPointCommand);
    // }

    // public void cancelHeatingZoneOverride(String zoneId) throws TimeoutException {
    // HeatSetPoint setPointCommand = new HeatSetPointBuilder().setCancelSetPoint().build();
    // setHeatingZoneOverride(zoneId, setPointCommand);
    // }

    // public void setHeatingZoneOverride(String zoneId, HeatSetPoint heatSetPoint) throws TimeoutException {
    // String url = iComfortWiFiBindingConstants.URL_V2_BASE + iComfortWiFiBindingConstants.URL_V2_HEAT_SETPOINT;
    // url = String.format(url, zoneId);
    // apiAccess.doAuthenticatedPut(url, heatSetPoint);
    // }

    // public @Nullable UserAccount requestUserAccount() throws TimeoutException {
    // String url = iComfortWiFiBindingConstants.URL_V2_BASE + iComfortWiFiBindingConstants.URL_V2_ACCOUNT;
    // return apiAccess.doAuthenticatedGet(url, UserAccount.class);
    // }

    // public Locations requestLocations() throws TimeoutException {
    // Locations locations = null;
    // UserAccount localAccount = useraccount;
    // if (localAccount != null) {
    // String url = iComfortWiFiBindingConstants.URL_V2_BASE + iComfortWiFiBindingConstants.URL_V2_INSTALLATION_INFO;
    // url = String.format(url, localAccount.getUserId());

    // locations = apiAccess.doAuthenticatedGet(url, Locations.class);
    // }
    // return locations != null ? locations : new Locations();
    // }

    // public LocationsStatus requestLocationsStatus() throws TimeoutException {
    // LocationsStatus locationsStatus = new LocationsStatus();

    // for (Location location : locations) {
    // String url = iComfortWiFiBindingConstants.URL_V2_BASE + iComfortWiFiBindingConstants.URL_V2_LOCATION_STATUS;
    // url = String.format(url, location.getLocationInfo().getLocationId());
    // LocationStatus status = apiAccess.doAuthenticatedGet(url, LocationStatus.class);
    // locationsStatus.add(status);
    // }

    // return locationsStatus;
    // }
    ///////////////////////////////////////

    public void update() {
        try {
            if (systemsInfo.returnStatus.equals(RequestStatus.SUCCESS)) {
                for (int i = 0; i < systemsInfo.systemInfo.size(); i++) {
                    // This is to get initial Temperature Unit
                    if (systemsInfo.systemInfo.get(i).getGatewayInfo() == null) {
                        GatewayInfo gatewayInfo = requestGatewayInfo(systemsInfo.systemInfo.get(i).gatewaySN,
                                TempUnits.CELSIUS);
                        if (gatewayInfo.returnStatus.equals(RequestStatus.SUCCESS)) {
                            systemsInfo.systemInfo.get(i).setGetewayInfo(gatewayInfo);
                        } else {
                            continue;
                        }
                    }
                    // Getting Gateway Information
                    TempUnits prefTempCurrent = systemsInfo.systemInfo.get(i).getGatewayInfo().preferredTemperatureUnit;
                    GatewayInfo gatewayInfo = requestGatewayInfo(systemsInfo.systemInfo.get(i).gatewaySN,
                            prefTempCurrent);
                    if (gatewayInfo.returnStatus.equals(RequestStatus.SUCCESS)) {
                        // Ignoring preferred temperature unit provided by the system
                        // in case they were changed from Framework
                        gatewayInfo.preferredTemperatureUnit = prefTempCurrent;
                        systemsInfo.systemInfo.get(i).setGetewayInfo(gatewayInfo);
                    } else {
                        systemsInfo.systemInfo.get(i).setZonesStatus(null);
                        systemsInfo.systemInfo.get(i).setGetewayInfo(null);
                        continue;
                    }
                    // Getting alerts
                    GatewaysAlerts gatewaysAlerts = requestGatewaysAlerts(systemsInfo.systemInfo.get(i).gatewaySN,
                            systemsInfo.systemInfo.get(i).getGatewayInfo().prefferedLanguage, alertsCount);
                    if (gatewaysAlerts.returnStatus.equals(RequestStatus.SUCCESS)) {
                        systemsInfo.systemInfo.get(i).setGetewaysAlerts(gatewaysAlerts);
                    } else {
                        systemsInfo.systemInfo.get(i).setGetewaysAlerts(null);
                    }
                    // Getting Zones Status
                    ZonesStatus zonesStatus = requestZonesStatus(systemsInfo.systemInfo.get(i).gatewaySN,
                            prefTempCurrent);
                    if (zonesStatus.returnStatus.equals(RequestStatus.SUCCESS)) {
                        for (int j = 0; j < zonesStatus.zoneStatus.size(); j++) {
                            // Same as for gateway
                            zonesStatus.zoneStatus.get(i).preferredTemperatureUnit = prefTempCurrent;
                        }
                        systemsInfo.systemInfo.get(i).setZonesStatus(zonesStatus);
                    } else {
                        systemsInfo.systemInfo.get(i).setZonesStatus(null);
                        systemsInfo.systemInfo.get(i).setGetewayInfo(null);
                        continue;
                    }
                }
            }
        } catch (TimeoutException e) {
            logger.info("Timeout on update");
        }
    }

    // Request update with provided tempUnit
    public void update(TempUnits tempUnit) {
        try {
            if (systemsInfo.returnStatus.equals(RequestStatus.SUCCESS)) {
                for (int i = 0; i < systemsInfo.systemInfo.size(); i++) {
                    // Getting Gateway Information
                    GatewayInfo gatewayInfo = requestGatewayInfo(systemsInfo.systemInfo.get(i).gatewaySN, tempUnit);
                    if (gatewayInfo.returnStatus.equals(RequestStatus.SUCCESS)) {
                        gatewayInfo.preferredTemperatureUnit = tempUnit;
                        systemsInfo.systemInfo.get(i).setGetewayInfo(gatewayInfo);
                    } else {
                        systemsInfo.systemInfo.get(i).setGetewayInfo(null);
                        systemsInfo.systemInfo.get(i).setZonesStatus(null);
                        continue;
                    }
                    // Getting alerts
                    GatewaysAlerts gatewaysAlerts = requestGatewaysAlerts(systemsInfo.systemInfo.get(i).gatewaySN,
                            systemsInfo.systemInfo.get(i).getGatewayInfo().prefferedLanguage, alertsCount);
                    if (gatewaysAlerts.returnStatus.equals(RequestStatus.SUCCESS)) {
                        systemsInfo.systemInfo.get(i).setGetewaysAlerts(gatewaysAlerts);
                    } else {
                        systemsInfo.systemInfo.get(i).setGetewaysAlerts(null);
                    }
                    // Getting Zones Status
                    ZonesStatus zonesStatus = requestZonesStatus(systemsInfo.systemInfo.get(i).gatewaySN, tempUnit);
                    if (zonesStatus.returnStatus.equals(RequestStatus.SUCCESS)) {
                        for (int j = 0; j < zonesStatus.zoneStatus.size(); j++) {
                            zonesStatus.zoneStatus.get(i).preferredTemperatureUnit = tempUnit;
                        }
                        systemsInfo.systemInfo.get(i).setZonesStatus(zonesStatus);
                    } else {
                        systemsInfo.systemInfo.get(i).setZonesStatus(null);
                        systemsInfo.systemInfo.get(i).setGetewayInfo(null);
                        continue;
                    }
                }
            }
        } catch (TimeoutException e) {
            logger.info("Timeout on update");
        }
    }

    public OwnerProfileInfo getOwnerProfileInfo() {
        return ownerProfileInfo;
    }

    public BuildingsInfo getBuildingsInfo() {
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

        // Updating status for changed system
        for (int i = 0; i < systemsInfo.systemInfo.size(); i++) {
            if (systemsInfo.systemInfo.get(i).getZonesStatus().zoneStatus.get(0).gatewaySN
                    .equals(zoneStatus.gatewaySN)) {
                // Pass the correct ZonesStatus object
                systemsInfo.systemInfo.get(i).setZonesStatus(newZonesStatus);
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

    private OwnerProfileInfo requestUserAccount(String userName) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandGetOwnerProfileInfo(userName);
        return apiAccess.doAuthenticatedGet(url, OwnerProfileInfo.class);
    }

    private BuildingsInfo requestBuildingsInfo(String userName) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandGetBuildingsInfo(userName);
        return apiAccess.doAuthenticatedGet(url, BuildingsInfo.class);
    }

    // @SuppressWarnings("null")
    private SystemsInfo requestSystemsInfo(String userName) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandGetSystemsInfo(userName);
        return apiAccess.doAuthenticatedGet(url, SystemsInfo.class);
    }

    private ZonesStatus requestZonesStatus(String gatewaySN, TempUnits tempUnit) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandGetTStatInfoList(gatewaySN, tempUnit.getTempUnitsValue());
        return apiAccess.doAuthenticatedGet(url, ZonesStatus.class);
    }

    private GatewayInfo requestGatewayInfo(String gatewaySN, TempUnits tempUnit) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandGetGatewayInfo(gatewaySN, tempUnit.getTempUnitsValue());
        return apiAccess.doAuthenticatedGet(url, GatewayInfo.class);
    }

    private GatewaysAlerts requestGatewaysAlerts(String gatewaySN, PrefferedLanguage languageNbr, Integer count)
            throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandGetGatewaysAlerts(gatewaySN,
                languageNbr.getPrefferedLanguageValue().toString(), count.toString());
        return apiAccess.doAuthenticatedGet(url, GatewaysAlerts.class);
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
