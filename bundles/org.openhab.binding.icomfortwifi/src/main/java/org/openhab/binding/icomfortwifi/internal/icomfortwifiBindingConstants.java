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
package org.openhab.binding.icomfortwifi.internal;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link iComfortWiFiBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Updated imports
 */
@NonNullByDefault
public class iComfortWiFiBindingConstants {

    private static final String BINDING_ID = "icomfortwifi";

    /** The JSON content type used when talking to iComfort. */
    public static final String JSON_CONTENT_TYPE = "application/json";

    public static final String URL_V2_BASE = "https://services.myicomfort.com/DBAcessService.svc";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_ICOMFORT_ACCOUNT = new ThingTypeUID(BINDING_ID, "account");
    public static final ThingTypeUID THING_TYPE_ICOMFORT_THERMOSTAT = new ThingTypeUID(BINDING_ID, "thermostat");
    public static final ThingTypeUID THING_TYPE_ICOMFORT_ZONE = new ThingTypeUID(BINDING_ID, "zone");
    public static final ThingTypeUID THING_TYPE_SAMPLE = new ThingTypeUID(BINDING_ID, "sample");
    // List of all Channel IDs
    // Read-Only channels
    public static final String ZONE_TEMPERATURE_CHANNEL = "Temperature";
    public static final String ZONE_HUMIDITY_CHANNEL = "Humidity";
    public static final String ZONE_SYSTEM_STATUS_CHANNEL = "SystemStatus";
    // Read-Write channels
    public static final String ZONE_OPERATION_MODE_CHANNEL = "OperationMode";
    public static final String ZONE_AWAY_MODE_CHANNEL = "AwayMode";
    public static final String ZONE_UNIFIED_OPERATION_MODE_CHANNEL = "UnifiedOperationMode"; // Used to set combined
                                                                                             // Away and Operation mode
    public static final String ZONE_FAN_MODE_CHANNEL = "FanMode";
    public static final String ZONE_COOL_SET_POINT_CHANNEL = "CoolSetPoint";
    public static final String ZONE_HEAT_SET_POINT_CHANNEL = "HeatSetPoint";
    public static final String ZONE_SET_POINT_CHANNEL = "SetPoint"; // To set heat only / cool only point
    public static final String DISPLAY_SYSTEM_MODE_CHANNEL = "SystemMode";
    public static final String ZONE_SET_POINT_STATUS_CHANNEL = "SetPointStatus";

    // TCS Channels
    public static final String TCS_ALARM_DESCRIPTION_CHANNEL = "alertsAndReminders#AlarmDescription";
    public static final String TCS_ALARM_NBR_CHANNEL = "alertsAndReminders#AlarmNbr";
    public static final String TCS_ALARM_TYPE_CHANNEL = "alertsAndReminders#AlarmType";
    public static final String TCS_ALARM_STATUS_CHANNEL = "alertsAndReminders#AlarmStatus";
    public static final String TCS_ALARM_DATE_TIME_SET_CHANNEL = "alertsAndReminders#DateTimeSet";
    public static final String TCS_ALARM_ALERT_NUMBER = "alertsAndReminders#AlertNumber";
    public static final String CHANNEL_1 = "channel1";

    // TCS Properties
    public static final String TCS_PROPERTY_SYSTEM_NAME = "systemName";
    public static final String TCS_PROPERTY_GATEWAY_SN = "gatewaySerialNumber";
    public static final String TCS_PROPERTY_FIRMWARE_VERSION = "firmwareVersion";

    // List of Discovery properties
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_NAME = "name";

    // List of all addressable things in OH = SUPPORTED_DEVICE_THING_TYPES_UIDS + the virtual bridge
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.unmodifiableSet(
            Stream.of(THING_TYPE_ICOMFORT_ACCOUNT, THING_TYPE_ICOMFORT_THERMOSTAT, THING_TYPE_ICOMFORT_ZONE)
                    .collect(Collectors.toSet()));
}
