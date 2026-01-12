/*
 * Copyright (c) 2010-2026 Contributors to the openHAB project
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.Checks;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.binding.icomfortwifi.internal.configuration.iComfortWiFiBridgeConfiguration;
import org.openhab.binding.icomfortwifi.internal.dto.BuildingsInfo;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.PreferredLanguage;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.RequestStatus;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.TempUnits;
import org.openhab.binding.icomfortwifi.internal.dto.GatewayInfo;
import org.openhab.binding.icomfortwifi.internal.dto.GatewaysAlerts;
import org.openhab.binding.icomfortwifi.internal.dto.OwnerProfileInfo;
import org.openhab.binding.icomfortwifi.internal.dto.ReqSetAwayMode;
import org.openhab.binding.icomfortwifi.internal.dto.ReqSetTStatInfo;
import org.openhab.binding.icomfortwifi.internal.dto.SystemInfo;
import org.openhab.binding.icomfortwifi.internal.dto.SystemsInfo;
import org.openhab.binding.icomfortwifi.internal.dto.UserValidation;
import org.openhab.binding.icomfortwifi.internal.dto.ZoneStatus;
import org.openhab.binding.icomfortwifi.internal.dto.ZonesStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * *
 * 
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Added @nullByDefault- updated Import section
 *
 */
@NonNullByDefault
public class iComfortWiFiApiClient {
    private final Logger logger = Objects.requireNonNull(LoggerFactory.getLogger(iComfortWiFiApiClient.class));

    private final HttpClient httpClient;
    private final iComfortWiFiBridgeConfiguration configuration;
    private final ApiAccess apiAccess;
    private @Nullable BuildingsInfo buildingsInfo = new BuildingsInfo();
    @SuppressWarnings("unused")
    private @Nullable OwnerProfileInfo ownerProfileInfo = new OwnerProfileInfo();

    @SuppressWarnings("unused")
    private SystemsInfo systemsInfo = new SystemsInfo();

    // noinspection FieldCanBeLocal
    @SuppressWarnings("unused")
    private final Integer alertsCount = 20;

    public iComfortWiFiApiClient(iComfortWiFiBridgeConfiguration configuration, HttpClient httpClient) {
        this.configuration = configuration;
        this.httpClient = httpClient;
        this.apiAccess = new ApiAccess(httpClient);
    }

    public void close() {
        this.apiAccess.setUserCredentials("");
        this.ownerProfileInfo = null;
        this.buildingsInfo = null;
        this.systemsInfo = new SystemsInfo();
        if (this.httpClient.isStarted()) {
            try {
                this.httpClient.stop();
            } catch (Exception var2) {
                this.logger.debug("Could not stop http client.", var2);
            }
        }
    }

    public boolean login() {
        boolean success = this.validateUsername();
        if (!success) {
            return false;
        }

        try {
            // Encode username safely (no checked exception)
            String encodedUser = Checks
                    .requireNonNull(URLEncoder.encode(this.configuration.username, StandardCharsets.UTF_8));

            // Retrieve all required account information
            this.ownerProfileInfo = this.requestUserAccount(encodedUser);
            this.buildingsInfo = this.requestBuildingsInfo(encodedUser);
            this.systemsInfo = this.requestSystemsInfo(encodedUser);

            // Post‑retrieval sanity check
            if (this.ownerProfileInfo == null || this.buildingsInfo == null) {
                this.logger.debug("Failed to get system basic information");
                success = false;
            }

        } catch (TimeoutException e) {
            this.logger.warn("Timeout during login information retrieval.");
            success = false;
        }

        return success;
    }

    public void logout() {
        this.close();
    }

    public void update() {
        CustomTypes.TempUnits unit = TempUnits.FAHRENHEIT;

        List<SystemInfo> systems = this.systemsInfo.systemInfo;
        if (!systems.isEmpty()) {
            SystemInfo firstSystem = Checks.requireNonNull(systems.get(0));

            GatewayInfo gw = firstSystem.getGatewayInfo();
            if (gw != null) {
                CustomTypes.TempUnits preferred = gw.preferredTemperatureUnit;
                if (preferred != null) {
                    unit = preferred;
                }
            }
        }

        this.update(unit);
    }

