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
package org.openhab.binding.icomfortwifi.internal.utils;

import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Create a Null Array List
 *
 * @author Jason Kotan - Initial contribution
 *
 */
@NonNullByDefault
public class NonNullArrayList<E> extends ArrayList<E> {
    // Add serialVersionUID field
    private static final long serialVersionUID = 1L;

    @Override
    public boolean add(E e) {
        return super.add(e);
    }

    @Override
    public E get(int index) {
        E element = super.get(index);
        if (element == null) {
            throw new IllegalStateException("List element at index " + index + " is unexpectedly null");
        }
        return element;
    }
}
