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
package org.openhab.binding.icomfortwifi.internal.handler;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.icomfortwifi.internal.configuration.IComfortWiFiThingConfiguration;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes;
import org.openhab.binding.icomfortwifi.internal.dto.GatewayInfo;
import org.openhab.binding.icomfortwifi.internal.dto.SystemInfo;
import org.openhab.binding.icomfortwifi.internal.dto.SystemsInfo;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;

/**
 * 
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - Updated for openHAB 5.x compliance
 */
@NonNullByDefault
public abstract class BaseIComfortWiFiHandler extends BaseThingHandler {
    private IComfortWiFiThingConfiguration configuration = new IComfortWiFiThingConfiguration();

    public BaseIComfortWiFiHandler(Thing thing) {
        super(thing);
    }

    public void initialize() {
        this.configuration = getConfigAs(IComfortWiFiThingConfiguration.class);
        checkConfig();
    }

    @Override
    public void dispose() {
        // intentionally empty
    }

    public String getId() {
        String id = this.configuration.id;
        return id == null ? "" : id;
    }

    protected IComfortWiFiThingConfiguration getiComfortWiFiThingConfig() {
        return this.configuration;
    }

    protected @Nullable IComfortWiFiBridgeHandler getiComfortWiFiBridge() {
        Bridge bridge = this.getBridge();
        return bridge != null ? (IComfortWiFiBridgeHandler) bridge.getHandler() : null;
    }

    protected @Nullable SystemsInfo getiComfortWiFiSystemsInfo() {
        IComfortWiFiBridgeHandler bridge = this.getiComfortWiFiBridge();
        return bridge != null ? bridge.getiComfortWiFiSystemsInfo() : null;
    }

    protected void requestUpdate() {
        Bridge bridge = this.getBridge();
        if (bridge != null) {
            ((IComfortWiFiBridgeHandler) bridge).getiComfortWiFiSystemsInfo();
        }
    }

    protected void updateiComfortWiFiThingStatus(ThingStatus newStatus) {
        this.updateiComfortWiFiThingStatus(newStatus, ThingStatusDetail.NONE, null);
    }

    protected void updateiComfortWiFiThingStatus(ThingStatus newStatus, ThingStatusDetail detail,
            @Nullable String message) {
        if (!newStatus.equals(this.getThing().getStatus())) {
            this.updateStatus(newStatus, detail, message);
        }
    }

    protected @Nullable GatewayInfo findGatewayInfoForZone(SystemInfo[] systems, String gatewaySN) {
        for (SystemInfo system : systems) {
            if (Objects.equals(system.gatewaySN, gatewaySN)) {
                return system.getGatewayInfo();
            }
        }
        return null;
    }

    protected boolean isMetric(CustomTypes.@Nullable TempUnits unit) {
        return unit != null && unit.ordinal() == 1;
    }

    private void checkConfig() {
        String currentId = this.configuration.id;
        if (currentId != null && !currentId.isEmpty()) {
            this.updateStatus(ThingStatus.ONLINE);
        } else {
            this.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Id not configured");
        }
    }

    protected State getAsDateTimeTypeOrNull(@Nullable Date date) {
        if (date == null) {
            return UnDefType.NULL;
        }

        int offsetMillis = TimeZone.getDefault().getOffset(date.getTime());
        Instant instant = date.toInstant().plusMillis(offsetMillis);
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId());

        return new DateTimeType(zdt);
    }
}
