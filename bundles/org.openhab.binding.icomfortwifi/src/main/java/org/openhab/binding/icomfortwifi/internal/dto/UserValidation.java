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
 * UserValidation DTO for iComfort Wi‑Fi API.
 *
 * Represents the response returned by the ValidateUser endpoint.
 */
@SuppressWarnings("unused")
public final class UserValidation {

    // ---------------------------------------------------------------------
    // JSON‑mapped fields
    // ---------------------------------------------------------------------

    @SerializedName("msg_code")
    public @Nullable RequestStatus msgCode;

    @SerializedName("msg_desc")
    public @Nullable String msgDesc = "";

    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

    public UserValidation() {
        // Gson populates fields; constructor ensures instantiability
    }

    // ---------------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------------

    public @Nullable RequestStatus getMsgCode() {
        return this.msgCode;
    }

    public @Nullable String getMsgDesc() {
        return this.msgDesc;
    }

    // ---------------------------------------------------------------------
    // Setters
    // ---------------------------------------------------------------------

    public void setMsgCode(@Nullable RequestStatus code) {
        this.msgCode = code;
    }

    public void setMsgDesc(@Nullable String desc) {
        this.msgDesc = desc;
    }
}
