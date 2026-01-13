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

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.RequestStatus;

import com.google.gson.annotations.SerializedName;

/**
 * ZonesStatus DTO for iComfort Wi‑Fi API.
 *
 * Represents the list of zone thermostat information returned by the cloud API.
 */
@SuppressWarnings("unused")
public final class ZonesStatus {

    // ---------------------------------------------------------------------
    // JSON‑mapped fields
    // ---------------------------------------------------------------------

    @SerializedName("ReturnStatus")
    public @NonNull RequestStatus returnStatus = RequestStatus.SUCCESS;

    @SerializedName("tStatInfo")
    public @Nullable List<ZoneStatus> zoneStatus;

    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

    public ZonesStatus() {
        // Gson overwrites defaults; constructor ensures non-null collections
    }

    // ---------------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------------

    public @NonNull RequestStatus getReturnStatus() {
        return this.returnStatus;
    }

    public @Nullable List<ZoneStatus> getZoneStatus() {
        return this.zoneStatus;
    }

    // ---------------------------------------------------------------------
    // Setters
    // ---------------------------------------------------------------------

    public void setReturnStatus(@NonNull RequestStatus status) {
        this.returnStatus = status;
    }

    public void setZoneStatus(@Nullable List<ZoneStatus> zoneStatus) {
        this.zoneStatus = zoneStatus;
    }
}