    public void update(CustomTypes.TempUnits tempUnit) {
        try {
            if (this.systemsInfo.returnStatus == RequestStatus.SUCCESS) {

                // Enhanced for-loop avoids all Iterator<> null-safety warnings
                for (SystemInfo system : this.systemsInfo.systemInfo) {

                    String sn = system.gatewaySN;
                    if (sn == null || sn.isEmpty()) {
                        continue;
                    }

                    GatewayInfo fetchedInfo = this.requestGatewayInfo(sn, tempUnit);
                    if (fetchedInfo != null && fetchedInfo.returnStatus == RequestStatus.SUCCESS) {
                        system.setGatewayInfo(fetchedInfo);

                        CustomTypes.PreferredLanguage prefLang = fetchedInfo.preferredLanguage;
                        if (prefLang == null) {
                            prefLang = PreferredLanguage.ENGLISH;
                        }

                        GatewaysAlerts alerts = this.requestGatewaysAlerts(sn, Checks.requireNonNull(prefLang),
                                this.alertsCount);
                        if (alerts != null && alerts.returnStatus == RequestStatus.SUCCESS) {
                            system.setGatewaysAlerts(alerts);
                        }

                        CustomTypes.TempUnits activeUnit = fetchedInfo.preferredTemperatureUnit;
                        if (activeUnit == null) {
                            activeUnit = tempUnit;
                        }

                        ZonesStatus zones = this.requestZonesStatus(sn, Checks.requireNonNull(activeUnit));
                        if (zones != null && zones.returnStatus == RequestStatus.SUCCESS) {
                            for (ZoneStatus zone : zones.zoneStatus) {
                                zone.preferredTemperatureUnit = activeUnit;
                            }
                            system.setZonesStatus(zones);
                        }

                    } else {
                        this.logger.debug("GatewayInfo for SN {} was null or failed; skipping sub-updates.", sn);
                    }
                }

            }
        } catch (TimeoutException e) {
            this.logger.info("Timeout on update");
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<@Nullable ZonesStatus> nullableZonesStatusClass() {
        return (Class<@Nullable ZonesStatus>) (Class<?>) ZonesStatus.class;
    }

    public void setZoneAwayMode(ZoneStatus zoneStatus, Integer awayMode) throws TimeoutException {
        ReqSetAwayMode requestSetAway = new ReqSetAwayMode(zoneStatus);
        requestSetAway.awayMode = awayMode;

        String url = iComfortWiFiApiCommands.getCommandSetAwayModeNew(requestSetAway);

        ZonesStatus newZonesStatus = this.apiAccess.doAuthenticatedPut(url, requestSetAway, nullableZonesStatusClass());

        if (newZonesStatus == null) {
            this.logger.warn("Failed to set Away Mode: API returned null ZonesStatus");
            return;
        }

        // Use enhanced for loop to avoid iterator generic/annotation mismatch
        for (SystemInfo system : this.systemsInfo.systemInfo) {
            Objects.requireNonNull(system);

            ZonesStatus currentZones = system.getZonesStatus();
            if (currentZones == null) {
                continue;
            }

            List<ZoneStatus> zoneList = Objects.requireNonNull(currentZones.zoneStatus);
            if (zoneList.isEmpty()) {
                continue;
            }

            ZoneStatus firstZone = Objects.requireNonNull(zoneList.get(0));

            String targetSN = zoneStatus.gatewaySN;

            if (targetSN.equals(firstZone.gatewaySN)) {
                system.setZonesStatus(newZonesStatus);
                // break; // optional
            }
        }
    }

    public void setZoneHeatingPoint(ZoneStatus zoneStatus, Double setPoint) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandSetTStatInfo();
        ReqSetTStatInfo requestSetInfo = new ReqSetTStatInfo(zoneStatus);
        requestSetInfo.heatSetPoint = setPoint;
        this.apiAccess.doAuthenticatedPut(url, requestSetInfo, "application/json");
        this.update();
    }

    public void setZoneCoolingPoint(ZoneStatus zoneStatus, Double setPoint) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandSetTStatInfo();
        ReqSetTStatInfo requestSetInfo = new ReqSetTStatInfo(zoneStatus);
        requestSetInfo.coolSetPoint = setPoint;
        this.apiAccess.doAuthenticatedPut(url, requestSetInfo, "application/json");
        this.update();
    }

