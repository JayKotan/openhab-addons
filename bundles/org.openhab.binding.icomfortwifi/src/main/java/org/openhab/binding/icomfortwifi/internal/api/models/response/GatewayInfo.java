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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.PreferedLanguage;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.RequestStatus;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.TempUnits;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the gateway information
 *
 * @author Konstantin Panchenko - Initial contribution
 *
 */
@NonNullByDefault
public class GatewayInfo {

    @SerializedName("Cool_Set_Point_High_Limit")
    public Double coolSetPointHighLimit = 0.0;

    @SerializedName("Cool_Set_Point_Low_Limit")
    public Double coolSetPointLowLimit = 0.0;

    @SerializedName("Daylight_Savings_Time")
    public Integer daylightSavingsTime = 0;

    @SerializedName("Heat_Cool_Dead_Band")
    public Double heatCoolDeadBand = 0.0;

    @SerializedName("Heat_Set_Point_High_Limit")
    public Double heatSetPointHighLimit = 0.0;

    @SerializedName("Heat_Set_Point_Low_Limit")
    public Double heatSetPointLowLimit = 0.0;

    @SerializedName("Pref_Language_Nbr")
    public PreferedLanguage preferedLanguage = PreferedLanguage.ENGLISH;

    @SerializedName("Pref_Temp_Unit")
    public TempUnits preferedTemperatureUnit = TempUnits.FAHRENHEIT;

    @SerializedName("ReturnStatus")
    public RequestStatus returnStatus = RequestStatus.UNKNOWN;

    @SerializedName("SystemID")
    public Integer systemID = 0;

    // Constructor that initializes all fields
    public GatewayInfo(Double coolSetPointHighLimit, Double coolSetPointLowLimit, Integer daylightSavingsTime,
            Double heatCoolDeadBand, Double heatSetPointHighLimit, Double heatSetPointLowLimit,
            PreferedLanguage preferedLanguage, TempUnits preferedTemperatureUnit, RequestStatus returnStatus,
            Integer systemID) {
        this.coolSetPointHighLimit = coolSetPointHighLimit;
        this.coolSetPointLowLimit = coolSetPointLowLimit;
        this.daylightSavingsTime = daylightSavingsTime;
        this.heatCoolDeadBand = heatCoolDeadBand;
        this.heatSetPointHighLimit = heatSetPointHighLimit;
        this.heatSetPointLowLimit = heatSetPointLowLimit;
        this.preferedLanguage = preferedLanguage;
        this.preferedTemperatureUnit = preferedTemperatureUnit;
        this.returnStatus = returnStatus;
        this.systemID = systemID;
    }

    public GatewayInfo() {
        this.coolSetPointHighLimit = 0.0;
        this.coolSetPointLowLimit = 0.0;
        this.daylightSavingsTime = 0;
        this.heatCoolDeadBand = 0.0;
        this.heatSetPointHighLimit = 0.0;
        this.heatSetPointLowLimit = 0.0;
        this.preferedLanguage = PreferedLanguage.ENGLISH;
        this.preferedTemperatureUnit = TempUnits.FAHRENHEIT;
        this.returnStatus = RequestStatus.UNKNOWN;
        this.systemID = 0; // Or other default value
    }
}
