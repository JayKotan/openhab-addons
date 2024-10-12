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
package org.openhab.binding.icomfortwifi.internal.configuration;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Contains the common configuration definition of an iComfortWiFi Thing
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Updated to comply with OpenHab 4.2.
 *
 */
@NonNullByDefault
public class iComfortWiFiThingConfiguration {
    public String id = ""; // GatewaySN for System ID or GatewaySN_ZoneID for Zonde ID
    public String name = ""; // System or Zone name (optional)
}
