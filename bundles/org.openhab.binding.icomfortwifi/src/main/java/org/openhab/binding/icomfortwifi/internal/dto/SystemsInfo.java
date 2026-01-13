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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.RequestStatus;

import com.google.gson.annotations.SerializedName;

/**
 * SystemsInfo DTO for iComfort Wi‑Fi API.
 *
 * Represents the list of HVAC systems associated with the authenticated user.
 */
@SuppressWarnings("unused")
public final class SystemsInfo {

    // ---------------------------------------------------------------------
    // JSON‑mapped fields
    // ---------------------------------------------------------------------

    @SerializedName("ReturnStatus")
    public @NonNull RequestStatus returnStatus = RequestStatus.SUCCESS;

    @SerializedName("Systems")
    public @NonNull List<@NonNull SystemInfo> systems = new ArrayList<>();

    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

    public SystemsInfo() {
        // Gson overwrites defaults; constructor ensures non-null list
    }

    // ---------------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------------

    public @NonNull RequestStatus getReturnStatus() {
        return this.returnStatus;
    }

    public @NonNull List<@NonNull SystemInfo> getSystems() {
        return this.systems;
    }

    // ---------------------------------------------------------------------
    // Setters
    // ---------------------------------------------------------------------

    public void setReturnStatus(@NonNull RequestStatus status) {
        this.returnStatus = status;
    }

    public void setSystems(@NonNull List<@NonNull SystemInfo> systems) {
        this.systems = systems;
    }
}
