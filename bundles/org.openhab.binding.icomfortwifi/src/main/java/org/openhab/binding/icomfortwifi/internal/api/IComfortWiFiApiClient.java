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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.Checks;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.binding.icomfortwifi.internal.configuration.IComfortWiFiBridgeConfiguration;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.PreferredLanguage;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.RequestStatus;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.TempUnits;
import org.openhab.binding.icomfortwifi.internal.dto.GatewayInfo;
import org.openhab.binding.icomfortwifi.internal.dto.GatewaysAlerts;
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
 * iComfort WiFi API client.
 *
 * Handles authentication, polling, and write operations against the Lennox
 * iComfort cloud API.
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - openHAB 5.2 cleanup and thermostat alerts
 */
@NonNullByDefault
public class IComfortWiFiApiClient {

    private final Logger logger = Objects.requireNonNull(LoggerFactory.getLogger(IComfortWiFiApiClient.class));
    private final HttpClient httpClient;
    private final IComfortWiFiBridgeConfiguration configuration;
    private final ApiAccess apiAccess;

    private SystemsInfo systemsInfo = new SystemsInfo();

    // Number of alerts to request for gateway and thermostat
    private final Integer alertsCount = 20;

    public IComfortWiFiApiClient(IComfortWiFiBridgeConfiguration configuration, HttpClient httpClient) {
        this.configuration = configuration;
        this.httpClient = httpClient;
        this.apiAccess = new ApiAccess(httpClient);
    }

    // ---------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------

    public void close() {
        try {
            this.apiAccess.setUserCredentials("");
        } catch (Exception e) {
            logger.debug("Error clearing API credentials", e);
        }
        try {
            if (this.httpClient.isStarted()) {
                this.httpClient.stop();
            }
        } catch (Exception e) {
            this.logger.debug("Could not stop http client.", e);
        }
    }

    public void logout() {
        close();
    }

    public boolean login() {
        boolean success = this.validateUsername();
        if (!success) {
            return false;
        }
        try {
            String encodedUser = Checks
                    .requireNonNull(URLEncoder.encode(this.configuration.username, StandardCharsets.UTF_8));
            this.systemsInfo = this.requestSystemsInfo(encodedUser);
        } catch (TimeoutException e) {
            this.logger.warn("Timeout during login information retrieval.", e);
            success = false;
        } catch (Exception e) {
            this.logger.warn("Unexpected error during login", e);
            success = false;
        }
        return success;
    }

    public SystemsInfo getSystemsInfo() {
        return this.systemsInfo;
    }

    // ---------------------------------------------------------------------
    // Update Logic
    // ---------------------------------------------------------------------

    public void update() {
        CustomTypes.TempUnits unit = TempUnits.FAHRENHEIT;
        List<SystemInfo> systems = this.systemsInfo.getSystems();

        if (!systems.isEmpty()) {
            SystemInfo firstSystem = systems.get(0);
            GatewayInfo gw = firstSystem.getGatewayInfo();
            if (gw != null && gw.preferredTemperatureUnit != null) {
                unit = Objects.requireNonNull(gw.preferredTemperatureUnit);
            }
        }
        this.update(unit);
    }

    public void update(TempUnits tempUnit) {
        try {
            if (this.systemsInfo.returnStatus != RequestStatus.SUCCESS) {
                return;
            }
            for (SystemInfo system : this.systemsInfo.getSystems()) {
                String sn = system.gatewaySN;
                if (sn == null || sn.isEmpty()) {
                    continue;
                }
                GatewayInfo fetchedInfo = requestGatewayInfo(sn, tempUnit);
                if (fetchedInfo == null || fetchedInfo.returnStatus != RequestStatus.SUCCESS) {
                    continue;
                }
                system.setGatewayInfo(fetchedInfo);
                PreferredLanguage prefLang = fetchedInfo.preferredLanguage;
                if (prefLang == null) {
                    prefLang = PreferredLanguage.ENGLISH;
                }
                GatewaysAlerts gwAlerts = requestGatewaysAlerts(sn, prefLang, this.alertsCount);
                if (gwAlerts != null) {
                    system.setGatewaysAlerts(gwAlerts);
                }
                TempUnits preferred = fetchedInfo.preferredTemperatureUnit;
                TempUnits activeUnit = (preferred != null) ? preferred : tempUnit;
                ZonesStatus zones = requestZonesStatus(sn, activeUnit);
                if (zones != null && zones.returnStatus == RequestStatus.SUCCESS) {
                    system.setZonesStatus(zones);
                }
            }
        } catch (TimeoutException e) {
            this.logger.info("Timeout during API update", e);
        }
    }
    // ---------------------------------------------------------------------
    // Zone write operations
    // ---------------------------------------------------------------------

