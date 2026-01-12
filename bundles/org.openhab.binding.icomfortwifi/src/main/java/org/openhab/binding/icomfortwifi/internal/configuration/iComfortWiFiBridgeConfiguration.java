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
package org.openhab.binding.icomfortwifi.internal.configuration;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * *
 * 
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Added @nullByDefault- updated Import section
 *
 */
@NonNullByDefault
public class iComfortWiFiBridgeConfiguration {
    public static final Integer DEFAULT_REFRESH = 30;

    public String username = "";
    public String password = "";
    public Integer refreshInterval;

    public iComfortWiFiBridgeConfiguration() {
        this.refreshInterval = DEFAULT_REFRESH;
    }
}
