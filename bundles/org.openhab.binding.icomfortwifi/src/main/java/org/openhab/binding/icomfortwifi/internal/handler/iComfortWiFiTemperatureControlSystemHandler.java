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
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes;
import org.openhab.binding.icomfortwifi.internal.dto.GatewayAlert;
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
 * 
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - Updated for openHAB 5.x compliance
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

    public void initialize() {
        super.initialize();
    }

    public void update(@Nullable SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
        if (systemInfo == null) {
            this.updateiComfortWiFiThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Status not found, check the display id");
        } else {
            if (!this.handleActiveFaults(systemInfo)) {
                this.updateiComfortWiFiThingStatus(ThingStatus.ONLINE);
                this.setDeviceProperties(systemInfo);
                Optional.ofNullable(systemInfo.getGatewaysAlerts()).ifPresent(alertsObj -> {
                    List<GatewayAlert> alerts = alertsObj.systemAlert;

                    if (!alerts.isEmpty() && this.alertNumber >= 0 && this.alertNumber < alerts.size()) {
                        GatewayAlert alert = Objects.requireNonNull(alerts.get(this.alertNumber));

                        Integer alarmNbrLocal = alert.alarmNbr;
                        CustomTypes.AlertStatus status = alert.status;
                        String type = alert.alarmType;
                        String desc = alert.alarmDescription;

                        // Updated to kebab-case
                        this.updateState("alerts-and-reminders#alarm-nbr",
                                alarmNbrLocal != null ? new DecimalType(alarmNbrLocal) : UnDefType.UNDEF);

                        this.updateState("alerts-and-reminders#alarm-description",
                                new StringType(desc != null ? desc : "No description"));

                        this.updateState("alerts-and-reminders#alarm-type",
                                new StringType(type != null ? type : "Unknown"));

                        this.updateState("alerts-and-reminders#alarm-status",
                                status != null ? new StringType(status.toString()) : UnDefType.UNDEF);

                        this.updateState("alerts-and-reminders#date-time-set",
                                this.getAsDateTimeTypeOrNull(alert.dateTimeSet));
                    }
                });

            }

            this.updateState("alerts-and-reminders#alert-number", new DecimalType(this.alertNumber));
        }
    }

    protected void setDeviceProperties(SystemInfo info) {
        try {
            Map<String, String> properties = this.editProperties();
            String sn = info.gatewaySN != null ? info.gatewaySN : "UNKNOWN";
            properties.put("systemName", info.systemName);
            properties.put("gatewaySerialNumber", sn);
            properties.put("firmwareVersion", info.firmwareVersion);
            this.updateProperties(properties);
        } catch (Exception var4) {
            this.logger.error("Error setting device properties: {}", var4.getMessage());
        }
    }

    public void handleCommand(ChannelUID channelUID, Command command) {
        SystemInfo currentInfo = this.systemInfo;
        if (currentInfo != null) {
            if (command == RefreshType.REFRESH) {
                this.update(currentInfo);
                // Updated to kebab-case
            } else if (channelUID.getId().equals("alerts-and-reminders#alert-number")
                    && command instanceof DecimalType) {
                this.alertNumber = ((DecimalType) command).intValue();
                this.update(currentInfo);
            }

        }
    }

    private boolean handleActiveFaults(SystemInfo info) {
        if (info.hasActiveFaults()) {
            this.updateiComfortWiFiThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    info.getActiveFault());
            return true;
        } else {
            return false;
        }
    }
}
