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
package org.openhab.binding.icomfortwifi.internal.api;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Exception for errors from the API Client.
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Added @nullByDefault - updated import section
 */
@NonNullByDefault
@SuppressWarnings("unused")
public class IComfortWiFiApiClientException extends Exception {

    private static final long serialVersionUID = 9991L;

    public IComfortWiFiApiClientException() {
    }

    public IComfortWiFiApiClientException(String message) {
        super(message);
    }

    public IComfortWiFiApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
