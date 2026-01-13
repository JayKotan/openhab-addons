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

import com.google.gson.annotations.SerializedName;

/**
 * GatewayInfo DTO for iComfort Wi‑Fi API.
 *
 * Represents system-level configuration and limits returned by the cloud API.
 */
@SuppressWarnings("unused")
public final class GatewayInfo {

    // ---------------------------------------------------------------------
    // JSON‑mapped fields
    // ---------------------------------------------------------------------

    @SerializedName("Cool_Set_Point_High_Limit")
    public @Nullable Double coolSetPointHighLimit;

    @SerializedName("Cool_Set_Point_Low_Limit")
    public @Nullable Double coolSetPointLowLimit;

    @SerializedName("Daylight_Savings_Time")
    public @Nullable Integer daylightSavingsTime;

    @SerializedName("Heat_Cool_Dead_Band")
    public @Nullable Double heatCoolDeadBand;

    @SerializedName("Heat_Set_Point_High_Limit")
    public @Nullable Double heatSetPointHighLimit;

    @SerializedName("Heat_Set_Point_Low_Limit")
    public @Nullable Double heatSetPointLowLimit;

    @SerializedName("Pref_Language_Nbr")
    public CustomTypes.@Nullable PreferredLanguage preferredLanguage;

    @SerializedName("Pref_Temp_Unit")
    public CustomTypes.@Nullable TempUnits preferredTemperatureUnit;

    @SerializedName("ReturnStatus")
    public CustomTypes.@Nullable RequestStatus returnStatus;

    @SerializedName("SystemID")
    public @Nullable Integer systemID;

    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

    public GatewayInfo() {
        // Gson will populate fields; constructor ensures object is instantiable
    }

    // ---------------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------------

    public @Nullable Double getCoolSetPointHighLimit() {
        return this.coolSetPointHighLimit;
    }

    public @Nullable Double getCoolSetPointLowLimit() {
        return this.coolSetPointLowLimit;
    }

    public @Nullable Integer getDaylightSavingsTime() {
        return this.daylightSavingsTime;
    }

    public @Nullable Double getHeatCoolDeadBand() {
        return this.heatCoolDeadBand;
    }

    public @Nullable Double getHeatSetPointHighLimit() {
        return this.heatSetPointHighLimit;
    }

    public @Nullable Double getHeatSetPointLowLimit() {
        return this.heatSetPointLowLimit;
    }

    public CustomTypes.@Nullable PreferredLanguage getPreferredLanguage() {
        return this.preferredLanguage;
    }

    public CustomTypes.@Nullable TempUnits getPreferredTemperatureUnit() {
        return this.preferredTemperatureUnit;
    }

    public CustomTypes.@Nullable RequestStatus getReturnStatus() {
        return this.returnStatus;
    }

    public @Nullable Integer getSystemID() {
        return this.systemID;
    }

    // ---------------------------------------------------------------------
    // Setters
    // ---------------------------------------------------------------------

    public void setCoolSetPointHighLimit(@Nullable Double value) {
        this.coolSetPointHighLimit = value;
    }

    public void setCoolSetPointLowLimit(@Nullable Double value) {
        this.coolSetPointLowLimit = value;
    }

    public void setDaylightSavingsTime(@Nullable Integer value) {
        this.daylightSavingsTime = value;
    }

    public void setHeatCoolDeadBand(@Nullable Double value) {
        this.heatCoolDeadBand = value;
    }

    public void setHeatSetPointHighLimit(@Nullable Double value) {
        this.heatSetPointHighLimit = value;
    }

    public void setHeatSetPointLowLimit(@Nullable Double value) {
        this.heatSetPointLowLimit = value;
    }

    public void setPreferredLanguage(CustomTypes.@Nullable PreferredLanguage value) {
        this.preferredLanguage = value;
    }

    public void setPreferredTemperatureUnit(CustomTypes.@Nullable TempUnits value) {
        this.preferredTemperatureUnit = value;
    }

    public void setReturnStatus(CustomTypes.@Nullable RequestStatus value) {
        this.returnStatus = value;
    }

    public void setSystemID(@Nullable Integer value) {
        this.systemID = value;
    }
}
