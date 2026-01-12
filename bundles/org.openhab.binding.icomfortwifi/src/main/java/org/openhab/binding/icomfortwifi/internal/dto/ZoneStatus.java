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
package org.openhab.binding.icomfortwifi.internal.dto;

import java.util.Date;
import java.util.List;

import javax.measure.Unit;
import javax.measure.quantity.Temperature;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.FanMode;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.OperationMode;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.SystemStatus;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.TempUnits;
import org.openhab.core.library.unit.ImperialUnits;
import org.openhab.core.library.unit.SIUnits;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - Updated for openHAB 5.x compliance
 */
@SuppressWarnings("unused")
public class ZoneStatus {
    @SerializedName("Away_Mode")
    public @Nullable String awayMode = "AWAY_OFF";
    @SerializedName("ConnectionStatus")
    public @Nullable String connectionStatus = "UNKNOWN";
    @SerializedName("Central_Zoned_Away")
    public Integer centralZonedAway = 0;
    @SerializedName("Cool_Set_Point")
    public Double coolSetPoint = 0.0;
    @SerializedName("DateTime_Mark")
    public @Nullable Date dateTimeMark;
    @SerializedName("Fan_Mode")
    public CustomTypes.FanMode fanMode;
    @SerializedName("GMT_To_Local")
    public Integer gmtToLocal;
    @SerializedName("GatewaySN")
    public @NonNull String gatewaySN;

    @SerializedName("Golden_Table_Updated")
    public Boolean goldenTableUpdated;
    @SerializedName("Heat_Set_Point")
    public Double heatSetPoint;
    @SerializedName("Indoor_Humidity")
    public Integer indoorHumidity;
    @SerializedName("Indoor_Temp")
    public Double indoorTemp;
    @SerializedName("Operation_Mode")
    public CustomTypes.OperationMode operationMode;
    @SerializedName("Pref_Temp_Units")

    public CustomTypes.@Nullable TempUnits preferredTemperatureUnit;
    @SerializedName("Program_Schedule_Mode")

    public @Nullable String programScheduleMode;
    @SerializedName("Program_Schedule_Selection")
    public Integer programScheduleSelection;
    @SerializedName("System_Status")
    public CustomTypes.@Nullable SystemStatus systemStatus;
    @SerializedName("Zone_Enabled")
    public Integer zoneEnabled;
    @SerializedName("Zone_Name")
    public @NonNull String zoneName;
    @SerializedName("Zone_Number")
    public Integer zoneNumber;
    @SerializedName("Zones_Installed")
    public Integer zonesInstalled;

    @SerializedName("ActiveFaults")
    public @Nullable List<String> activeFaults;

    public ZoneStatus() {
        this.fanMode = FanMode.AUTO;
        this.gmtToLocal = 0;
        this.gatewaySN = "";
        this.goldenTableUpdated = false;
        this.heatSetPoint = 0.0;
        this.indoorHumidity = 0;
        this.indoorTemp = 0.0;
        this.operationMode = OperationMode.OFF;
        this.programScheduleMode = "";
        this.programScheduleSelection = 0;
        this.systemStatus = SystemStatus.IDLE;
        this.zoneEnabled = 0;
        this.zoneName = "";
        this.zoneNumber = 0;
        this.zonesInstalled = 0;
    }

    // ...

    public @NonNull Unit<@NonNull Temperature> getTemperatureUnit() {
        // Use a local variable to anchor the nullable field
        TempUnits units = this.preferredTemperatureUnit;

        // Return the non-null units, defaulting to Celsius if units is null or not Fahrenheit
        return (units == TempUnits.FAHRENHEIT) ? ImperialUnits.FAHRENHEIT : SIUnits.CELSIUS;
    }

    public @NonNull String getZoneID() {
        return this.gatewaySN + "_" + this.zoneNumber;
    }

    public boolean hasActiveFaults() {
        // 1. Anchor the nullable field to a local variable
        List<String> faults = this.activeFaults;

        // 2. Perform the null check and the size check on the local variable
        return faults != null && !faults.isEmpty();
    }

    public String getActiveFault() {
        // 1. Anchor again
        List<String> faults = this.activeFaults;

        // 2. Safely access the first element
        if (faults != null && !faults.isEmpty()) {
            return faults.get(0);
        }
        return "NONE";
    }
}
