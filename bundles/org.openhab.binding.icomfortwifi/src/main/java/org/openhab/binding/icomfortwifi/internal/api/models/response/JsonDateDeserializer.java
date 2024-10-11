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

import java.lang.reflect.Type;
import java.util.Date;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
// import org.eclipse.jdt.annotation.Nullable;

/**
 * Response model for the System Alert
 *
 * @author Konstantin Panchenko - Initial contribution
 *
 */

@NonNullByDefault
public class JsonDateDeserializer implements JsonDeserializer<Date> {

    @Override
    public @org.eclipse.jdt.annotation.NonNull Date deserialize(@org.eclipse.jdt.annotation.Nullable JsonElement json,
            @org.eclipse.jdt.annotation.Nullable Type typeOfT,
            @org.eclipse.jdt.annotation.Nullable JsonDeserializationContext context) throws JsonParseException {
        if (json == null || !json.isJsonPrimitive()) {
            return new Date(); // Fallback to current date if json is null
        }

        try {
            String s = json.getAsJsonPrimitive().getAsString();
            long l = Long.parseLong(s.substring(6, s.length() - 7));
            return new Date(l);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            // Log or handle the error if needed
            return new Date(); // Fallback to current date on error
        }
    }
}
