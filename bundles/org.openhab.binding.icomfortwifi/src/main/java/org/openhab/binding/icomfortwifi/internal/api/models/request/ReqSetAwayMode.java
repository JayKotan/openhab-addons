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
public class ReqSetAwayMode {

    @SerializedName("GatewaySN")
    public String gatewaySN = "";

    @SerializedName("ZoneNumber")
    public Integer zoneNumber = 0;

    @SerializedName("AwayMode")
    public Integer awayMode = 0;

    @SerializedName("HeatSetPoint")
    public Double heatSetPoint = 0.0; // Nullable field

    @SerializedName("CoolSetPoint")
    public Double coolSetPoint = 0.0; // Nullable field

    @SerializedName("FanMode")
    public Integer fanMode = 0;

    @SerializedName("TempScale")
    public String preferedTemperatureUnit = ""; // Nullable field

    public ReqSetAwayMode() {
        // this.gatewaySN = "";
    }

    public ReqSetAwayMode(ZoneStatus zoneStatus) {
        this.gatewaySN = zoneStatus.gatewaySN;
        this.zoneNumber = zoneStatus.zoneNumber;
        this.awayMode = zoneStatus.awayMode.getAwayValue();
        this.heatSetPoint = zoneStatus.heatSetPoint;
        this.coolSetPoint = zoneStatus.coolSetPoint;
        this.fanMode = zoneStatus.fanMode.getFanModeValue();
        this.preferedTemperatureUnit = zoneStatus.preferedTemperatureUnit.getTempUnitsValue();
    }
}
