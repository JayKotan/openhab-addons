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

import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.RequestStatus;

import com.google.gson.annotations.SerializedName;

/**
 * Alias for a list of locations
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Updated to comply with OpenHab 4.2.
 */
@NonNullByDefault
public class BuildingsInfo {

    @SerializedName("ReturnStatus")
    public RequestStatus returnStatus;

    @SerializedName("Buildings")
    public ArrayList<Building> buildingInfo;

    public BuildingsInfo() {
        this.returnStatus = RequestStatus.UNKNOWN; // or another default value
        this.buildingInfo = new ArrayList<>(); // Initialize to an empty list
    }
}
