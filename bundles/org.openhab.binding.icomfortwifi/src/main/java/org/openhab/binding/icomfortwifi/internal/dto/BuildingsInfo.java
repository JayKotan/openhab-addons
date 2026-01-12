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

import java.util.ArrayList;
import java.util.List;

import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.RequestStatus;

import com.google.gson.annotations.SerializedName;

/**
 * Alias for a list of locations
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Updated for openHAB 5.x compliance
 */
@SuppressWarnings("unused")
public class BuildingsInfo {

    @SerializedName("ReturnStatus")
    public RequestStatus returnStatus = RequestStatus.SUCCESS;

    @SerializedName("Buildings")
    public List<Building> buildingInfo = new ArrayList<>();

    public BuildingsInfo() {
    }
}
