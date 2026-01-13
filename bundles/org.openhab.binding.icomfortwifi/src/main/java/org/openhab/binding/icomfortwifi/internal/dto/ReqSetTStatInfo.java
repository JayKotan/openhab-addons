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

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.FanMode;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.OperationMode;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.TempUnits;

import com.google.gson.annotations.SerializedName;

/**
 * ReqSetTStatInfo DTO for iComfort Wi‑Fi API.
 *
 * Represents the payload sent to update thermostat settings for a zone.
 */
@SuppressWarnings("unused")
public final class ReqSetTStatInfo {

    // ---------------------------------------------------------------------
    // JSON‑mapped fields
    // ---------------------------------------------------------------------

    @SerializedName("Cool_Set_Point")
    public @Nullable Double coolSetPoint;

    @SerializedName("Heat_Set_Point")
    public @Nullable Double heatSetPoint;

    @SerializedName("Fan_Mode")
    public @Nullable Integer fanMode;

    @SerializedName("Operation_Mode")
    public @Nullable Integer operationMode;

    @SerializedName("Pref_Temp_Units")
    public @Nullable String prefTempUnits;

    @SerializedName("Zone_Number")
    public @Nullable Integer zoneNumber;

    @SerializedName("GatewaySN")
    public @Nullable String gatewaySN;

    // ---------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------

    public ReqSetTStatInfo() {
        // Default constructor for manual construction or Gson
    }

    public ReqSetTStatInfo(@Nullable ZoneStatus zoneStatus) {
        if (zoneStatus == null) {
            return;
        }

        // Anchor simple fields
        Integer localZone = zoneStatus.zoneNumber;
        this.zoneNumber = (localZone != null) ? localZone : 0;

        this.gatewaySN = zoneStatus.gatewaySN;

        // Anchor complex fields
        FanMode fm = zoneStatus.fanMode;
        this.fanMode = (fm != null) ? fm.getFanModeValue() : null;

        OperationMode om = zoneStatus.operationMode;
        this.operationMode = (om != null) ? om.getOperationModeValue() : null;

        TempUnits ptu = zoneStatus.preferredTemperatureUnit;
        this.prefTempUnits = (ptu != null) ? ptu.getTempUnitsValue() : null;

        // Direct assignment for nullable setpoints
        this.coolSetPoint = zoneStatus.coolSetPoint;
        this.heatSetPoint = zoneStatus.heatSetPoint;
    }

    // ---------------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------------

    public @Nullable Double getCoolSetPoint() {
        return this.coolSetPoint;
    }

    public @Nullable Double getHeatSetPoint() {
        return this.heatSetPoint;
    }

    public @Nullable Integer getFanMode() {
        return this.fanMode;
    }

    public @Nullable Integer getOperationMode() {
        return this.operationMode;
    }

    public @Nullable String getPrefTempUnits() {
        return this.prefTempUnits;
    }

    public @Nullable Integer getZoneNumber() {
        return this.zoneNumber;
    }

    public @Nullable String getGatewaySN() {
        return this.gatewaySN;
    }

    // ---------------------------------------------------------------------
    // Setters
    // ---------------------------------------------------------------------

    public void setCoolSetPoint(@Nullable Double value) {
        this.coolSetPoint = value;
    }

    public void setHeatSetPoint(@Nullable Double value) {
        this.heatSetPoint = value;
    }

    public void setFanMode(@Nullable Integer value) {
        this.fanMode = value;
    }

    public void setOperationMode(@Nullable Integer value) {
        this.operationMode = value;
    }

    public void setPrefTempUnits(@Nullable String value) {
        this.prefTempUnits = value;
    }

    public void setZoneNumber(@Nullable Integer value) {
        this.zoneNumber = value;
    }

    public void setGatewaySN(@Nullable String value) {
        this.gatewaySN = value;
    }
}
