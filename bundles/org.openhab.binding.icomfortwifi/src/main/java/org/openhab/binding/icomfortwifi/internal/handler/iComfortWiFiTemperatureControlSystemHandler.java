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

import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.icomfortwifi.internal.api.models.response.SystemInfo;
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
public class iComfortWiFiTemperatureControlSystemHandler extends BaseiComfortWiFiHandler {
    public final Logger logger = LoggerFactory.getLogger(iComfortWiFiTemperatureControlSystemHandler.class);
    public SystemInfo systemInfo;
    public Integer alertNumber = 0;
    // private @Nullable GatewayStatus gatewayStatus;
    // private @Nullable TemperatureControlSystemStatus tcsStatus;

    public iComfortWiFiTemperatureControlSystemHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    public void update(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
        if (systemInfo == null) {
            updateiComfortWiFiThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Status not found, check the display id");
        } else if (handleActiveFaults(systemInfo) == false) {
            updateiComfortWiFiThingStatus(ThingStatus.ONLINE);
            setDeviceProperties(systemInfo);

            updateState(iComfortWiFiBindingConstants.TCS_ALARM_DESCRIPTION_CHANNEL, new StringType(
                    systemInfo.getGatewaysAlerts().systemAlert.get(alertNumber).alarmDescription.toString()));

            updateState(iComfortWiFiBindingConstants.TCS_ALARM_NBR_CHANNEL,
                    new DecimalType(systemInfo.getGatewaysAlerts().systemAlert.get(alertNumber).alarmNbr));

            updateState(iComfortWiFiBindingConstants.TCS_ALARM_TYPE_CHANNEL,
                    new StringType(systemInfo.getGatewaysAlerts().systemAlert.get(alertNumber).alarmType.toString()));

            // Set Alarm status
            updateState(iComfortWiFiBindingConstants.TCS_ALARM_STATUS_CHANNEL,
                    new StringType(systemInfo.getGatewaysAlerts().systemAlert.get(alertNumber).status.toString()));

            updateState(iComfortWiFiBindingConstants.TCS_ALARM_DATE_TIME_SET_CHANNEL,
                    getAsDateTimeTypeOrNull(systemInfo.getGatewaysAlerts().systemAlert.get(alertNumber).dateTimeSet));

            updateState(iComfortWiFiBindingConstants.TCS_ALARM_ALERT_NUMBER, new DecimalType(alertNumber));
        }
    }

    protected void setDeviceProperties(SystemInfo systemInfo) {
        try {
            if (systemInfo == null) {
                logger.debug("Cannot refresh device (empty response or handler has no bridge)");
                return;
            }
            // Change the type to @NonNull to match the source
            Map<@NonNull String, @NonNull String> properties = editProperties();
            properties.put(iComfortWiFiBindingConstants.TCS_PROPERTY_SYSTEM_NAME, systemInfo.systemName);
            properties.put(iComfortWiFiBindingConstants.TCS_PROPERTY_GATEWAY_SN, systemInfo.gatewaySN);
            properties.put(iComfortWiFiBindingConstants.TCS_PROPERTY_FIRMWARE_VERSION, systemInfo.firmwareVersion);
            updateProperties(properties);
        } catch (Exception e) {
            logger.error("Error setting device properties", e); // Add error handling if needed
        }
    }

    @Override
    public void handleCommand(@NonNull ChannelUID channelUID, @NonNull Command command) {
        logger.debug("Entering TCS Handler for Gateway {}", systemInfo.gatewaySN);
        logger.debug("Executing command {}", command.toString());
        if (command == RefreshType.REFRESH) {
            update(systemInfo);
        } else if (channelUID.getId().equals(iComfortWiFiBindingConstants.TCS_ALARM_ALERT_NUMBER)
                && command instanceof DecimalType) {
            // Handling Alert Number to display
            alertNumber = ((DecimalType) command).intValue();
            update(systemInfo);
        }
    }

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
