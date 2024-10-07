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

import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.AlertStatus;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the System Alert
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Added in headers.
 */
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

    public GatewayAlert() {
    }
}
