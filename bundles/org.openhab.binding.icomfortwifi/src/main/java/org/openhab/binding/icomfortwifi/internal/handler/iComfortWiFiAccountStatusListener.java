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
package org.openhab.binding.icomfortwifi.internal.handler;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingStatus;

/**
 * Interface for a listener of the iComfortWiFi account status
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Updated to comply with OpenHab 4.2.
 */
@NonNullByDefault
public interface iComfortWiFiAccountStatusListener {

    /**
     * Notifies the client that the status has changed.
     *
     * @param status The new status of the account thing
     */
    public void accountStatusChanged(ThingStatus status);
}
