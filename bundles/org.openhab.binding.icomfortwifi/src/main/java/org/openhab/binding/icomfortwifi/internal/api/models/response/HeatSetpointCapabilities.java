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

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the heat set point capabilities
 *
 * @author Jasper van Zuijlen - Initial contribution
 *
 */
public class HeatSetpointCapabilities {

    @SerializedName("maxHeatSetpoint")
    public double maxHeatSetpoint;

    @SerializedName("minHeatSetpoint")
    public double minHeatSetpoint;

    @SerializedName("valueResolution")
    public double valueResolution;

    @SerializedName("allowedSetpointModes")
    public List<String> allowedSetpointModes;

    @SerializedName("maxDuration")
    public String maxDuration;

    @SerializedName("timingResolution")
    public String timingResolution;
}
