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

import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.RequestStatus;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the user profile
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - Updated for openHAB 5.x compliance
 */
@SuppressWarnings("unused")
public class OwnerProfileInfo {

    @SerializedName("FirstName")
    public String firstName = "";

    @SerializedName("LastName")
    public String lastName = "";

    @SerializedName("MobilePhone")
    public String mobilePhone = "";

    @SerializedName("NewGatewayPending")
    public Boolean newGatewayPending = false;

    @SerializedName("Phone")
    public String phone = "";

    @SerializedName("PwdFlag")
    public Boolean pwdFlag = false;

    @SerializedName("RegistrationComplete")
    public Boolean registrationComplete = false;

    @SerializedName("ReturnStatus")
    public RequestStatus returnStatus = RequestStatus.SUCCESS;

    @SerializedName("TCInComplete")
    public Boolean tcInComplete = false;

    @SerializedName("UserID")
    public String userID = "";

    @SerializedName("eMail")
    public String eMail = "";

    public OwnerProfileInfo() {
    }
}
