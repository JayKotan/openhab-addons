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

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.Checks;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.binding.icomfortwifi.internal.dto.JsonDateDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@NonNullByDefault
public class ApiAccess {
    private static final int REQUEST_TIMEOUT_SECONDS = 5;
    private final Logger logger = Objects.requireNonNull(LoggerFactory.getLogger(ApiAccess.class));
    private final HttpClient httpClient;
    private final Gson gson;
    private volatile @Nullable String userCredentials;

    public ApiAccess(HttpClient httpClient) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDateDeserializer());
        this.gson = Checks.requireNonNull(gsonBuilder.create());
        this.httpClient = httpClient;
        this.httpClient.getProtocolHandlers().remove("www-authenticate");
    }

    public void setUserCredentials(@Nullable String userCredentials) {
        this.userCredentials = userCredentials;
    }

    // --- Core Request Methods ---

    /**
     * Helper that serializes the request container to JSON before calling the main doRequest.
     */
    private <@Nullable TOut> @Nullable TOut doRequest(HttpMethod method, String url, Map<String, String> headers,
            @Nullable Object requestContainer, String contentType, @Nullable Class<TOut> outClass)
            throws TimeoutException {
        String json = (requestContainer != null) ? this.gson.toJson(requestContainer) : null;
        return this.doRequest(method, url, headers, json, contentType, outClass);
    }

    /**
     * The worker method that performs the actual HTTP communication.
     */
    public <@Nullable TOut> @Nullable TOut doRequest(HttpMethod method, String url, Map<String, String> headers,
            @Nullable String requestData, String contentType, @Nullable Class<TOut> outClass) throws TimeoutException {

        this.logger.debug("Requesting: [{}]", url);
        TOut retVal = null;

        try {
            Request request = this.httpClient.newRequest(url).method(method);

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.header(entry.getKey(), entry.getValue());
            }

            if (requestData != null) {
                request.content(new StringContentProvider(contentType, requestData, StandardCharsets.UTF_8));
            }

            ContentResponse response = request.timeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS).send();
            String reply = response.getContentAsString();

            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Response Status: {} Content: {}", response.getStatus(), reply);
            }

            int status = response.getStatus();
            if (status >= 200 && status <= 202) {
                if (outClass != null && outClass != Void.class && reply != null && !reply.isEmpty()) {
                    retVal = this.gson.fromJson(reply, outClass);
                }
            } else {
                this.logger.debug("Request failed with unexpected response code {}", status);
            }
        } catch (InterruptedException e) {
            this.logger.error("Request interrupted: ", e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            this.logger.error("Error in handling request: ", e);
        }

        return retVal;
    }

    // --- Authenticated Helpers ---

    public <@Nullable TOut> @Nullable TOut doAuthenticatedGet(String url, Class<TOut> outClass)
            throws TimeoutException {
        return this.doAuthenticatedRequest(HttpMethod.GET, url, null, outClass);
    }

    @SuppressWarnings("unchecked")
    private static Class<@Nullable Void> nullableVoidClass() {
        return (Class<@Nullable Void>) (Class<?>) Void.class;
    }

    public void doAuthenticatedPut(String url, @Nullable Object requestBody, String contentType)
            throws TimeoutException {
        this.doAuthenticatedRequest(HttpMethod.PUT, url, requestBody, nullableVoidClass(), contentType);
    }

    public <@Nullable TOut> @Nullable TOut doAuthenticatedPut(String url, Object requestContainer, Class<TOut> outClass)
            throws TimeoutException {
        return this.doAuthenticatedRequest(HttpMethod.PUT, url, requestContainer, outClass);
    }

    public <@Nullable TOut> @Nullable TOut doAuthenticatedRequest(HttpMethod method, String url,
            @Nullable Object requestContainer, @Nullable Class<TOut> outClass) throws TimeoutException {
        return this.doAuthenticatedRequest(method, url, requestContainer, outClass, "application/json");
    }

    /**
     * Base authenticated request that allows specifying a Content-Type.
     */
    public <@Nullable TOut> @Nullable TOut doAuthenticatedRequest(HttpMethod method, String url,
            @Nullable Object requestContainer, @Nullable Class<TOut> outClass, String contentType)
            throws TimeoutException {

        String authCredentials = this.userCredentials;
        if (authCredentials == null) {
            this.logger.warn("User credentials are null. Authentication skipped for URL: {}", url);
            return null;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authCredentials);
        headers.put("Accept", "application/json");
        headers.put("Accept-Charset", "utf-8");

        return this.doRequest(method, url, headers, requestContainer, contentType, outClass);
    }
}
