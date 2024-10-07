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
 * Response model for the schedule capabilities
 *
 * @author Jasper van Zuijlen - Initial contribution
 *
 */
public class ScheduleCapabilities {

    @SerializedName("maxSwitchpointsPerDay")
    public int maxSwitchpointsPerDay;

    @SerializedName("minSwitchpointsPerDay")
    public int minSwitchpointsPerDay;

    @SerializedName("setpointValueResolution")
    public double setpointValueResolution;

    @SerializedName("timingResolution")
    public String timingResolution;
}
