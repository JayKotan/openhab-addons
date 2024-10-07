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
 * Response model for the location info
 *
 * @author Jasper van Zuijlen - Initial contribution
 *
 */
public class LocationInfo {

    @SerializedName("locationId")
    public String locationId;

    @SerializedName("name")
    public String name;

    @SerializedName("streetAddress")
    public String streetAddress;

    @SerializedName("city")
    public String city;

    @SerializedName("country")
    public String country;

    @SerializedName("postcode")
    public String postcode;

    @SerializedName("locationType")
    public String locationType;

    @SerializedName("useDaylightSaveSwitching")
    public boolean useDaylightSaveSwitching;

    @SerializedName("timeZone")
    public TimeZone timeZone;

    @SerializedName("locationOwner")
    public LocationOwner locationOwner;

    public String getLocationId() {
        return locationId;
    }

    public String getName() {
        return name;
    }
}
