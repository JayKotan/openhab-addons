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

import org.openhab.binding.icomfortwifi.internal.api.models.response.ZoneStatus;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for the setting status information
 *
 * @author Konstantin Panchenko - Initial contribution
 *
 */

public class ReqSetAwayMode {
    @SerializedName("GatewaySN")
    public String gatewaySN = null;
    @SerializedName("ZoneNumber")
    public Integer zoneNumber = 0;
    @SerializedName("AwayMode")
    public Integer awayMode = 0;
    @SerializedName("HeatSetPoint")
    public Double heatSetPoint = null;
    @SerializedName("CoolSetPoint")
    public Double coolSetPoint = null;
    @SerializedName("FanMode")
    public Integer fanMode = 0;
    @SerializedName("TempScale")
    public String preferedTemperatureUnit = null;
    @SerializedName("Program_Schedule_Mode")
    public String programScheduleMode = "";
    @SerializedName("Program_Schedule_Selection")
    public Integer programScheduleSelection = 0;

    public ReqSetAwayMode() {
    }

    public ReqSetAwayMode(ZoneStatus zoneStatus) {
        this.gatewaySN = zoneStatus.gatewaySN;
        this.zoneNumber = zoneStatus.zoneNumber;
        this.awayMode = zoneStatus.awayMode.getAwayValue();
        this.heatSetPoint = zoneStatus.heatSetPoint;
        this.coolSetPoint = zoneStatus.coolSetPoint;
        this.fanMode = zoneStatus.fanMode.getFanModeValue();
        this.preferedTemperatureUnit = zoneStatus.preferedTemperatureUnit.getTempUnitsValue();
        this.programScheduleMode = zoneStatus.programScheduleMode.getProgramScheduleModeValue();
        this.programScheduleSelection = zoneStatus.programScheduleSelection.getProgramScheduleSelectionValue();
    }
}
