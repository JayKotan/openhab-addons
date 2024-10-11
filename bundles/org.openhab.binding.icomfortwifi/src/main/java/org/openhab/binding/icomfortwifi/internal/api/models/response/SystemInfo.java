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
package org.openhab.binding.icomfortwifi.internal.api.models.response;

// import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the system information
 *
 * @author Konstantin Panchenko - Initial contribution
 *
 */

@NonNullByDefault
public class SystemInfo {

    @SerializedName("BuildingID")
    public Integer buildingID = 0;

    @SerializedName("Firmware_Ver")
    public String firmwareVersion = "";

    @SerializedName("Gateway_SN")
    public String gatewaySN = "";

    @SerializedName("RegistrationCompleteFlag")
    public Boolean registrationCompleteFlag = Boolean.FALSE;

    @SerializedName("Status")
    public String status = "";

    @SerializedName("SystemID")
    public Integer systemID = 0;

    @SerializedName("System_Name")
    public String systemName = "";

    private final ZonesStatus zonesStatus;
    private final GatewayInfo gatewayInfo;
    private final GatewaysAlerts gatewaysAlerts;

    public SystemInfo(GatewayInfo gatewayInfo, GatewaysAlerts gatewaysAlerts, ZonesStatus zonesStatus) {
        this.gatewayInfo = gatewayInfo;
        this.gatewaysAlerts = gatewaysAlerts;
        this.zonesStatus = zonesStatus;
    }

    public ZonesStatus getZonesStatus() {
        return zonesStatus;
    }

    public GatewayInfo getGatewayInfo() {
        return gatewayInfo;
    }

    public GatewaysAlerts getGatewaysAlerts() {
        return gatewaysAlerts;
    }

    public boolean hasActiveFaults() {
        return false;
    }

    public String getActiveFault() {
        return status;
    }

    // public void setZonesStatus(ZonesStatus zonesStatus) {
    // this.zonesStatus = zonesStatus;
    // }

    // public void setGetewayInfo(GatewayInfo gatewayInfo) {
    // this.gatewayInfo = gatewayInfo;
    // }

    // public void setGetewaysAlerts(GatewaysAlerts gatewaysAlerts) {
    // this.gatewaysAlerts = gatewaysAlerts;
    // }
}
