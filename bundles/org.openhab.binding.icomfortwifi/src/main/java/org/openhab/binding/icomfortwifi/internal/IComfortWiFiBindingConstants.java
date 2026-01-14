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
package org.openhab.binding.icomfortwifi.internal;

import java.util.Set;

import org.eclipse.jdt.annotation.Checks;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * 
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - Updated for openHAB 5.x compliance
 */
@NonNullByDefault
public class IComfortWiFiBindingConstants {
    public static final String BINDING_ID = "icomfortwifi";
    public static final String JSON_CONTENT_TYPE = "application/json";
    public static final String URL_V2_BASE = "https://services.myicomfort.com/DBAcessService.svc";
    public static final String CONFIG_USERNAME = "username";
    public static final String CONFIG_PASSWORD = "password";
    public static final String CONFIG_REFRESH_INTERVAL = "refreshInterval";
    public static final ThingTypeUID THING_TYPE_ICOMFORT_ACCOUNT = new ThingTypeUID("icomfortwifi", "account");
    public static final ThingTypeUID THING_TYPE_ICOMFORT_THERMOSTAT = new ThingTypeUID("icomfortwifi", "thermostat");
    public static final ThingTypeUID THING_TYPE_ICOMFORT_ZONE = new ThingTypeUID("icomfortwifi", "zone");

    public static final String ZONE_TEMPERATURE_CHANNEL = "temperature";
    public static final String ZONE_HUMIDITY_CHANNEL = "humidity";
    public static final String ZONE_SYSTEM_STATUS_CHANNEL = "system-status";
    public static final String ZONE_OPERATION_MODE_CHANNEL = "operation-mode";
    public static final String ZONE_AWAY_MODE_CHANNEL = "away-mode";
    public static final String ZONE_UNIFIED_OPERATION_MODE_CHANNEL = "unified-operation-mode";
    public static final String ZONE_FAN_MODE_CHANNEL = "fan-mode";
    public static final String ZONE_COOL_SET_POINT_CHANNEL = "cool-set-point";
    public static final String ZONE_HEAT_SET_POINT_CHANNEL = "heat-set-point";
    public static final String ZONE_SET_POINT_CHANNEL = "set-point";
    public static final String DISPLAY_SYSTEM_MODE_CHANNEL = "system-mode";
    public static final String ZONE_SET_POINT_STATUS_CHANNEL = "set-point-status";

    public static final String TCS_ALARM_DESCRIPTION_CHANNEL = "alerts-and-reminders#alarm-description";
    public static final String TCS_ALARM_NBR_CHANNEL = "alerts-and-reminders#alarm-nbr";
    public static final String TCS_ALARM_TYPE_CHANNEL = "alerts-and-reminders#alarm-type";
    public static final String TCS_ALARM_STATUS_CHANNEL = "alerts-and-reminders#alarm-status";
    public static final String TCS_ALARM_DATE_TIME_SET_CHANNEL = "alerts-and-reminders#date-time-set";

    public static final String TCS_PROPERTY_SYSTEM_NAME = "systemName";
    public static final String TCS_PROPERTY_GATEWAY_SN = "gatewaySerialNumber";
    public static final String TCS_PROPERTY_FIRMWARE_VERSION = "firmwareVersion";
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_ZONE_ID = "zoneID";

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Checks.requireNonNull(Set
            .<ThingTypeUID> of(THING_TYPE_ICOMFORT_ACCOUNT, THING_TYPE_ICOMFORT_THERMOSTAT, THING_TYPE_ICOMFORT_ZONE));

    public IComfortWiFiBindingConstants() {
    }
}
