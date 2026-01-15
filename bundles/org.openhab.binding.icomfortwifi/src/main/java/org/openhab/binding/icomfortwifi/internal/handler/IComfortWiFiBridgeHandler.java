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
package org.openhab.binding.icomfortwifi.internal.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.icomfortwifi.internal.RunnableWithTimeout;
import org.openhab.binding.icomfortwifi.internal.api.IComfortWiFiApiClient;
import org.openhab.binding.icomfortwifi.internal.configuration.IComfortWiFiBridgeConfiguration;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.TempUnits;
import org.openhab.binding.icomfortwifi.internal.dto.GatewayInfo;
import org.openhab.binding.icomfortwifi.internal.dto.GatewaysAlerts;
import org.openhab.binding.icomfortwifi.internal.dto.SystemInfo;
import org.openhab.binding.icomfortwifi.internal.dto.SystemsInfo;
import org.openhab.binding.icomfortwifi.internal.dto.ZoneStatus;
import org.openhab.binding.icomfortwifi.internal.dto.ZonesStatus;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - Updated for openHAB 5.x compliance
 */
@NonNullByDefault
public class IComfortWiFiBridgeHandler extends BaseBridgeHandler {

    private final Logger logger = Objects.requireNonNull(LoggerFactory.getLogger(IComfortWiFiBridgeHandler.class));
    private final HttpClient httpClient;

    private @Nullable IComfortWiFiBridgeConfiguration configuration;
    private @Nullable IComfortWiFiApiClient apiClient;

    private final List<IComfortWiFiAccountStatusListener> listeners = new CopyOnWriteArrayList<>();

    protected @Nullable ScheduledFuture<?> refreshTask;

    public IComfortWiFiBridgeHandler(Bridge thing, HttpClient httpClient) {
        super(thing);
        this.httpClient = httpClient;
    }

    @Override
    public void initialize() {
        try {
            if (!httpClient.isStarted()) {
                httpClient.start();
            }
        } catch (Exception e) {
            updateAccountStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "HTTP client failed");
            return;
        }

        IComfortWiFiBridgeConfiguration config = getConfigAs(IComfortWiFiBridgeConfiguration.class);
        this.configuration = config;

