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
import java.util.Map;
import java.util.Objects;

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
 * ZoneStatus DTO for iComfort Wi‑Fi API.
 *
 * Represents the state of a single HVAC zone.
 */
@SuppressWarnings("unused")
public final class ZoneStatus {

    // ---------------------------------------------------------------------
    // JSON‑mapped fields
    // ---------------------------------------------------------------------

    @SerializedName("Away_Mode")
    public Integer awayMode = 0;

    @SerializedName("ConnectionStatus")
    public @Nullable String connectionStatus = "UNKNOWN";

    @SerializedName("Cool_Set_Point")
    public Double coolSetPoint = 0.0;

    @SerializedName("DateTime_Mark")
    public @Nullable Date dateTimeMark;

    @SerializedName("Fan_Mode")
    public FanMode fanMode;

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
    public OperationMode operationMode;

    @SerializedName("Pref_Temp_Units")
    public @Nullable TempUnits preferredTemperatureUnit;

    @SerializedName("Program_Schedule_Mode")
    public @Nullable String programScheduleMode;

    @SerializedName("Program_Schedule_Selection")
    public Integer programScheduleSelection;

    @SerializedName("System_Status")
    public @Nullable SystemStatus systemStatus;

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

    @SerializedName("Schedule_Name")
    public @Nullable String scheduleName;

    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

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

    // ---------------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------------

    public @NonNull Unit<@NonNull Temperature> getTemperatureUnit() {
        TempUnits units = this.preferredTemperatureUnit;
        return (units == TempUnits.FAHRENHEIT) ? ImperialUnits.FAHRENHEIT : SIUnits.CELSIUS;
    }

    public @NonNull String getZoneID() {
        return this.gatewaySN + "_" + this.zoneNumber;
    }

    public @Nullable String getScheduleName() {
        return this.scheduleName;
    }

    // ---------------------------------------------------------------------
    // Utility Methods
    // ---------------------------------------------------------------------

    public boolean hasActiveFaults() {
        List<String> faults = this.activeFaults;
        return faults != null && !faults.isEmpty();
    }

    public String getActiveFault() {
        List<String> faults = this.activeFaults;
        if (faults != null && !faults.isEmpty()) {
            return faults.get(0);
        }
        return "NONE";
    }

    /**
     * Return the program schedule selection as a primitive int with a safe default.
     */
    public @Nullable Integer getProgramScheduleSelection() {
        return this.programScheduleSelection;
    }

    /**
     * Convenience: return a display string for the schedule selection using the provided map.
     * If the map contains a label for the index, that label is returned; otherwise the numeric index is returned.
     */
    public static String toDisplaySchedule(@Nullable Map<Integer, String> scheduleMap, @Nullable Integer index) {
        if (index == null) {
            return "unknown";
        }
        if (scheduleMap != null) {
            String label = scheduleMap.get(index);
            if (label != null && !label.isEmpty()) {
                return label;
            }
        }
        return Integer.toString(index);
    }

    // ---------------------------------------------------------------------
    // Object helpers
    // ---------------------------------------------------------------------

    @Override
    public String toString() {
        return "ZoneStatus{" + "gatewaySN='" + gatewaySN + '\'' + ", zoneNumber=" + zoneNumber + ", zoneName='"
                + zoneName + '\'' + ", programScheduleSelection=" + programScheduleSelection + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ZoneStatus)) {
            return false;
        }
        ZoneStatus that = (ZoneStatus) o;
        return Objects.equals(gatewaySN, that.gatewaySN) && Objects.equals(zoneNumber, that.zoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gatewaySN, zoneNumber);
    }
}
