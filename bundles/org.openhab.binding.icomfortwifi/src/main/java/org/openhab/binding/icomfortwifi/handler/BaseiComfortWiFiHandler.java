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
package org.openhab.binding.icomfortwifi.handler;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.icomfortwifi.internal.api.models.response.SystemsInfo;
import org.openhab.binding.icomfortwifi.internal.configuration.iComfortWiFiThingConfiguration;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;

/**
 * Base class for an iComfortWiFi handler
 *
 * @author Konstantin Panchenko - Initial contribution
 */
public abstract class BaseiComfortWiFiHandler extends BaseThingHandler {
    private iComfortWiFiThingConfiguration configuration;

    public BaseiComfortWiFiHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        configuration = getConfigAs(iComfortWiFiThingConfiguration.class);
        checkConfig();
    }

    @Override
    public void dispose() {
        configuration = null;
    }

    public String getId() {
        if (configuration != null) {
            return configuration.id;
        }
        return null;
    }

    /**
     * Returns the configuration of the Thing
     *
     * @return The parsed configuration or null
     */
    protected iComfortWiFiThingConfiguration getiComfortWiFiThingConfig() {
        return configuration;
    }

    /**
     * Retrieves the bridge
     *
     * @return The iComfortWiFi bridge
     */
    protected iComfortWiFiBridgeHandler getiComfortWiFiBridge() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            return (iComfortWiFiBridgeHandler) bridge.getHandler();
        }

        return null;
    }

    /**
     * Retrieves the iComfortWiFi configuration from the bridge
     *
     * @return The current iComfortWiFi configuration
     */
    protected SystemsInfo getiComfortWiFiSystemsInfo() {
        iComfortWiFiBridgeHandler bridge = getiComfortWiFiBridge();
        if (bridge != null) {
            return bridge.getiComfortWiFiSystemsInfo();
        }

        return null;
    }

    /**
     * Retrieves the iComfortWiFi configuration from the bridge
     *
     * @return The current iComfortWiFi configuration
     */
    protected void requestUpdate() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            ((iComfortWiFiBridgeHandler) bridge).getiComfortWiFiSystemsInfo();
        }
    }

    /**
     * Updates the status of the iComfortWiFi thing when it changes
     *
     * @param newStatus The new status to update to
     */
    protected void updateiComfortWiFiThingStatus(ThingStatus newStatus) {
        updateiComfortWiFiThingStatus(newStatus, ThingStatusDetail.NONE, null);
    }

    /**
     * Updates the status of the iComfortWiFi thing when it changes
     *
     * @param newStatus The new status to update to
     * @param detail The status detail value
     * @param message The message to show with the status
     */
    protected void updateiComfortWiFiThingStatus(ThingStatus newStatus, ThingStatusDetail detail, String message) {
        // Prevent spamming the log file
        if (!newStatus.equals(getThing().getStatus())) {
            updateStatus(newStatus, detail, message);
        }
    }

    /**
     * Checks the configuration for validity, result is reflected in the status of the Thing
     *
     * @param configuration The configuration to check
     */
    private void checkConfig() {
        if (configuration == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Configuration is missing or corrupted");
        } else if (StringUtils.isEmpty(configuration.id)) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Id not configured");
        }
    }

    protected State getAsDateTimeTypeOrNull(@Nullable Date date) {
        if (date == null) {
            return UnDefType.NULL;
        }

        long offsetMillis = TimeZone.getDefault().getOffset(date.getTime());
        Instant instant = date.toInstant().plusMillis(offsetMillis);
        return new DateTimeType(ZonedDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId()));
    }
}
