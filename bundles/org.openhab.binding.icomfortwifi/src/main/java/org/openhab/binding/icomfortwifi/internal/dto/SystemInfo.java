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

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * SystemInfo DTO for iComfort Wi‑Fi API.
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - Updated for openHAB 5.x compliance
 */
public final class SystemInfo {

    // ---------------------------------------------------------------------
    // JSON‑mapped fields
    // ---------------------------------------------------------------------

    @SerializedName("BuildingID")
    public @Nullable Integer buildingID;

    @SerializedName("Firmware_Ver")
    public @NonNull String firmwareVersion = "";

    @SerializedName("Gateway_SN")
    public String gatewaySN = "";

    @SerializedName("RegistrationCompleteFlag")
    public @Nullable Boolean registrationCompleteFlag;

    @SerializedName("Status")
    public @Nullable String status;

    @SerializedName("SystemID")
    public @Nullable Integer systemID;

    @SerializedName("System_Name")
    public @NonNull String systemName = "";

    @SerializedName("ZonesStatus")
    private @Nullable ZonesStatus zonesStatus;

    @SerializedName("GatewayInfo")
    private @Nullable GatewayInfo gatewayInfo;

    @SerializedName("GatewaysAlerts")
    private GatewaysAlerts gatewaysAlerts = new GatewaysAlerts();

    // ---------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------

    public SystemInfo(String systemName, String gatewaySN /* other args */) {
        this.systemName = Objects.requireNonNull(systemName);
        this.gatewaySN = Objects.requireNonNull(gatewaySN);
    }

    // ---------------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------------

    public ZonesStatus getZonesStatus() {
        ZonesStatus status = this.zonesStatus;
        if (status == null) {
            throw new IllegalStateException("ZonesStatus is required but was null in the API response");
        }
        return status;
    }

    public @Nullable ZonesStatus getZonesStatusOrNull() {
        return this.zonesStatus;
    }

    public @Nullable GatewayInfo getGatewayInfo() {
        return this.gatewayInfo;
    }

    public GatewaysAlerts getGatewaysAlerts() {
        return this.gatewaysAlerts;
    }

    // ---------------------------------------------------------------------
    // Setters
    // ---------------------------------------------------------------------

    public void setZonesStatus(@Nullable ZonesStatus zonesStatus) {
        this.zonesStatus = zonesStatus;
    }

    public void setGatewayInfo(@Nullable GatewayInfo gatewayInfo) {
        this.gatewayInfo = gatewayInfo;
    }

    public void setGatewaysAlerts(GatewaysAlerts gatewaysAlerts) {
        this.gatewaysAlerts = gatewaysAlerts;
    }

    // ---------------------------------------------------------------------
    // Fault Logic
    // ---------------------------------------------------------------------

    public boolean hasActiveFaults() {
        String currentStatus = this.status;
        return currentStatus == null || !"Normal".equalsIgnoreCase(currentStatus);
    }

    public String getActiveFault() {
        String currentStatus = this.status;
        return (currentStatus != null && !currentStatus.isEmpty()) ? currentStatus : "UNKNOWN";
    }
}
