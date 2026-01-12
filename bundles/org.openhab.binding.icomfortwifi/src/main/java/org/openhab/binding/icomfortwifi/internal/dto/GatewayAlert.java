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

import java.util.Date;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.AlertStatus;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the System Alert
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - Updated for openHAB 5.x compliance
 */
@SuppressWarnings("unused")
public class GatewayAlert {

    @SerializedName("Alarm_Description")
    public @Nullable String alarmDescription;

    @SerializedName("Alarm_Nbr")
    public @Nullable Integer alarmNbr;

    @SerializedName("Alarm_Type")
    public @Nullable String alarmType;

    @SerializedName("Alarm_Value")
    public @Nullable String alarmValue;

    @SerializedName("DateTime_Reset")
    public @Nullable Date dateTimeReset;

    @SerializedName("DateTime_Set")
    public @Nullable Date dateTimeSet;

    @SerializedName("Status")
    public @Nullable AlertStatus status;

    public GatewayAlert() {
    }
}
