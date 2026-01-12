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
 * 
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - Updated for openHAB 5.x compliance
 */
@SuppressWarnings("unused")
public class ReqSetAwayMode {
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

    public ReqSetAwayMode() {
    }

    public ReqSetAwayMode(@Nullable ZoneStatus zoneStatus) {
        if (zoneStatus == null) {
            return;
        }

        // Anchor variables locally to satisfy strict null-analysis
        this.gatewaySN = zoneStatus.gatewaySN;

        Integer zNum = zoneStatus.zoneNumber;
        this.zoneNumber = (zNum != null) ? zNum : 0;

        this.awayMode = "AWAY_ON".equals(zoneStatus.awayMode) ? 1 : 0;

        // Explicitly anchor nullable doubles to clear setpoint warnings
        this.heatSetPoint = zoneStatus.heatSetPoint;
        this.coolSetPoint = zoneStatus.coolSetPoint;

        // Using Method References (ClassName::MethodName) often satisfies JDT
        // because it avoids the creation of an intermediate lambda parameter
        this.fanMode = Optional.ofNullable(zoneStatus.fanMode).map(FanMode::getFanModeValue).orElse(0);

        TempUnits tempUnits = zoneStatus.preferredTemperatureUnit;
        if (tempUnits != null) {
            this.preferredTemperatureUnit = tempUnits.getTempUnitsValue();
        } else {
            this.preferredTemperatureUnit = "";
        }
    }
}
