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

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes;
import org.openhab.binding.icomfortwifi.internal.dto.GatewayAlert;
import org.openhab.binding.icomfortwifi.internal.dto.GatewaysAlerts;
import org.openhab.binding.icomfortwifi.internal.dto.SystemInfo;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * iComfortWiFiTemperatureControlSystemHandler
 *
 * - Uses DTO getters (no direct field access)
 * - Provides updateGatewayAlerts(...)
 * - Parses TStatAlert date strings locally and delegates to BaseiComfortWiFiHandler's Date overload
 */
@NonNullByDefault
public class iComfortWiFiTemperatureControlSystemHandler extends BaseiComfortWiFiHandler {

    private final Logger logger = Objects
            .requireNonNull(LoggerFactory.getLogger(iComfortWiFiTemperatureControlSystemHandler.class));

    public @Nullable SystemInfo systemInfo;
    public Integer alertNumber = 0;

    public iComfortWiFiTemperatureControlSystemHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    /**
     * Primary update entry called by the bridge or on refresh.
     */
    public void update(@Nullable SystemInfo systemInfo) {
        this.systemInfo = systemInfo;

        if (systemInfo == null) {
            updateiComfortWiFiThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Status not found, check the display id");
            return;
        }

        if (!handleActiveFaults(systemInfo)) {
            updateiComfortWiFiThingStatus(ThingStatus.ONLINE);
            setDeviceProperties(systemInfo);

            // Gateway-level alerts from SystemInfo
            GatewaysAlerts gatewaysAlerts = systemInfo.getGatewaysAlerts();
            if (gatewaysAlerts == null) {
                // Clear gateway alert channels when no gateway alerts object is present
                updateState("alerts-and-reminders#alarm-nbr", UnDefType.UNDEF);
                updateState("alerts-and-reminders#alarm-description", UnDefType.UNDEF);
                updateState("alerts-and-reminders#alarm-type", UnDefType.UNDEF);
                updateState("alerts-and-reminders#alarm-status", UnDefType.UNDEF);
                updateState("alerts-and-reminders#date-time-set", UnDefType.UNDEF);
            } else {
                List<GatewayAlert> alerts = gatewaysAlerts.getAlerts();
                if (alerts == null || alerts.isEmpty() || alertNumber < 0 || alertNumber >= alerts.size()) {
                    // Clear gateway alert channels when list is empty or alertNumber is out of range
                    updateState("alerts-and-reminders#alarm-nbr", UnDefType.UNDEF);
                    updateState("alerts-and-reminders#alarm-description", UnDefType.UNDEF);
                    updateState("alerts-and-reminders#alarm-type", UnDefType.UNDEF);
                    updateState("alerts-and-reminders#alarm-status", UnDefType.UNDEF);
                    updateState("alerts-and-reminders#date-time-set", UnDefType.UNDEF);
                } else {
                    GatewayAlert alert = Objects.requireNonNull(alerts.get(alertNumber));

                    // Alarm number (GatewayAlert uses Integer)
                    Integer alarmNbrLocal = alert.getAlarmNbr();
                    if (alarmNbrLocal == null) {
                        updateState("alerts-and-reminders#alarm-nbr", UnDefType.UNDEF);
                    } else {
                        updateState("alerts-and-reminders#alarm-nbr", new DecimalType(alarmNbrLocal));
                    }

                    // Status (GatewayAlert)
                    CustomTypes.AlertStatus gatewayAlertStatus = alert.getStatus();
                    if (gatewayAlertStatus == null) {
                        updateState("alerts-and-reminders#alarm-status", UnDefType.UNDEF);
                    } else {
                        updateState("alerts-and-reminders#alarm-status", new StringType(gatewayAlertStatus.toString()));
                    }

                    // Type & description
                    String type = alert.getAlarmType();
                    String desc = alert.getAlarmDescription();

                    updateState("alerts-and-reminders#alarm-description",
                            new StringType(desc != null ? desc : "No description"));
                    updateState("alerts-and-reminders#alarm-type", new StringType(type != null ? type : "Unknown"));

                    // Date (GatewayAlert.getDateTimeSet() returns java.util.Date)
                    updateState("alerts-and-reminders#date-time-set", getAsDateTimeTypeOrNull(alert.getDateTimeSet()));
                }
            }
        }
    }

