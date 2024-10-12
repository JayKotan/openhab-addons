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
package org.openhab.binding.icomfortwifi.internal.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.openhab.binding.icomfortwifi.internal.api.models.response.JsonDateDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Provides access to (an optionally OAUTH based) API. Makes sure that all the necessary headers are set.
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Updated Imports and additional Null handeling. Updated API Access, both doRequest,
 *         doAuthenticatedRequest.
 */
@NonNullByDefault
public class ApiAccess {
    private static final int REQUEST_TIMEOUT_SECONDS = 5; // Timeout for requests in seconds
    private final Logger logger = LoggerFactory.getLogger(ApiAccess.class); // Logger for logging
    private final HttpClient httpClient; // HTTP client for making requests
    @Nullable
    volatile String userCredentials; // User credentials for authentication
    private final Gson gson; // Gson instance for JSON serialization/deserialization

    public ApiAccess(HttpClient httpClient) {
        // Set up Gson with a custom date deserializer
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDateDeserializer());
        this.gson = gsonBuilder.create();
        this.userCredentials = null; // Initialize in the constructor
        this.httpClient = httpClient; // Initialize HTTP client

        // Ensure the HttpClient is started
        try {
            if (this.httpClient.isStopped()) {
                this.httpClient.start(); // Start the client if it is not running
                logger.info("HttpClient started in ApiAccess constructor.");
            }
        } catch (Exception e) {
            logger.error("Failed to start HttpClient in ApiAccess constructor: {}", e.getMessage(), e);
        }
    }

    public void setUserCredentials(@Nullable String userCredentials) {
        this.userCredentials = userCredentials;
    }

    /**
     * Issues an HTTP request on the API's URL. Makes sure that the request is correctly formatted.
     *
     * @param method The HTTP method to use (POST, GET, ...)
     * @param url The URL to query
     * @param headers The optional additional headers to apply, can be null
     * @param requestData The optional request data to use, can be null
     * @param contentType The content type to use with the request data. Required when using requestData
     * @param outClass The class type to deserialize the response into, can be null
     * @return The deserialized response object or null
     * @throws TimeoutException Thrown when a request times out
     */
    public @Nullable <TOut> TOut doRequest(HttpMethod method, String url, Map<String, String> headers,
            @Nullable String requestData, String contentType, @Nullable Class<TOut> outClass) throws TimeoutException {
        logger.debug("Requesting: [{}]", url); // Log the request URL
        if (httpClient.isStopped()) {
            logger.error("HttpClient is stopped. Cannot send request.");
            return null; // or throw an exception
        }
        @Nullable
        TOut retVal = null;
        try {
            Request request = httpClient.newRequest(url).method(method); // Create a new request
            // Ensure headers are properly typed
            {
                headers.forEach(request::header); // Set headers on the request
            }
            if (requestData != null) {
                request.content(new StringContentProvider(requestData), contentType); // Set request content
            }
            ContentResponse response = request.timeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS).send(); // Send the
                                                                                                          // request
            logger.debug("Response: {}", response); // Log the response
            logger.debug("\n{}\n{}", response.getHeaders(), response.getContentAsString()); // Log headers and content

            // New logging for raw response content
            String reply = response.getContentAsString(); // Get the response content
            logger.debug("Raw response content: {}", reply); // Log the raw JSON response

            // Check the response status
            if ((response.getStatus() == HttpStatus.OK_200) || (response.getStatus() == HttpStatus.ACCEPTED_202)) {
                if (outClass != null) {
                    retVal = gson.fromJson(reply, outClass); // Deserialize the response to the specified class
                }
            } else if (response.getStatus() == HttpStatus.CREATED_201) {
                // success, nothing to return, ignore
            } else {
                logger.debug("Request failed with unexpected response code {}", response.getStatus()); // Log unexpected
                                                                                                       // response
            }
        } catch (ExecutionException e) {
            logger.debug("Error in handling request: ", e);
        } catch (InterruptedException e) {
            logger.debug("Handling request interrupted: ", e);
            Thread.currentThread().interrupt();
        }

        return retVal; // Return the result
    }

    /**
     * Issues an authenticated HTTP GET request on the API's URL, deserializing the response into the specified class.
     *
     * @param <TOut> The type of the expected response.
     * @param url The URL to query.
     * @param outClass The class type to deserialize the response into.
     * @return The deserialized response object or null.
     * @throws TimeoutException Thrown when a request times out.
     */
    public @Nullable <TOut> TOut doAuthenticatedGet(String url, Class<TOut> outClass) throws TimeoutException {
        return doAuthenticatedRequest(HttpMethod.GET, url, null, outClass); // Call the authenticated request method
    }

    /**
     * Issues an HTTP PUT request on the API's URL, using an object that is serialized to JSON as input.
     * Makes sure that the request is correctly formatted.
     *
     * @param url The URL to query
     * @param requestContainer The object to use as JSON data for the request
     * @throws TimeoutException Thrown when a request times out
     */
    public void doAuthenticatedPut(String url, @Nullable Object requestContainer) throws TimeoutException {
        doAuthenticatedRequest(HttpMethod.PUT, url, requestContainer, null); // Call the authenticated request method
    }

    /**
     * Issues an HTTP PUT request on the API's URL, using an object that is serialized to JSON as input.
     * Makes sure that the request is correctly formatted.*
     *
     * @param url The URL to query
     * @param requestContainer The object to use as JSON data for the request
     * @param outClass The type of the requested result
     * @throws TimeoutException Thrown when a request times out
     */
    public @Nullable <TOut> TOut doAuthenticatedPut(String url, Object requestContainer, Class<TOut> outClass)
            throws TimeoutException {
        return doAuthenticatedRequest(HttpMethod.PUT, url, requestContainer, outClass);
    }

    public @Nullable <TOut> TOut doAuthenticatedRequest(HttpMethod method, String url,
            @Nullable Object requestContainer, @Nullable Class<TOut> outClass) throws TimeoutException {

        // Initialize headers map
        Map<String, String> headers = new HashMap<>();

        // Safely get user credentials
        String authCredentials = userCredentials;

        if (authCredentials != null) {
            headers.put("Authorization", authCredentials); // Use credentials as-is without re-encoding
        } else {
            logger.warn("User credentials are null or empty.");
            return null; // Handle accordingly (consider throwing an exception if this is critical)
        }
        // Add additional headers
        headers.put("Accept", "application/json, application/xml, text/json, text/x-json, text/javascript, text/xml");
        headers.put("Accept-Charset", "utf-8");
        // Ensure the content type is always non-null
        // String contentType = requestContainer != null ? "application/json" : "application/x-www-form-urlencoded";
        // Make the request
        return doRequest(method, url, headers, requestContainer, "application/json", outClass);
    }

    /**
     * Issues an HTTP PUT request on the API's URL, using an object that is serialized to JSON as input.
     * Makes sure that the request is correctly formatted.
     *
     * @param method The HTTP method to use (POST, GET, ...)
     * @param url The URL to query
     * @param headers The optional additional headers to apply, can be null
     * @param requestContainer The object to use as JSON data for the request
     * @param outClass The type of the requested result
     * @return The result of the request or null
     * @throws TimeoutException Thrown when a request times out
     */
    private @Nullable <TOut> TOut doRequest(HttpMethod method, String url, Map<String, String> headers,
            @Nullable Object requestContainer, String contentType, @Nullable Class<TOut> outClass)
            throws TimeoutException {

        String json = requestContainer != null ? gson.toJson(requestContainer) : null; // Serialize the request
                                                                                       // container to JSON
        return doRequest(method, url, headers, json, contentType, outClass); // Call the main doRequest method
    }
}
