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

import java.util.Date;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.AlertStatus;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the System Alert
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Added in headers.
 */
@NonNullByDefault
public class GatewayAlert {

    @SerializedName("Alarm_Description")
    public String alarmDescription;

    @SerializedName("Alarm_Nbr")
    public Integer alarmNbr;

    @SerializedName("Alarm_Type")
    public String alarmType;

    @SerializedName("Alarm_Value")
    public String alarmValue;

    @SerializedName("DateTime_Reset")
    public Date dateTimeReset;

    @SerializedName("DateTime_Set")
    public Date dateTimeSet;

    @SerializedName("Status")
    public AlertStatus status;

    public GatewayAlert(String alarmDescription, Integer alarmNbr, String alarmType, String alarmValue,
            Date dateTimeReset, Date dateTimeSet, AlertStatus status) {
        this.alarmDescription = alarmDescription;
        this.alarmNbr = alarmNbr;
        this.alarmType = alarmType;
        this.alarmValue = alarmValue;
        this.dateTimeReset = dateTimeReset;
        this.dateTimeSet = dateTimeSet;
        this.status = status;
    }

    // Default constructor
    public GatewayAlert() {
        this.alarmDescription = ""; // or throw an exception
        this.alarmNbr = 0; // or throw an exception
        this.alarmType = ""; // or throw an exception
        this.alarmValue = ""; // or throw an exception
        this.dateTimeReset = new Date(); // or throw an exception
        this.dateTimeSet = new Date(); // or throw an exception
        this.status = AlertStatus.UNKNOWN; // Assuming you have a default status
    }
}
