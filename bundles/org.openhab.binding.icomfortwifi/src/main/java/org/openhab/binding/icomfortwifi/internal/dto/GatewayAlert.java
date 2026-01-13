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
 * GatewayAlert DTO for iComfort Wi‑Fi API.
 *
 * Represents a single system-level alert raised by the gateway.
 */
@SuppressWarnings("unused")
public final class GatewayAlert {

    // ---------------------------------------------------------------------
    // JSON‑mapped fields
    // ---------------------------------------------------------------------

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

    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

    public GatewayAlert() {
        // Gson populates fields; constructor ensures instantiability
    }

    // ---------------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------------

    public @Nullable String getAlarmDescription() {
        return this.alarmDescription;
    }

    public @Nullable Integer getAlarmNbr() {
        return this.alarmNbr;
    }

    public @Nullable String getAlarmType() {
        return this.alarmType;
    }

    public @Nullable String getAlarmValue() {
        return this.alarmValue;
    }

    public @Nullable Date getDateTimeReset() {
        return this.dateTimeReset;
    }

    public @Nullable Date getDateTimeSet() {
        return this.dateTimeSet;
    }

    public @Nullable AlertStatus getStatus() {
        return this.status;
    }

    // ---------------------------------------------------------------------
    // Setters
    // ---------------------------------------------------------------------

    public void setAlarmDescription(@Nullable String value) {
        this.alarmDescription = value;
    }

    public void setAlarmNbr(@Nullable Integer value) {
        this.alarmNbr = value;
    }

    public void setAlarmType(@Nullable String value) {
        this.alarmType = value;
    }

    public void setAlarmValue(@Nullable String value) {
        this.alarmValue = value;
    }

    public void setDateTimeReset(@Nullable Date value) {
        this.dateTimeReset = value;
    }

    public void setDateTimeSet(@Nullable Date value) {
        this.dateTimeSet = value;
    }

    public void setStatus(@Nullable AlertStatus value) {
        this.status = value;
    }
}