    public void setZoneAwayMode(ZoneStatus zoneStatus, Integer awayMode) throws TimeoutException {
        ReqSetAwayMode requestSetAway = new ReqSetAwayMode(zoneStatus);
        requestSetAway.awayMode = awayMode;
        String url = IComfortWiFiApiCommands.getCommandSetAwayModeNew(requestSetAway);
        ZonesStatus newZonesStatus = this.apiAccess.doAuthenticatedPut(url, requestSetAway, nullableZonesStatusClass());
        if (newZonesStatus == null) {
            this.logger.warn("Failed to set Away Mode: API returned null ZonesStatus");
            return;
        }

        for (SystemInfo system : this.systemsInfo.getSystems()) {
            ZonesStatus currentZones = system.getZonesStatusOrNull();
            List<ZoneStatus> zones = (currentZones != null) ? currentZones.zoneStatus : null;
            if (zones == null || zones.isEmpty()) {
                continue;
            }

            ZoneStatus firstZone = Objects.requireNonNull(zones.get(0));
            String targetSN = zoneStatus.gatewaySN;
            String firstZoneSN = firstZone.gatewaySN;
            if (targetSN.equals(firstZoneSN)) {
                system.setZonesStatus(newZonesStatus);
            }
        }
    }

    public void setZoneHeatingPoint(ZoneStatus zoneStatus, Double setPoint) throws TimeoutException {
        String url = IComfortWiFiApiCommands.getCommandSetTStatInfo();
        ReqSetTStatInfo requestSetInfo = new ReqSetTStatInfo(zoneStatus);
        requestSetInfo.heatSetPoint = setPoint;
        this.apiAccess.doAuthenticatedPut(url, requestSetInfo, "application/json");
        update();
    }

    public void setZoneCoolingPoint(ZoneStatus zoneStatus, Double setPoint) throws TimeoutException {
        String url = IComfortWiFiApiCommands.getCommandSetTStatInfo();
        ReqSetTStatInfo requestSetInfo = new ReqSetTStatInfo(zoneStatus);
        requestSetInfo.coolSetPoint = setPoint;
        this.apiAccess.doAuthenticatedPut(url, requestSetInfo, "application/json");
        update();
    }

    public void setZoneOperationMode(ZoneStatus zoneStatus, Integer operationMode) throws TimeoutException {
        String url = IComfortWiFiApiCommands.getCommandSetTStatInfo();
        ReqSetTStatInfo requestSetInfo = new ReqSetTStatInfo(zoneStatus);
        requestSetInfo.operationMode = operationMode;
        this.apiAccess.doAuthenticatedPut(url, requestSetInfo, "application/json");
        update();
    }

    public void setZoneFanMode(ZoneStatus zoneStatus, Integer fanMode) throws TimeoutException {
        String url = IComfortWiFiApiCommands.getCommandSetTStatInfo();
        ReqSetTStatInfo requestSetInfo = new ReqSetTStatInfo(zoneStatus);
        requestSetInfo.fanMode = fanMode;
        this.apiAccess.doAuthenticatedPut(url, requestSetInfo, "application/json");
        update();
    }

