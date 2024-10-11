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

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.icomfortwifi.internal.api.models.response.GatewayAlert;
import org.openhab.binding.icomfortwifi.internal.api.models.response.GatewayInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.GatewaysAlerts;
import org.openhab.binding.icomfortwifi.internal.api.models.response.SystemInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.ZonesStatus;
import org.openhab.binding.icomfortwifi.internal.iComfortWiFiBindingConstants;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import org.openhab.binding.icomfortwifi.internal.api.models.response.ZoneStatus;
/**
 * Handler for a temperature control system. Gets and sets global system mode.
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - updated imports and added logger to setDeviceProperties
 *
 */
@NonNullByDefault
public class iComfortWiFiTemperatureControlSystemHandler extends BaseiComfortWiFiHandler {
    private final Logger logger = LoggerFactory.getLogger(iComfortWiFiTemperatureControlSystemHandler.class);
    // public @Nullable SystemInfo systemInfo;
    private Integer alertNumber = 0;
    // private @Nullable GatewayStatus gatewayStatus;
    // private @Nullable TemperatureControlSystemStatus tcsStatus;
    private SystemInfo systemInfo;

    private SystemInfo fetchSystemInfo() {
        // Fetch or simulate the system information here
        // This is just an example, you would replace it with actual fetching logic
        GatewayInfo gatewayInfo = new GatewayInfo(); // Create your GatewayInfo instance
        GatewaysAlerts gatewaysAlerts = new GatewaysAlerts(); // Create your GatewaysAlerts instance
        ZonesStatus zonesStatus = new ZonesStatus(); // Create your ZonesStatus instance

        // Create a new SystemInfo object with the fetched data
        return new SystemInfo(gatewayInfo, gatewaysAlerts, zonesStatus);
    }

    public iComfortWiFiTemperatureControlSystemHandler(Thing thing) {
        super(thing);
        // Initialize systemsInfo to a new instance or fetch it if possible
        // this.systemsInfo = new SystemsInfo(); // Ensure SystemsInfo is initialized
        this.systemInfo = fetchSystemInfo();
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    public void update(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;

        if (!handleActiveFaults(systemInfo)) {
            updateiComfortWiFiThingStatus(ThingStatus.ONLINE);
            setDeviceProperties(systemInfo);
            // Declare currentAlert outside the if statement
            @Nullable
            GatewayAlert currentAlert = null;
            // Check if alertNumber is valid
            if (alertNumber >= 0 && alertNumber < systemInfo.getGatewaysAlerts().systemAlert.size()) {
                // Create a safe copy of the system alerts list
                ArrayList<GatewayAlert> alerts = new ArrayList<>(systemInfo.getGatewaysAlerts().systemAlert);
                currentAlert = alerts.get(alertNumber); // Assign currentAlert here
                updateState(iComfortWiFiBindingConstants.TCS_ALARM_DESCRIPTION_CHANNEL,
                        new StringType(currentAlert.alarmDescription));
                // Directly create a DecimalType from alarmNbr, assuming it cannot be null
                Integer alarmNbr = currentAlert.alarmNbr;
                updateState(iComfortWiFiBindingConstants.TCS_ALARM_NBR_CHANNEL, new DecimalType(alarmNbr)); // Remove
                                                                                                            // the null
                                                                                                            // check
                updateState(iComfortWiFiBindingConstants.TCS_ALARM_TYPE_CHANNEL,
                        new StringType(currentAlert.alarmType));
                // Set Alarm status
                updateState(iComfortWiFiBindingConstants.TCS_ALARM_STATUS_CHANNEL,
                        new StringType(currentAlert.status.toString()));
                updateState(iComfortWiFiBindingConstants.TCS_ALARM_DATE_TIME_SET_CHANNEL,
                        getAsDateTimeTypeOrNull(currentAlert.dateTimeSet));
            }
            updateState(iComfortWiFiBindingConstants.TCS_ALARM_ALERT_NUMBER, new DecimalType(alertNumber));
        } else {
            // Handle the case where currentAlert is null
            logger.warn("Current alert is null for alert number: {}", alertNumber);
        }
    }

    protected void setDeviceProperties(SystemInfo systemInfo) {
        try {
            // Change the type to @NonNull to match the source
            Map<String, String> properties = editProperties();
            properties.put(iComfortWiFiBindingConstants.TCS_PROPERTY_SYSTEM_NAME, systemInfo.systemName);
            properties.put(iComfortWiFiBindingConstants.TCS_PROPERTY_GATEWAY_SN, systemInfo.gatewaySN);
            properties.put(iComfortWiFiBindingConstants.TCS_PROPERTY_FIRMWARE_VERSION, systemInfo.firmwareVersion);
            updateProperties(properties);
        } catch (Exception e) {
            logger.error("Error setting device properties", e); // Add error handling if needed
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Executing command {}", command.toString());
        if (command == RefreshType.REFRESH) {
            update(systemInfo);
        } else if (channelUID.getId().equals(iComfortWiFiBindingConstants.TCS_ALARM_ALERT_NUMBER)
                && command instanceof DecimalType) {
            alertNumber = ((DecimalType) command).intValue();
            update(systemInfo);
        }
    }

    // private SystemsInfo fetchSystemsInfo() {
    // return new SystemsInfo(); // Or however you obtain it
    // }

    private boolean handleActiveFaults(SystemInfo systemInfo) { // Not handling at the moment, don't know values for
                                                                // status
        if (systemInfo.hasActiveFaults()) {
            updateiComfortWiFiThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    systemInfo.getActiveFault());
            return true;
        }
        return false;
    }
}
