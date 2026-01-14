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

import java.lang.reflect.Type;
import java.util.Date;

import org.eclipse.jdt.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Custom Gson deserializer for iComfort's Microsoft-style date format.
 *
 * Example format: "/Date(1758754260000+0000)/"
 */
@SuppressWarnings("unused")
public final class JsonDateDeserializer implements JsonDeserializer<Date> {

    // ---------------------------------------------------------------------
    // Constructor (prevent instantiation)
    // ---------------------------------------------------------------------

    public JsonDateDeserializer() {
        // Default constructor required by Gson
    }

    // ---------------------------------------------------------------------
    // Deserialization Logic
    // ---------------------------------------------------------------------

    @Override
    public @Nullable Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json.isJsonNull()) {
            return null;
        }

        String raw = json.getAsString(); // e.g. "/Date(1758754260000+0000)/"

        try {
            int start = raw.indexOf('(') + 1;
            int end = raw.indexOf(')');
            String inner = raw.substring(start, end); // "1758754260000+0000"

            // Strip timezone if present
            int plusIndex = inner.indexOf('+');
            if (plusIndex > 0) {
                inner = inner.substring(0, plusIndex);
            }
            long millis = Long.parseLong(inner);
            return new Date(millis);
        } catch (Exception e) {
            throw new JsonParseException("Invalid date format: " + raw, e);
        }
    }
}