    /**
     * Called by the bridge when gateway-level alerts are available.
     */
    public void updateGatewayAlerts(@Nullable GatewaysAlerts alerts) {
        List<GatewayAlert> list = (alerts != null) ? alerts.getAlerts() : null;

        if (list == null || list.isEmpty()) {
            updateState("alerts-and-reminders#alarm-nbr", UnDefType.UNDEF);
            updateState("alerts-and-reminders#alarm-description", UnDefType.UNDEF);
            updateState("alerts-and-reminders#alarm-type", UnDefType.UNDEF);
            updateState("alerts-and-reminders#alarm-status", UnDefType.UNDEF);
            updateState("alerts-and-reminders#date-time-set", UnDefType.UNDEF);
            updateState("alerts-and-reminders#date-time-reset", UnDefType.UNDEF);
            return;
        }

        // Always use the first alert (latest) instead of alertNumber
        GatewayAlert alert = list.get(0);

        Integer alarmNbrLocal = alert.getAlarmNbr();
        updateState("alerts-and-reminders#alarm-nbr",
                (alarmNbrLocal != null) ? new DecimalType(alarmNbrLocal) : UnDefType.UNDEF);

        String desc = alert.getAlarmDescription();
        updateState("alerts-and-reminders#alarm-description", new StringType(desc != null ? desc : "No description"));

        String type = alert.getAlarmType();
        updateState("alerts-and-reminders#alarm-type", new StringType(type != null ? type : "Unknown"));

        CustomTypes.AlertStatus gatewayAlertStatus = alert.getStatus();
        updateState("alerts-and-reminders#alarm-status",
                (gatewayAlertStatus != null) ? new StringType(gatewayAlertStatus.toString()) : UnDefType.UNDEF);

        updateState("alerts-and-reminders#date-time-reset", getAsDateTimeTypeOrNull(alert.getDateTimeReset()));

        updateState("alerts-and-reminders#date-time-set", getAsDateTimeTypeOrNull(alert.getDateTimeSet()));
    }

    /**
     * Called by the bridge when thermostat-specific alerts are available.
     * TStatAlert.dateTimeSet is a String in the DTO, so parse it here and call the Date overload.
     */

    protected void setDeviceProperties(SystemInfo info) {
        try {
            Map<String, String> properties = editProperties();

            String sn = Objects.requireNonNullElse(info.gatewaySN, "UNKNOWN");
            String systemName = Objects.requireNonNullElse(info.systemName, "UNKNOWN");
            String firmware = Objects.requireNonNullElse(info.firmwareVersion, "UNKNOWN");

            properties.put("systemName", systemName);
            properties.put("gatewaySerialNumber", sn);
            properties.put("firmwareVersion", firmware);

            updateProperties(properties);
        } catch (Exception e) {
            logger.error("Error setting device properties: {}", e.getMessage());
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        SystemInfo currentInfo = this.systemInfo;
        if (currentInfo == null) {
            return;
        }

        if (command == RefreshType.REFRESH) {
            update(currentInfo);
        }
    }

    private boolean handleActiveFaults(SystemInfo info) {
        // We are disabling the 'force offline' logic.
        // This ensures the Thing stays ONLINE so you can still control the temperature
        // even if the system is reporting a background fault like 'Missing Zoning Controller'.

        if (info.hasActiveFaults()) {
            // We log the fault so you can still see it in the logs,
            // but we don't take the Thing offline.
            logger.debug("System reporting active fault: {}", info.getActiveFault());
        }

        return false;
    }
}
