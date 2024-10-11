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
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.RequestStatus;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the user profile
 *
 * @author Konstantin Panchenko - Initial contribution
 *
 */
@NonNullByDefault
public class OwnerProfileInfo {

    @SerializedName("FirstName")
    public String firstName;

    @SerializedName("LastName")
    public String lastName;

    @SerializedName("MobilePhone")
    public String mobilePhone;

    @SerializedName("NewGatewayPending")
    public Boolean newGatewayPending;

    @SerializedName("Phone")
    public String phone;

    @SerializedName("PwdFlag")
    public Boolean pwdFlag;

    @SerializedName("RegistrationComplete")
    public Boolean registrationComplete;

    @SerializedName("ReturnStatus")
    public RequestStatus returnStatus;

    @SerializedName("TCInComplete")
    public Boolean tcInComplete;

    @SerializedName("UserID")
    public String userID;

    @SerializedName("eMail")
    public String eMail;

    public OwnerProfileInfo() {
        this.firstName = ""; // Default empty string or adjust as needed
        this.lastName = ""; // Default empty string or adjust as needed
        this.mobilePhone = ""; // Default empty string or adjust as needed
        this.newGatewayPending = false; // Default value for Boolean
        this.phone = ""; // Default empty string or adjust as needed
        this.pwdFlag = false; // Default value for Boolean
        this.registrationComplete = false; // Default value for Boolean
        this.returnStatus = RequestStatus.UNKNOWN; // or another default value
        this.tcInComplete = false; // Default value for Boolean
        this.userID = ""; // Default empty string or adjust as needed
        this.eMail = ""; // Default empty string or adjust as needed
    }
}
