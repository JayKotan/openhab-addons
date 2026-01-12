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
 * 
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - Updated for openHAB 5.x compliance
 */
@SuppressWarnings("unused")
public class ReqSetTStatInfo {
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

    public ReqSetTStatInfo() {
    }

    public ReqSetTStatInfo(@Nullable ZoneStatus zoneStatus) {
        // 1. Guard against the entire object
        if (zoneStatus == null) {
            return;
        }

        // 2. Simple Fields - Anchor and Assign
        Integer localZone = zoneStatus.zoneNumber;
        this.zoneNumber = (localZone != null) ? localZone : 0;

        this.gatewaySN = zoneStatus.gatewaySN;

        // 3. Complex Objects - Anchor locally to prove safety to the compiler
        // "FanMode"
        FanMode fm = zoneStatus.fanMode;
        this.fanMode = (fm != null) ? fm.getFanModeValue() : null;

        // "OperationMode"
        OperationMode om = zoneStatus.operationMode;
        this.operationMode = (om != null) ? om.getOperationModeValue() : null;

        // "TempUnits" (This fixes your specific error)
        TempUnits ptu = zoneStatus.preferredTemperatureUnit;
        this.prefTempUnits = (ptu != null) ? ptu.getTempUnitsValue() : null;

        // 4. Setpoints (Direct assignment is safe as fields are @Nullable)
        this.coolSetPoint = zoneStatus.coolSetPoint;
        this.heatSetPoint = zoneStatus.heatSetPoint;
    }
}
