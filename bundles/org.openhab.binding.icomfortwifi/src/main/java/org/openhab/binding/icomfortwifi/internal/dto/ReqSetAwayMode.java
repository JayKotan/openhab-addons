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

import java.util.Optional;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.FanMode;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.TempUnits;

import com.google.gson.annotations.SerializedName;

/**
 * ReqSetAwayMode DTO for iComfort Wi‑Fi API.
 *
 * Represents the payload sent to update Away Mode for a specific zone.
 */
@SuppressWarnings("unused")
public final class ReqSetAwayMode {

    // ---------------------------------------------------------------------
    // JSON‑mapped fields
    // ---------------------------------------------------------------------

    @SerializedName("GatewaySN")
    public String gatewaySN = "";

    @SerializedName("ZoneNumber")
    public Integer zoneNumber = 0;

    @SerializedName("AwayMode")
    public Integer awayMode = 0;

    @SerializedName("HeatSetPoint")
    public @Nullable Double heatSetPoint;

    @SerializedName("CoolSetPoint")
    public @Nullable Double coolSetPoint;

    @SerializedName("FanMode")
    public Integer fanMode = 0;

    @SerializedName("TempScale")
    public String preferredTemperatureUnit = "";

    // ---------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------

    public ReqSetAwayMode() {
        // Default constructor for manual construction or Gson
    }

    public ReqSetAwayMode(@Nullable ZoneStatus zoneStatus) {
        if (zoneStatus == null) {
            return;
        }

        // Anchor variables locally to satisfy strict null-analysis
        this.gatewaySN = zoneStatus.gatewaySN;

        Integer zNum = zoneStatus.zoneNumber;
        this.zoneNumber = (zNum != null) ? zNum : 0;

        // Correct integer-based Away Mode mapping
        this.awayMode = (zoneStatus.awayMode != null && zoneStatus.awayMode == 1) ? 1 : 0;

        // Explicitly anchor nullable doubles to clear setpoint warnings
        this.heatSetPoint = zoneStatus.heatSetPoint;
        this.coolSetPoint = zoneStatus.coolSetPoint;

        // Using method references avoids intermediate lambda parameters
        this.fanMode = Optional.ofNullable(zoneStatus.fanMode).map(FanMode::getFanModeValue).orElse(0);

        TempUnits tempUnits = zoneStatus.preferredTemperatureUnit;
        this.preferredTemperatureUnit = (tempUnits != null) ? tempUnits.getTempUnitsValue() : "";
    }

    // ---------------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------------

    public String getGatewaySN() {
        return this.gatewaySN;
    }

    public Integer getZoneNumber() {
        return this.zoneNumber;
    }

    public Integer getAwayMode() {
        return this.awayMode;
    }

    public @Nullable Double getHeatSetPoint() {
        return this.heatSetPoint;
    }

    public @Nullable Double getCoolSetPoint() {
        return this.coolSetPoint;
    }

    public Integer getFanMode() {
        return this.fanMode;
    }

    public String getPreferredTemperatureUnit() {
        return this.preferredTemperatureUnit;
    }

    // ---------------------------------------------------------------------
    // Setters
    // ---------------------------------------------------------------------

    public void setGatewaySN(String value) {
        this.gatewaySN = value;
    }

    public void setZoneNumber(Integer value) {
        this.zoneNumber = value;
    }

    public void setAwayMode(Integer value) {
        this.awayMode = value;
    }

    public void setHeatSetPoint(@Nullable Double value) {
        this.heatSetPoint = value;
    }

    public void setCoolSetPoint(@Nullable Double value) {
        this.coolSetPoint = value;
    }

    public void setFanMode(Integer value) {
        this.fanMode = value;
    }

    public void setPreferredTemperatureUnit(String value) {
        this.preferredTemperatureUnit = value;
    }
}