    public void setZoneOperationMode(ZoneStatus zoneStatus, Integer operationMode) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandSetTStatInfo();
        ReqSetTStatInfo requestSetInfo = new ReqSetTStatInfo(zoneStatus);
        requestSetInfo.operationMode = operationMode;
        this.apiAccess.doAuthenticatedPut(url, requestSetInfo, "application/json");
        this.update();
    }

    public void setZoneFanMode(ZoneStatus zoneStatus, Integer fanMode) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandSetTStatInfo();
        ReqSetTStatInfo requestSetInfo = new ReqSetTStatInfo(zoneStatus);
        requestSetInfo.fanMode = fanMode;
        this.apiAccess.doAuthenticatedPut(url, requestSetInfo, "application/json");
        this.update();
    }

    @SuppressWarnings("unchecked")
    private static Class<@Nullable OwnerProfileInfo> nullableOwnerProfileInfoClass() {
        return (Class<@Nullable OwnerProfileInfo>) (Class<?>) OwnerProfileInfo.class;
    }

    private @Nullable OwnerProfileInfo requestUserAccount(String username) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandGetOwnerProfileInfo(username);

        return this.apiAccess.doAuthenticatedGet(url, nullableOwnerProfileInfoClass());
    }

    @SuppressWarnings("unchecked")
    private static Class<@Nullable BuildingsInfo> nullableBuildingsInfoClass() {
        return (Class<@Nullable BuildingsInfo>) (Class<?>) BuildingsInfo.class;
    }

    private @Nullable BuildingsInfo requestBuildingsInfo(String username) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandGetBuildingsInfo(username);

        return this.apiAccess.doAuthenticatedGet(url, nullableBuildingsInfoClass());
    }

    @SuppressWarnings("unchecked")
    private static Class<@Nullable SystemsInfo> nullableSystemsInfoClass() {
        return (Class<@Nullable SystemsInfo>) (Class<?>) SystemsInfo.class;
    }

    private SystemsInfo requestSystemsInfo(String username) throws TimeoutException {
        String url = iComfortWiFiApiCommands.getCommandGetSystemsInfo(username);

        SystemsInfo status = this.apiAccess.doAuthenticatedGet(url, nullableSystemsInfoClass());

        if (status == null) {
            throw new IllegalStateException("SystemsInfo could not be retrieved.");
        }

        return status;
    }

    private @Nullable ZonesStatus requestZonesStatus(String gatewaySN, CustomTypes.TempUnits tempUnit)
            throws TimeoutException {

        String url = iComfortWiFiApiCommands.getCommandGetTStatInfoList(gatewaySN,
                Objects.requireNonNull(tempUnit.getTempUnitsValue()));

        return this.apiAccess.doAuthenticatedGet(url, nullableZonesStatusClass());
    }

    @SuppressWarnings("unchecked")
    private static Class<@Nullable GatewayInfo> nullableGatewayInfoClass() {
        return (Class<@Nullable GatewayInfo>) (Class<?>) GatewayInfo.class;
    }

    private @Nullable GatewayInfo requestGatewayInfo(String gatewaySN, CustomTypes.TempUnits tempUnit)
            throws TimeoutException {

        String url = iComfortWiFiApiCommands.getCommandGetGatewayInfo(gatewaySN,
                Objects.requireNonNull(tempUnit.getTempUnitsValue()));

        return this.apiAccess.doAuthenticatedGet(url, nullableGatewayInfoClass());
    }

    @SuppressWarnings("unchecked")
    private static Class<@Nullable GatewaysAlerts> nullableGatewaysAlertsClass() {
        return (Class<@Nullable GatewaysAlerts>) (Class<?>) GatewaysAlerts.class;
    }

    private @Nullable GatewaysAlerts requestGatewaysAlerts(String gatewaySN, CustomTypes.PreferredLanguage languageNbr,
            Integer count) throws TimeoutException {

        String langStr = Objects.requireNonNull(String.valueOf(languageNbr.getPreferredLanguageValue()));

        String countStr = Objects.requireNonNull(String.valueOf(count));

        String url = iComfortWiFiApiCommands.getCommandGetGatewaysAlerts(gatewaySN, langStr, countStr);

        return this.apiAccess.doAuthenticatedGet(url, nullableGatewaysAlertsClass());
    }

    @SuppressWarnings("unchecked")
    private static Class<@Nullable UserValidation> nullableUserValidationClass() {
        return (Class<@Nullable UserValidation>) (Class<?>) UserValidation.class;
    }

    private boolean validateUsername() { // Changed to camelCase
        UserValidation validation = null;
        String basicAuthentication = "";

        try {
            // Ensure these match your configuration field names exactly
            String user = this.configuration.username;
            String pass = this.configuration.password;

            String rawAuthEncoded = Checks.requireNonNull(URLEncoder.encode(user, StandardCharsets.UTF_8));
            String authString = !user.contains(" ") && !user.contains(":") ? user : rawAuthEncoded;
            authString = authString + ":" + pass;

            String encoded = Checks
                    .requireNonNull(Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8)));

            basicAuthentication = "Basic " + encoded;

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", basicAuthentication);
            headers.put("Accept", "application/json, text/json");

            // Standardized to lowercase 'n' for the local variable
            String encodedUsername = Checks.requireNonNull(URLEncoder.encode(user, StandardCharsets.UTF_8));

            validation = this.apiAccess.doRequest(HttpMethod.PUT,
                    iComfortWiFiApiCommands.getCommandValidateUser(encodedUsername, 0), headers, "",
                    "application/x-www-form-urlencoded", nullableUserValidationClass());

        } catch (TimeoutException ex) {
            this.logger.error("Error during user validation", ex);
        }

        if (validation != null && validation.msgCode == RequestStatus.SUCCESS) {
            this.apiAccess.setUserCredentials(basicAuthentication);
            return true;
        } else {
            this.apiAccess.setUserCredentials("");
            return false;
        }
    }

    @SuppressWarnings("unused")
    public @Nullable OwnerProfileInfo getOwnerProfileInfo() {
        return this.ownerProfileInfo;
    }

    @SuppressWarnings("unused")
    public @Nullable BuildingsInfo getBuildingsInfo() {
        return this.buildingsInfo;
    }

    public SystemsInfo getSystemsInfo() {
        return this.systemsInfo;
    }
}
