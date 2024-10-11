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

// import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.RequestStatus;
import org.openhab.binding.icomfortwifi.internal.utils.NonNullArrayList;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the System Alert
 *
 * @author Konstantin Panchenko - Initial contribution
 *
 */
@NonNullByDefault
public class ZonesStatus {

    @SerializedName("ReturnStatus")
    public RequestStatus returnStatus;

    @SerializedName("tStatInfo")
    public NonNullArrayList<ZoneStatus> zoneStatus;

    public ZonesStatus() {
        this.returnStatus = RequestStatus.UNKNOWN; // Initialize with a default value
        this.zoneStatus = new NonNullArrayList<>(); // Initialize as an empty ArrayList
    }
}
