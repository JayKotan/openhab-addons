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
package org.openhab.binding.icomfortwifi.internal.api.models.response;

import java.util.Date;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.AwayStatus;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.ConnectionStatus;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.FanMode;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.OperationMode;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.SystemStatus;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.TempUnits;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the zone status
 *
 * @author Konstantin Panchenko - Initial contribution
 *
 */
@NonNullByDefault
public class ZoneStatus {

    @SerializedName("Away_Mode")
    public AwayStatus awayMode = AwayStatus.UNKNOWN;

    @SerializedName("Central_Zoned_Away")
    public Integer centralZonedAway = 0;

    @SerializedName("ConnectionStatus")
    public ConnectionStatus connectionStatus = ConnectionStatus.UNKNOWN;

    @SerializedName("Cool_Set_Point")
    public Double coolSetPoint = 0.0;

    @SerializedName("DateTime_Mark")
    public Date dateTimeMark = new Date();

    @SerializedName("Fan_Mode")
    public FanMode fanMode = FanMode.UNKNOWN;

    @SerializedName("GMT_To_Local")
    public Integer gmtToLocal = 0;

    @SerializedName("GatewaySN")
    public String gatewaySN = "";

    @SerializedName("Golden_Table_Updated")
    public Boolean goldenTableUpdated = false;

    @SerializedName("Heat_Set_Point")
    public Double heatSetPoint = 0.0;

    @SerializedName("Indoor_Humidity")
    public Integer indoorHumidity = 0;

    @SerializedName("Indoor_Temp")
    public Double indoorTemp = 0.0;

    @SerializedName("Operation_Mode")
    public OperationMode operationMode = OperationMode.UNKNOWN;

    @SerializedName("Pref_Temp_Units")
    public TempUnits preferedTemperatureUnit = TempUnits.FAHRENHEIT;

    @SerializedName("Program_Schedule_Mode")
    public String programScheduleMode = "";

    @SerializedName("Program_Schedule_Selection")
    public Integer programScheduleSelection = 0;

    @SerializedName("System_Status")
    public SystemStatus systemStatus = SystemStatus.UNKNOWN;

    @SerializedName("Zone_Enabled")
    public Integer zoneEnabled = 0;

    @SerializedName("Zone_Name")
    public String zoneName = "";

    @SerializedName("Zone_Number")
    public Integer zoneNumber = 0;

    @SerializedName("Zones_Installed")
    public Integer zonesInstalled = 0;

    public ZoneStatus() {
    }

    public String getZoneID() {
        return gatewaySN + "_" + zoneNumber; // Assuming gatewaySN and zoneNumber are initialized
    }

    public boolean hasActiveFaults() {
        // Return false as per your previous indication.
        return false;
    }

    public String getActiveFault() {
        // Directly return the string representation of the current system status.
        return systemStatus.toString(); // Assumed to be non-null, so no need for null check.
    }
}