    // ---------------------------------------------------------------------
    // Low-level request helpers
    // ---------------------------------------------------------------------
    @SuppressWarnings("null")
    public static Map<Integer, String> parseScheduleNameString(String raw) {
        Map<Integer, String> map = new LinkedHashMap<>();
        if (raw == null || raw.trim().isEmpty()) {
            return map;
        }
        raw = raw.trim();
        if (raw.startsWith("\"") && raw.endsWith("\"")) {
            raw = raw.substring(1, raw.length() - 1);
        }

        String[] pairs = raw.split("\\^");
        for (String pair : pairs) {
            String[] parts = pair.split("\\|", 2);
            if (parts.length == 2) {
                try {
                    int idx = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    if (!name.isEmpty()) {
                        map.put(idx, name);
                    }
                } catch (NumberFormatException e) {
                    // ignore malformed entries
                }
            }
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static Class<@Nullable ZonesStatus> nullableZonesStatusClass() {
        return (Class<@Nullable ZonesStatus>) (Class<?>) ZonesStatus.class;
    }

    @SuppressWarnings("unchecked")
    private static Class<@Nullable SystemsInfo> nullableSystemsInfoClass() {
        return (Class<@Nullable SystemsInfo>) (Class<?>) SystemsInfo.class;
    }

    @SuppressWarnings("unchecked")
    private static Class<@Nullable GatewayInfo> nullableGatewayInfoClass() {
        return (Class<@Nullable GatewayInfo>) (Class<?>) GatewayInfo.class;
    }

    @SuppressWarnings("unchecked")
    private static Class<@Nullable GatewaysAlerts> nullableGatewaysAlertsClass() {
        return (Class<@Nullable GatewaysAlerts>) (Class<?>) GatewaysAlerts.class;
    }

    @SuppressWarnings("unchecked")
    private static Class<@Nullable UserValidation> nullableUserValidationClass() {
        return (Class<@Nullable UserValidation>) (Class<?>) UserValidation.class;
    }

    private SystemsInfo requestSystemsInfo(String username) throws TimeoutException {
        String url = IComfortWiFiApiCommands.getCommandGetSystemsInfo(username);
        SystemsInfo status = this.apiAccess.doAuthenticatedGet(url, nullableSystemsInfoClass());
        if (status == null) {
            throw new IllegalStateException("SystemsInfo could not be retrieved.");
        }
        return status;
    }

    private @Nullable ZonesStatus requestZonesStatus(String gatewaySN, TempUnits tempUnit) throws TimeoutException {
        String url = IComfortWiFiApiCommands.getCommandGetTStatInfoList(gatewaySN,
                Objects.requireNonNull(tempUnit.getTempUnitsValue()));
        return this.apiAccess.doAuthenticatedGet(url, nullableZonesStatusClass());
    }

    private @Nullable GatewayInfo requestGatewayInfo(String gatewaySN, TempUnits tempUnit) throws TimeoutException {
        String url = IComfortWiFiApiCommands.getCommandGetGatewayInfo(gatewaySN,
                Objects.requireNonNull(tempUnit.getTempUnitsValue()));
        return this.apiAccess.doAuthenticatedGet(url, nullableGatewayInfoClass());
    }

    private @Nullable GatewaysAlerts requestGatewaysAlerts(String gatewaySN, PreferredLanguage languageNbr,
            Integer count) throws TimeoutException {
        String langStr = Objects.requireNonNull(String.valueOf(languageNbr.getPreferredLanguageValue()));
        String countStr = Objects.requireNonNull(String.valueOf(count));
        String url = IComfortWiFiApiCommands.getCommandGetGatewaysAlerts(gatewaySN, langStr, countStr);
        return this.apiAccess.doAuthenticatedGet(url, nullableGatewaysAlertsClass());
    }

    // ---------------------------------------------------------------------
    // Authentication
    // ---------------------------------------------------------------------

    private boolean validateUsername() {
        UserValidation validation = null;
        String basicAuthentication = "";
        try {
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
            String encodedUsername = Checks.requireNonNull(URLEncoder.encode(user, StandardCharsets.UTF_8));
            validation = this.apiAccess.doRequest(HttpMethod.PUT,
                    IComfortWiFiApiCommands.getCommandValidateUser(encodedUsername, 0), headers, "",
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
}
