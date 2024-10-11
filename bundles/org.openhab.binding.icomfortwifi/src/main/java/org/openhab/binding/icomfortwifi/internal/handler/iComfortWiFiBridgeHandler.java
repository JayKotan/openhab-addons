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
package org.openhab.binding.icomfortwifi.internal.handler;

// import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.icomfortwifi.internal.RunnableWithTimeout;
import org.openhab.binding.icomfortwifi.internal.api.iComfortWiFiApiClient;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.TempUnits;
import org.openhab.binding.icomfortwifi.internal.api.models.response.SystemInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.SystemsInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.ZoneStatus;
import org.openhab.binding.icomfortwifi.internal.configuration.iComfortWiFiBridgeConfiguration;
import org.openhab.binding.icomfortwifi.internal.utils.NonNullArrayList;
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
 * This bridge handler connects to iComfort WiFi and handles all the API requests. It pulls down the
 * updated data, polls the system and does all the co-ordination with the other handlers
 * to get the data updated to the correct things.
 *
 * @author Konstantin Panchenko - Initial contribution
 */
@NonNullByDefault
public class iComfortWiFiBridgeHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(iComfortWiFiBridgeHandler.class);
    private final HttpClient httpClient;
    private @NonNullByDefault({}) iComfortWiFiBridgeConfiguration configuration;
    private @NonNullByDefault({}) iComfortWiFiApiClient apiClient;
    private List<iComfortWiFiAccountStatusListener> listeners = new CopyOnWriteArrayList<>();
    // private SystemsInfo systemsInfo; // Make sure this is declared

    protected @Nullable ScheduledFuture<?> refreshTask;

    public iComfortWiFiBridgeHandler(Bridge thing, HttpClient httpClient) {
        super(thing);
        this.httpClient = httpClient;
        // this.systemsInfo = new SystemsInfo();
        // this.configuration = getThing().getConfiguration().as(iComfortWiFiBridgeConfiguration.class);
    }

    @Override
    public void initialize() {
        configuration = getConfigAs(iComfortWiFiBridgeConfiguration.class);

        if (checkConfig()) {
            try {
                apiClient = new iComfortWiFiApiClient(configuration, this.httpClient);
            } catch (Exception e) {
                logger.error("Could not start API client", e);
                updateAccountStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Could not create iComfortWiFi API client");
            }

            if (apiClient != null) {
                // Initialization can take a while, so kick it off on a separate thread
                scheduler.schedule(() -> {
                    if (apiClient.login()) {
                        startRefreshTask();
                    } else {
                        updateAccountStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                                "Authentication failed");
                    }
                }, 0, TimeUnit.SECONDS);
            }
        }
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
        return apiClient.getSystemsInfo();
    }

    public void setZoneOperationMode(ZoneStatus zoneStatus, Integer mode) {
        tryToCall(() -> apiClient.setZoneOperationMode(zoneStatus, mode));
        updateThings();
    }

    public void setZoneFanMode(ZoneStatus zoneStatus, Integer mode) {
        tryToCall(() -> apiClient.setZoneFanMode(zoneStatus, mode));
        updateThings();
    }

    public void setZoneAwayMode(ZoneStatus zoneStatus, Integer mode) {
        tryToCall(() -> apiClient.setZoneAwayMode(zoneStatus, mode));
        updateThings();
    }

    // Set zone cool point command
    public void setZoneCoolingPoint(ZoneStatus zoneStatus, double doubleValue) {
        // Validate that desired temperature in the range of gateway set points
        for (SystemInfo systemInfo : apiClient.getSystemsInfo().systemInfo) {
            if (systemInfo.gatewaySN.equals(zoneStatus.gatewaySN)) {
                if (doubleValue >= systemInfo.getGatewayInfo().coolSetPointLowLimit
                        && doubleValue <= systemInfo.getGatewayInfo().coolSetPointHighLimit) {
                    tryToCall(() -> apiClient.setZoneCoolingPoint(zoneStatus, doubleValue));
                    break;
                }
            }

        }
        updateThings();
    }

    // Set zone heat point command
    public void setZoneHeatingPoint(ZoneStatus zoneStatus, double doubleValue) {
        // Validate that desired temperature in the range of gateway set points
        for (SystemInfo systemInfo : apiClient.getSystemsInfo().systemInfo) {
            if (systemInfo.gatewaySN.equals(zoneStatus.gatewaySN)) {
                if (doubleValue >= systemInfo.getGatewayInfo().heatSetPointLowLimit
                        && doubleValue <= systemInfo.getGatewayInfo().heatSetPointHighLimit) {
                    tryToCall(() -> apiClient.setZoneHeatingPoint(zoneStatus, doubleValue));
                    break;
                }
            }
        }
        updateThings();
    }

    public void setAlternateTemperatureUnit(TempUnits tempUnit) {
        this.update(tempUnit);
    }

    public void addAccountStatusListener(iComfortWiFiAccountStatusListener listener) {
        listeners.add(listener);
        listener.accountStatusChanged(getThing().getStatus());
    }

    public void removeAccountStatusListener(iComfortWiFiAccountStatusListener listener) {
        listeners.remove(listener);
    }

    public void tryToCall(RunnableWithTimeout action) {
        try {
            action.run();
        } catch (TimeoutException e) {
            updateAccountStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Timeout on executing request");
        }
    }

    private void disposeApiClient() {
        if (apiClient != null) {
            apiClient.logout();
        }
        apiClient = null;
    }

    private void disposeRefreshTask() {
        ScheduledFuture<?> localRefreshTask = refreshTask;
        if (localRefreshTask != null) {
            localRefreshTask.cancel(true);
            // this.refreshTask = null;
        }
    }

    private boolean checkConfig() {
        String errorMessage = "";
        if (configuration.userName.isEmpty()) {
            errorMessage = "Username not configured";
        } else if (configuration.password.isEmpty()) {
            errorMessage = "Password not configured";
        } else {
            return true;
        }
        updateAccountStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, errorMessage);
        return false;
    }

    public void startRefreshTask() {
        disposeRefreshTask();

        refreshTask = scheduler.scheduleWithFixedDelay(this::update, 0, configuration.refreshInterval,
                TimeUnit.SECONDS);
    }

    private void update() {
        try {
            iComfortWiFiApiClient localApiCLient = apiClient;
            if (localApiCLient != null) {
                localApiCLient.update();
            }
            updateAccountStatus(ThingStatus.ONLINE);
            updateThings();
        } catch (Exception e) {
            updateAccountStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            logger.debug("Failed to update installation status", e);
        }
    }

    public void update(TempUnits tempUnit) {
        try {
            iComfortWiFiApiClient localApiCLient = apiClient;
            if (localApiCLient != null) {
                localApiCLient.update(tempUnit);
            }
            updateAccountStatus(ThingStatus.ONLINE);
            updateThings();
        } catch (Exception e) {
            updateAccountStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            logger.debug("Failed to update installation status", e);
        }
    }

    public void updateAccountStatus(ThingStatus newStatus) {
        updateAccountStatus(newStatus, ThingStatusDetail.NONE, null);
    }

    public void updateAccountStatus(ThingStatus newStatus, ThingStatusDetail detail, @Nullable String message) {
        // Prevent spamming the log file
        if (!newStatus.equals(getThing().getStatus())) {
            updateStatus(newStatus, detail, message);
            updateListeners(newStatus);
        }
    }

    public void updateListeners(ThingStatus status) {
        for (iComfortWiFiAccountStatusListener listener : listeners) {
            listener.accountStatusChanged(status);
        }
    }

    private void updateThings() {
        NonNullArrayList<SystemInfo> systemsInfoListFromApi = apiClient.getSystemsInfo().systemInfo; // This returns
                                                                                                     // NonNullArrayList
        // Lists to hold data for easier access
        List<ZoneStatus> zoneStatusList = new ArrayList<>();
        List<String> zoneIdList = new ArrayList<>();
        NonNullArrayList<String> tcsIdList = new NonNullArrayList<>(); // Ensure no null values in this list
        NonNullArrayList<ThingStatus> tcsStatusList = new NonNullArrayList<>(); // Ensure no null values in this list
        // Creating lookup tables
        for (SystemInfo systemInfo : systemsInfoListFromApi) {
            {
                NonNullArrayList<ZoneStatus> zonesStatusList = systemInfo.getZonesStatus().zoneStatus; // Assuming this
                                                                                                       // returns
                                                                                                       // NonNullArrayList
                for (ZoneStatus zoneStatus : zonesStatusList) {
                    {
                        zoneStatusList.add(zoneStatus);
                        zoneIdList.add(zoneStatus.getZoneID());
                        tcsIdList.add(systemInfo.gatewaySN); // Assuming the same index for TCS IDs
                    }
                }
            }
        }
        // Update the things by type, with pre-filtered info
        for (Thing thing : getThing().getThings()) {
            ThingHandler thingHandler = thing.getHandler();
            if (thingHandler instanceof iComfortWiFiHeatingZoneHandler) {
                iComfortWiFiHeatingZoneHandler zoneHandler = (iComfortWiFiHeatingZoneHandler) thingHandler;
                String zoneId = zoneHandler.getId();
                // Find corresponding index
                int index = zoneIdList.indexOf(zoneId);
                if (index >= 0) {
                    // Retrieve TCS ID safely
                    String tcsId = tcsIdList.get(index);
                    // Retrieve ThingStatus safely
                    ThingStatus tcsStatus = tcsStatusList.get(tcsIdList.indexOf(tcsId));
                    // Retrieve ZoneStatus safely without Optional
                    ZoneStatus zoneStatus = null;
                    for (ZoneStatus status : zoneStatusList) {
                        if (status.getZoneID().equals(zoneId)) {
                            zoneStatus = status;
                            break; // Exit loop once found
                        }
                    }
                    // Check if zoneStatus was found
                    if (zoneStatus == null) {
                        throw new IllegalStateException("No ZoneStatus found for Zone ID: " + zoneId);
                    }
                    // Now safely update the zoneHandler
                    zoneHandler.update(tcsStatus, zoneStatus);
                } else {
                    // Handle the case where no matching Zone ID was found
                    throw new IllegalStateException("No matching TCS ID found for Zone ID: " + zoneId);
                }
            }
        }
    }
}
