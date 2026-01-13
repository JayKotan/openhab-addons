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
 * Building DTO for iComfort Wi‑Fi API.
 *
 * Represents metadata about a user's building/home.
 */
@SuppressWarnings("unused")
public final class Building {

    // ---------------------------------------------------------------------
    // JSON‑mapped fields
    // ---------------------------------------------------------------------

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

    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

    public Building() {
        // Gson populates fields; constructor ensures instantiability
    }

    // ---------------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------------

    public @Nullable String getAddress1() {
        return this.address_1;
    }

    public @Nullable String getAddress2() {
        return this.address_2;
    }

    public @Nullable Integer getBuildingAge() {
        return this.buildingAge;
    }

    public @Nullable Boolean getBuildingAlert() {
        return this.buildingAlert;
    }

    public @Nullable Integer getBuildingID() {
        return this.buildingID;
    }

    public @Nullable Boolean getBuildingReminder() {
        return this.buildingReminder;
    }

    public @Nullable Integer getBuildingSize() {
        return this.buildingSize;
    }

    public @Nullable Integer getBuildingStyle() {
        return this.buildingStyle;
    }

    public @Nullable String getBuildingName() {
        return this.buildingName;
    }

    public @Nullable String getCity() {
        return this.city;
    }

    public @Nullable String getCountry() {
        return this.country;
    }

    public @Nullable Boolean getDealerAlertsDlrWants() {
        return this.dealerAlertsDlrWants;
    }

    public @Nullable Boolean getDealerAlertsOwnerAllow() {
        return this.dealerAlertsOwnerAllow;
    }

    public @Nullable Integer getDealerID() {
        return this.dealerID;
    }

    public @Nullable Boolean getDealoerReminderDlrWants() {
        return this.dealoerReminderDlrWants;
    }

    public @Nullable Boolean getDealerReminderOwnerAllow() {
        return this.dealerReminderOwnerAllow;
    }

    public @Nullable Boolean getDealerTStatView() {
        return this.dealerTStatView;
    }

    public @Nullable Boolean getDefaultBuilding() {
        return this.defaultBuilding;
    }

    public @Nullable Double getLatitude() {
        return this.latitude;
    }

    public @Nullable Double getLongitude() {
        return this.longitude;
    }

    public @Nullable String getNotificationEmail() {
        return this.notificationEmail;
    }

    public @Nullable Integer getNumberOfBedrooms() {
        return this.numberOfBedrooms;
    }

    public @Nullable Integer getNumberOfFloors() {
        return this.numberOfFloors;
    }

    public @Nullable Integer getNumberOfOccupants() {
        return this.numberOfOccupants;
    }

    public @Nullable String getStateOrProvince() {
        return this.stateOrProvince;
    }

    public @Nullable String getUserID() {
        return this.userID;
    }

    public @Nullable String getUtilityCompany() {
        return this.utilityCompany;
    }

    public @Nullable String getZipOrPostalCode() {
        return this.zipOrPostalCode;
    }

    // ---------------------------------------------------------------------
    // Setters
    // ---------------------------------------------------------------------

    public void setAddress1(@Nullable String value) {
        this.address_1 = value;
    }

    public void setAddress2(@Nullable String value) {
        this.address_2 = value;
    }

    public void setBuildingAge(@Nullable Integer value) {
        this.buildingAge = value;
    }

    public void setBuildingAlert(@Nullable Boolean value) {
        this.buildingAlert = value;
    }

    public void setBuildingID(@Nullable Integer value) {
        this.buildingID = value;
    }

    public void setBuildingReminder(@Nullable Boolean value) {
        this.buildingReminder = value;
    }

    public void setBuildingSize(@Nullable Integer value) {
        this.buildingSize = value;
    }

    public void setBuildingStyle(@Nullable Integer value) {
        this.buildingStyle = value;
    }

    public void setBuildingName(@Nullable String value) {
        this.buildingName = value;
    }

    public void setCity(@Nullable String value) {
        this.city = value;
    }

    public void setCountry(@Nullable String value) {
        this.country = value;
    }

    public void setDealerAlertsDlrWants(@Nullable Boolean value) {
        this.dealerAlertsDlrWants = value;
    }

    public void setDealerAlertsOwnerAllow(@Nullable Boolean value) {
        this.dealerAlertsOwnerAllow = value;
    }

    public void setDealerID(@Nullable Integer value) {
        this.dealerID = value;
    }

    public void setDealoerReminderDlrWants(@Nullable Boolean value) {
        this.dealoerReminderDlrWants = value;
    }

    public void setDealerReminderOwnerAllow(@Nullable Boolean value) {
        this.dealerReminderOwnerAllow = value;
    }

    public void setDealerTStatView(@Nullable Boolean value) {
        this.dealerTStatView = value;
    }

    public void setDefaultBuilding(@Nullable Boolean value) {
        this.defaultBuilding = value;
    }

    public void setLatitude(@Nullable Double value) {
        this.latitude = value;
    }

    public void setLongitude(@Nullable Double value) {
        this.longitude = value;
    }

    public void setNotificationEmail(@Nullable String value) {
        this.notificationEmail = value;
    }

    public void setNumberOfBedrooms(@Nullable Integer value) {
        this.numberOfBedrooms = value;
    }

    public void setNumberOfFloors(@Nullable Integer value) {
        this.numberOfFloors = value;
    }

    public void setNumberOfOccupants(@Nullable Integer value) {
        this.numberOfOccupants = value;
    }

    public void setStateOrProvince(@Nullable String value) {
        this.stateOrProvince = value;
    }

    public void setUserID(@Nullable String value) {
        this.userID = value;
    }

    public void setUtilityCompany(@Nullable String value) {
        this.utilityCompany = value;
    }

    public void setZipOrPostalCode(@Nullable String value) {
        this.zipOrPostalCode = value;
    }
}
