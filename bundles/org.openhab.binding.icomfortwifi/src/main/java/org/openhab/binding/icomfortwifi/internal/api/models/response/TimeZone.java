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

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the time zone
 *
 * @author Jasper van Zuijlen - Initial contribution
 *
 */
public class TimeZone {

    @SerializedName("timeZoneId")
    public String timeZoneId;

    @SerializedName("displayName")
    public String displayName;

    @SerializedName("offsetMinutes")
    public int offsetMinutes;

    @SerializedName("currentOffsetMinutes")
    public int currentOffsetMinutes;

    @SerializedName("supportsDaylightSaving")
    public boolean supportsDaylightSaving;
}
