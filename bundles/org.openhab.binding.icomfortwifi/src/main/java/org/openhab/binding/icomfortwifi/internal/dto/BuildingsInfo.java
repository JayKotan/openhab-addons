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

import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.RequestStatus;

import com.google.gson.annotations.SerializedName;

/**
 * BuildingsInfo DTO for iComfort Wi‑Fi API.
 *
 * Represents the list of buildings associated with the authenticated user.
 */
@SuppressWarnings("unused")
public final class BuildingsInfo {

    // ---------------------------------------------------------------------
    // JSON‑mapped fields
    // ---------------------------------------------------------------------

    @SerializedName("ReturnStatus")
    public RequestStatus returnStatus = RequestStatus.SUCCESS;

    @SerializedName("Buildings")
    public List<Building> buildings = new ArrayList<>();

    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

    public BuildingsInfo() {
        // Gson overwrites defaults; constructor ensures non-null list
    }

    // ---------------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------------

    public RequestStatus getReturnStatus() {
        return this.returnStatus;
    }

    public List<Building> getBuildings() {
        return this.buildings;
    }

    // ---------------------------------------------------------------------
    // Setters
    // ---------------------------------------------------------------------

    public void setReturnStatus(RequestStatus status) {
        this.returnStatus = status;
    }

    public void setBuildings(List<Building> buildings) {
        this.buildings = buildings;
    }
}
