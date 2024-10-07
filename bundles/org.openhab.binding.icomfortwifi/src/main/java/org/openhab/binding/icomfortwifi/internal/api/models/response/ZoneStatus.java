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

public class ZoneStatus {

    @SerializedName("Away_Mode")
    public AwayStatus awayMode;

    @SerializedName("Central_Zoned_Away")
    public Integer centralZonedAway;

    @SerializedName("ConnectionStatus")
    public ConnectionStatus connectionStatus;

    @SerializedName("Cool_Set_Point")
    public Double coolSetPoint;

    @SerializedName("DateTime_Mark")
    public Date dateTimeMark;

    @SerializedName("Fan_Mode")
    public FanMode fanMode;

    @SerializedName("GMT_To_Local")
    public Integer gmtToLocal;

    @SerializedName("GatewaySN")
    public String gatewaySN;

    @SerializedName("Golden_Table_Updated")
    public Boolean goldenTableUpdated;

    @SerializedName("Heat_Set_Point")
    public Double heatSetPoint;

    @SerializedName("Indoor_Humidity")
    public Integer indoorHumidity;

    @SerializedName("Indoor_Temp")
    public Double indoorTemp;

    @SerializedName("Operation_Mode")
    public OperationMode operationMode;

    @SerializedName("Pref_Temp_Units")
    public TempUnits preferredTemperatureUnit;

    @SerializedName("Program_Schedule_Mode")
    public String programScheduleMode;

    @SerializedName("Program_Schedule_Selection")
    public Integer programScheduleSelection;

    @SerializedName("System_Status")
    public SystemStatus systemStatus;

    @SerializedName("Zone_Enabled")
    public Integer zoneEnabled;

    @SerializedName("Zone_Name")
    public String zoneName;

    @SerializedName("Zone_Number")
    public Integer zoneNumber;

    @SerializedName("Zones_Installed")
    public Integer zonesInstalled;

    @SerializedName("zoneId")
    private String zoneId;

    public ZoneStatus() {
    }

    public boolean hasActiveFaults() { // Always false, don't know what System Status could be.
        return false;
    }

    public String getActiveFault() { // For future implementation System Status must be provided as string message
        return systemStatus.toString();
    }

    public String getZoneIdTcs() {
        return zoneId;
    }

    public String getZoneID() {
        return gatewaySN + "_" + zoneNumber.toString();
    }
}
