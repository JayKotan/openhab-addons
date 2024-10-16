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

import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.RequestStatus;

import com.google.gson.annotations.SerializedName;

/**
 * Alias for a list of locations
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Updated Imports
 */

public class BuildingsInfo {

    @SerializedName("ReturnStatus")
    public RequestStatus returnStatus;

    @SerializedName("Buildings")
    public ArrayList<Building> buildingInfo;

    public BuildingsInfo() {
    }
}
