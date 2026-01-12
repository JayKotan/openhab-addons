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

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the building info
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - Updated for openHAB 5.x compliance
 */
@SuppressWarnings("unused")
public class Building {

    @SerializedName("Addr1")
    public @Nullable String address_1;

    @SerializedName("Addr2")
    public @Nullable String address_2;

    @SerializedName("Age_of_Building")
    public @Nullable Integer buildingAge;

    @SerializedName("BuildingAlert")
    public @Nullable Boolean buildingAlert;

    @SerializedName("BuildingID")
    public @Nullable Integer buildingID;

    @SerializedName("BuildingReminder")
    public @Nullable Boolean buildingReminder;

    @SerializedName("BuildingSize")
    public @Nullable Integer buildingSize;

    @SerializedName("BuildingStyle")
    public @Nullable Integer buildingStyle;

    @SerializedName("Building_Name")
    public @Nullable String buildingName;

    @SerializedName("City")
    public @Nullable String city;

    @SerializedName("Country")
    public @Nullable String country;

    @SerializedName("DealerAlerts_DlrWants")
    public @Nullable Boolean dealerAlertsDlrWants;

    @SerializedName("DealerAlerts_OwnerAllow")
    public @Nullable Boolean dealerAlertsOwnerAllow;

    @SerializedName("DealerID")
    public @Nullable Integer dealerID;

    @SerializedName("DealerReminder_DlrWants")
    public @Nullable Boolean dealoerReminderDlrWants;

    @SerializedName("DealerReminder_OwnerAllow")
    public @Nullable Boolean dealerReminderOwnerAllow;

    @SerializedName("DealerTStatView")
    public @Nullable Boolean dealerTStatView;

    @SerializedName("DefaultBuilding")
    public @Nullable Boolean defaultBuilding;

    @SerializedName("Latitude")
    public @Nullable Double latitude;

    @SerializedName("Longitude")
    public @Nullable Double longitude;

    @SerializedName("NotificationEmail")
    public @Nullable String notificationEmail;

    @SerializedName("Number_of_Bedrooms")
    public @Nullable Integer numberOfBedrooms;

    @SerializedName("Number_of_Floors")
    public @Nullable Integer numberOfFloors;

    @SerializedName("Number_of_Occupants")
    public @Nullable Integer numberOfOccupants;

    @SerializedName("St_Prov")
    public @Nullable String stateOrProvince;

    @SerializedName("UserID")
    public @Nullable String userID;

    @SerializedName("UtilityCompany")
    public @Nullable String utilityCompany;

    @SerializedName("ZIP_PC")
    public @Nullable String zipOrPostalCode;

    public Building() {
    }
}