        if (checkConfig(config)) {
            try {
                apiClient = new IComfortWiFiApiClient(config, this.httpClient);
                final IComfortWiFiApiClient client = Objects.requireNonNull(apiClient);
                scheduler.schedule(() -> {
                    if (client.login()) {
                        client.update();
                        updateAccountStatus(ThingStatus.ONLINE);
                        startRefreshTask();
                    } else {
                        updateAccountStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                                "Authentication failed");
                    }
                }, 0, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.error("Failed to initialize iComfort API Client: {}", e.getMessage());
                updateAccountStatus(ThingStatus.OFFLINE, ThingStatusDetail.HANDLER_REGISTERING_ERROR,
                        "Client initialization failed");
            }
        }
    }

    public void addAccountStatusListener(IComfortWiFiAccountStatusListener listener) {
        listeners.add(listener);
        listener.accountStatusChanged(getThing().getStatus());
    }

    public void removeAccountStatusListener(IComfortWiFiAccountStatusListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void dispose() {
        disposeRefreshTask();
        disposeApiClient();
        listeners.clear();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    public SystemsInfo getiComfortWiFiSystemsInfo() {
        IComfortWiFiApiClient client = apiClient;
        return (client != null) ? client.getSystemsInfo() : new SystemsInfo();
    }

    // --- Action Methods ---

    public void setZoneOperationMode(ZoneStatus zoneStatus, Integer mode) {
        final IComfortWiFiApiClient client = this.apiClient;
        if (client != null) {
            tryToCall(() -> client.setZoneOperationMode(zoneStatus, mode));
            client.update(); // force fresh API read
            updateThings(); // push new values to handlers
        }
    }

    public void setZoneFanMode(ZoneStatus zoneStatus, Integer mode) {
        final IComfortWiFiApiClient client = this.apiClient;
        if (client != null) {
            tryToCall(() -> client.setZoneFanMode(zoneStatus, mode));
            client.update(); // force fresh API read
            updateThings(); // push new values to handlers
        }
    }

    public void setZoneAwayMode(ZoneStatus zone, int awayValue) {
        final IComfortWiFiApiClient client = this.apiClient;
        if (client != null) {
            try {
                client.setZoneAwayMode(zone, awayValue);
                update();
            } catch (TimeoutException e) {
                logger.warn("Error setting Away Mode for zone {}: {}", zone.zoneNumber, e.getMessage());
            }
        }
    }

    public void setZoneCoolingPoint(ZoneStatus zoneStatus, double doubleValue) {
        final IComfortWiFiApiClient client = this.apiClient;
        if (client == null) {
            return;
        }

        // Fix: Use the consistent lookup helper
        GatewayInfo info = findGatewayInfoForSN(zoneStatus.gatewaySN);

        if (info == null) {
            updateThings();
            return;
        } // <--- This brace was missing

        if (isWithinLimits(doubleValue, info.coolSetPointLowLimit, info.coolSetPointHighLimit, info)) {
            tryToCall(() -> client.setZoneCoolingPoint(zoneStatus, doubleValue));
        }
        update();
    }

    public void setZoneHeatingPoint(ZoneStatus zoneStatus, double doubleValue) {
        final IComfortWiFiApiClient client = this.apiClient;
        if (client == null) {
            return;
        }

        GatewayInfo info = findGatewayInfoForSN(zoneStatus.gatewaySN);
        if (info == null) {
            return;
        }

        if (isWithinLimits(doubleValue, info.heatSetPointLowLimit, info.heatSetPointHighLimit, info)) {
            tryToCall(() -> client.setZoneHeatingPoint(zoneStatus, doubleValue));
        }
        update();
    }

    /**
     * Helper to find GatewayInfo by Serial Number
     */
    private @Nullable GatewayInfo findGatewayInfoForSN(String gatewaySN) {
        final IComfortWiFiApiClient client = this.apiClient;
        if (client == null) {
            return null;
        }

        for (SystemInfo system : client.getSystemsInfo().getSystems()) {
            if (Objects.equals(system.gatewaySN, gatewaySN)) {
                return system.getGatewayInfo();
            }
        }
        return null;
    }

    private boolean isWithinLimits(double value, @Nullable Double low, @Nullable Double high, GatewayInfo info) {
        if (low == null || high == null) {
            return false;
        }

        TempUnits unit = info.getPreferredTemperatureUnit();
        boolean isMetric = (unit == TempUnits.CELSIUS);

        double checkValue = (isMetric && value > 40) ? (value - 32) * 5 / 9 : value;

        return checkValue >= low && checkValue <= high;
    }

    // --- Update Logic ---

    private void updateThings() {
        final IComfortWiFiApiClient client = this.apiClient;
        if (client == null) {
            return;
        }

        List<SystemInfo> localSystemInfos = client.getSystemsInfo().getSystems();

        Map<String, @Nullable ZoneStatus> idToZoneMap = new HashMap<>();
        Map<String, @Nullable GatewayInfo> idToGatewayMap = new HashMap<>();

        // Build lookup maps for zones and gateway info
        for (SystemInfo sys : localSystemInfos) {
            GatewayInfo gInfo = sys.getGatewayInfo();
            ZonesStatus zStatusContainer = sys.getZonesStatusOrNull();

            if (zStatusContainer != null) {
                List<ZoneStatus> list = zStatusContainer.getZoneStatus();
                if (list != null) {
                    for (ZoneStatus zStat : list) {
                        String zoneId = zStat.getZoneID();
                        idToZoneMap.put(zoneId, zStat);

                        if (gInfo != null) {
                            idToGatewayMap.put(zoneId, gInfo);
                        }
                    }
                }
            }
        }

        // Push updates to handlers
        for (Thing thing : getThing().getThings()) {
            ThingHandler handler = thing.getHandler();

            // ZONE HANDLER
            if (handler instanceof IComfortWiFiHeatingZoneHandler zoneHandler) {
                String zoneId = zoneHandler.getId();
                ZoneStatus zStatus = idToZoneMap.get(zoneId);
                GatewayInfo gInfo = idToGatewayMap.get(zoneId);

                if (zStatus != null) {
                    zoneHandler.update(getThing().getStatus(), zStatus, gInfo);
                }
            }

            // THERMOSTAT HANDLER (Gateway Alerts + Thermostat Alerts)
            if (handler instanceof IComfortWiFiTemperatureControlSystemHandler tcsHandler) {
                for (SystemInfo sys : localSystemInfos) {
                    if (thing.getUID().getId().equals(sys.gatewaySN)) {
                        GatewaysAlerts gwAlerts = sys.getGatewaysAlerts();
                        if (gwAlerts != null) {
                            tcsHandler.updateGatewayAlerts(gwAlerts);
                        }
                        break;
                    }
                }
            }
        }
    }

    public void startRefreshTask() {
        disposeRefreshTask();

        final IComfortWiFiBridgeConfiguration config = this.configuration;
        if (config == null) {
            return;
        }

        refreshTask = scheduler.scheduleWithFixedDelay(this::update, 0, config.refreshInterval, TimeUnit.SECONDS);
    }

    private void update() {
        final IComfortWiFiApiClient client = this.apiClient;
        if (client == null) {
            return;
        }

        try {
            client.update();
            updateAccountStatus(ThingStatus.ONLINE);
            updateThings();
        } catch (Exception e) {
            updateAccountStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }
    }

    public void updateAccountStatus(ThingStatus newStatus) {
        updateAccountStatus(newStatus, ThingStatusDetail.NONE, null);
    }

    public void updateAccountStatus(ThingStatus newStatus, ThingStatusDetail detail, @Nullable String message) {
        if (!newStatus.equals(getThing().getStatus())) {
            updateStatus(newStatus, detail, message);
            for (IComfortWiFiAccountStatusListener l : listeners) {
                l.accountStatusChanged(newStatus);
            }
        }
    }

    private boolean checkConfig(IComfortWiFiBridgeConfiguration config) {
        if (config.username.isEmpty() || config.password.isEmpty()) {
            updateAccountStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Credentials missing");
            return false;
        }
        return true;
    }

    private void disposeApiClient() {
        final IComfortWiFiApiClient client = this.apiClient;
        if (client != null) {
            client.logout();
            this.apiClient = null;
        }
    }

    private void disposeRefreshTask() {
        final ScheduledFuture<?> task = this.refreshTask;
        if (task != null) {
            task.cancel(true);
            this.refreshTask = null;
        }
    }

    public void tryToCall(RunnableWithTimeout action) {
        try {
            action.run();
        } catch (TimeoutException e) {
            updateAccountStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "API Timeout");
        }
    }
} // Final closing brace for the class
