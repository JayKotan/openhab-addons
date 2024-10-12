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

// import java.util.List;

// import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.RequestStatus;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the system information
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Updated to comply with OpenHab 4.2.
 *
 */

public class SystemInfo {

    public SystemInfo() {
    }

    @SerializedName("BuildingID")
    public Integer buildingID;

    @SerializedName("Firmware_Ver")
    public String firmwareVersion;

    @SerializedName("Gateway_SN")
    public String gatewaySN;

    @SerializedName("RegistrationCompleteFlag")
    public Boolean registrationCompleteFlag;

    @SerializedName("Status")
    public String status;

    @SerializedName("SystemID")
    public Integer systemID;

    @SerializedName("System_Name")
    public String systemName;

    private ZonesStatus zonesStatus;

    public ZonesStatus getZonesStatus() {
        return zonesStatus;
    }

    public void setZonesStatus(ZonesStatus zonesStatus) {
        this.zonesStatus = zonesStatus;
    }

    private GatewayInfo gatewayInfo;

    public GatewayInfo getGatewayInfo() {
        return gatewayInfo;
    }

    public void setGetewayInfo(GatewayInfo gatewayInfo) {
        this.gatewayInfo = gatewayInfo;
    }

    private GatewaysAlerts gatewaysAlerts;

    public GatewaysAlerts getGatewaysAlerts() {
        return gatewaysAlerts;
    }

    public void setGetewaysAlerts(GatewaysAlerts gatewaysAlerts) {
        this.gatewaysAlerts = gatewaysAlerts;
    }

    public boolean hasActiveFaults() { // Always return false, implemented for compatibility and future use.
        return false;
    }

    public String getActiveFault() { // Not used, don't know status values
        return status;
    }
}
