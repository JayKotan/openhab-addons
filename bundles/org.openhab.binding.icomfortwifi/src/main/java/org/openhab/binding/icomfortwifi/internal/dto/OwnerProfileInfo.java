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
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.RequestStatus;

import com.google.gson.annotations.SerializedName;

/**
 * OwnerProfileInfo DTO for iComfort Wi‑Fi API.
 *
 * Represents the authenticated user's profile information.
 */
@SuppressWarnings("unused")
public final class OwnerProfileInfo {

    // ---------------------------------------------------------------------
    // JSON‑mapped fields
    // ---------------------------------------------------------------------

    @SerializedName("FirstName")
    public @Nullable String firstName = "";

    @SerializedName("LastName")
    public @Nullable String lastName = "";

    @SerializedName("MobilePhone")
    public @Nullable String mobilePhone = "";

    @SerializedName("NewGatewayPending")
    public @Nullable Boolean newGatewayPending = false;

    @SerializedName("Phone")
    public @Nullable String phone = "";

    @SerializedName("PwdFlag")
    public @Nullable Boolean pwdFlag = false;

    @SerializedName("RegistrationComplete")
    public @Nullable Boolean registrationComplete = false;

    @SerializedName("ReturnStatus")
    public @Nullable RequestStatus returnStatus = RequestStatus.SUCCESS;

    @SerializedName("TCInComplete")
    public @Nullable Boolean tcInComplete = false;

    @SerializedName("UserID")
    public @Nullable String userID = "";

    @SerializedName("eMail")
    public @Nullable String eMail = "";

    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

    public OwnerProfileInfo() {
        // Gson populates fields; constructor ensures instantiability
    }

    // ---------------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------------

    public @Nullable String getFirstName() {
        return this.firstName;
    }

    public @Nullable String getLastName() {
        return this.lastName;
    }

    public @Nullable String getMobilePhone() {
        return this.mobilePhone;
    }

    public @Nullable Boolean getNewGatewayPending() {
        return this.newGatewayPending;
    }

    public @Nullable String getPhone() {
        return this.phone;
    }

    public @Nullable Boolean getPwdFlag() {
        return this.pwdFlag;
    }

    public @Nullable Boolean getRegistrationComplete() {
        return this.registrationComplete;
    }

    public @Nullable RequestStatus getReturnStatus() {
        return this.returnStatus;
    }

    public @Nullable Boolean getTcInComplete() {
        return this.tcInComplete;
    }

    public @Nullable String getUserID() {
        return this.userID;
    }

    public @Nullable String getEmail() {
        return this.eMail;
    }

    // ---------------------------------------------------------------------
    // Setters
    // ---------------------------------------------------------------------

    public void setFirstName(@Nullable String value) {
        this.firstName = value;
    }

    public void setLastName(@Nullable String value) {
        this.lastName = value;
    }

    public void setMobilePhone(@Nullable String value) {
        this.mobilePhone = value;
    }

    public void setNewGatewayPending(@Nullable Boolean value) {
        this.newGatewayPending = value;
    }

    public void setPhone(@Nullable String value) {
        this.phone = value;
    }

    public void setPwdFlag(@Nullable Boolean value) {
        this.pwdFlag = value;
    }

    public void setRegistrationComplete(@Nullable Boolean value) {
        this.registrationComplete = value;
    }

    public void setReturnStatus(@Nullable RequestStatus value) {
        this.returnStatus = value;
    }

    public void setTcInComplete(@Nullable Boolean value) {
        this.tcInComplete = value;
    }

    public void setUserID(@Nullable String value) {
        this.userID = value;
    }

    public void setEmail(@Nullable String value) {
        this.eMail = value;
    }
}
