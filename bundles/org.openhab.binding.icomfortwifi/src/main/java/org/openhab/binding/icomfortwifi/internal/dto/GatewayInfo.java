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
 * Response model for the gateway information
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - Updated for openHAB 5.x compliance
 */
@SuppressWarnings("unused")
public class GatewayInfo {
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

    public GatewayInfo() {
    }
}
