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
package org.openhab.binding.icomfortwifi.internal.api.models.request;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.icomfortwifi.internal.api.models.response.ZoneStatus;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for the setting status information
 *
 * @author Konstantin Panchenko - Initial contribution
 *
 */
@NonNullByDefault
public class ReqSetTStatInfo {
    @SerializedName("Cool_Set_Point")
    public Double coolSetPoint = 0.0;
    @SerializedName("Heat_Set_Point")
    public Double heatSetPoint = 0.0;
    @SerializedName("Fan_Mode")
    public Integer fanMode = 0;
    @SerializedName("Operation_Mode")
    public Integer operationMode = 0;
    @SerializedName("Pref_Temp_Units")
    public String prefTempUnits = "";
    @SerializedName("Zone_Number")
    public Integer zoneNumber = 0;
    @SerializedName("GatewaySN")
    public String gatewaySN = "";

    public ReqSetTStatInfo() {
    }

    public ReqSetTStatInfo(ZoneStatus zoneStatus) {
        this.coolSetPoint = zoneStatus.coolSetPoint;
        this.heatSetPoint = zoneStatus.heatSetPoint;
        this.fanMode = zoneStatus.fanMode.getFanModeValue();
        this.operationMode = zoneStatus.operationMode.getOperationModeValue();
        this.prefTempUnits = zoneStatus.preferedTemperatureUnit.getTempUnitsValue();
        this.zoneNumber = zoneStatus.zoneNumber;
        this.gatewaySN = zoneStatus.gatewaySN;
    }
}
