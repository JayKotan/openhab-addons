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

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.RequestStatus;

import com.google.gson.annotations.SerializedName;

/**
 * GatewaysAlerts DTO for iComfort Wi‑Fi API.
 *
 * Represents the list of system-level alerts returned by the gateway.
 */
@SuppressWarnings("unused")
public final class GatewaysAlerts {

    // ---------------------------------------------------------------------
    // JSON‑mapped fields
    // ---------------------------------------------------------------------

    @SerializedName("ReturnStatus")
    public @Nullable RequestStatus returnStatus;
    @SerializedName("Alerts")
    public @Nullable List<GatewayAlert> alerts;
    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

    public GatewaysAlerts() {
        // Gson will populate fields; constructor ensures non-null list
    }

    // ---------------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------------

    public @Nullable RequestStatus getReturnStatus() {
        return this.returnStatus;
    }

    public @Nullable List<GatewayAlert> getAlerts() {
        return this.alerts;
    }
    // ---------------------------------------------------------------------
    // Setters
    // ---------------------------------------------------------------------

    public void setReturnStatus(@Nullable RequestStatus status) {
        this.returnStatus = status;
    }

    public void setAlerts(@Nullable List<GatewayAlert> alerts) {
        this.alerts = alerts;
    }
}
