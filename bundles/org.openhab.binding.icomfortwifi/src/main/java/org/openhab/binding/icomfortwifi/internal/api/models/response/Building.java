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

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the building info
 *
 * @author Konstantin Panchenko - Initial contribution
 *
 */
@NonNullByDefault
public class Building {

    @SerializedName("Addr1")
    public String address_1 = ""; // Default empty string

    @SerializedName("Addr2")
    public String address_2 = ""; // Default empty string

    @SerializedName("Age_of_Building")
    public Integer buildingAge = 0; // Default to 0

    @SerializedName("BuildingAlert")
    public Boolean buildingAlert = false; // Default to false

    @SerializedName("BuildingID")
    public Integer buildingID = 0; // Default to 0

    @SerializedName("BuildingReminder")
    public Boolean buildingReminder = false; // Default to false

    @SerializedName("BuildingSize")
    public Integer buildingSize = 0; // Default to 0

    @SerializedName("BuildingStyle")
    public Integer buildingStyle = 0; // Default to 0

    @SerializedName("Building_Name")
    public String buildingName = ""; // Default empty string

    @SerializedName("City")
    public String city = ""; // Default empty string

    @SerializedName("Country")
    public String country = ""; // Default empty string

    @SerializedName("DealerAlerts_DlrWants")
    public Boolean dealerAlertsDlrWants = false; // Default to false

    @SerializedName("DealerAlerts_OwnerAllow")
    public Boolean dealerAlertsOwnerAllow = false; // Default to false

    @SerializedName("DealerID")
    public Integer dealerID = 0; // Default to 0

    @SerializedName("DealerReminder_DlrWants")
    public Boolean dealerReminderDlrWants = false; // Default to false

    @SerializedName("DealerReminder_OwnerAllow")
    public Boolean dealerReminderOwnerAllow = false; // Default to false

    @SerializedName("DealerTStatView")
    public Boolean dealerTStatView = false; // Default to false

    @SerializedName("DefaultBuilding")
    public Boolean defaultBuilding = false; // Default to false

    @SerializedName("Latitude")
    public Double latitude = 0.0; // Default to 0.0

    @SerializedName("Longitude")
    public Double longitude = 0.0; // Default to 0.0

    @SerializedName("NotificationEmail")
    public String notificationEmail = ""; // Default empty string

    @SerializedName("Number_of_Bedrooms")
    public Integer numberOfBedrooms = 0; // Default to 0

    @SerializedName("Number_of_Floors")
    public Integer numberOfFloors = 0; // Default to 0

    @SerializedName("Number_of_Occupants")
    public Integer numberOfOccupants = 0; // Default to 0

    @SerializedName("St_Prov")
    public String stateOrProvince = ""; // Default empty string

    @SerializedName("UserID")
    public String userID = ""; // Default empty string

    @SerializedName("UtilityCompany")
    public String utilityCompany = ""; // Default empty string

    @SerializedName("ZIP_PC")
    public String zipOrPostalCode = ""; // Default empty string

    public Building() {
    }
}
