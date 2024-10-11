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
package org.openhab.binding.icomfortwifi.internal;

import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Provides an interface for a delegate that can throw a timeout
 *
 * @author Konstantin Panchenko - Initial contribution
 *
 */
@NonNullByDefault
public interface RunnableWithTimeout {

    public abstract void run() throws TimeoutException;
}
