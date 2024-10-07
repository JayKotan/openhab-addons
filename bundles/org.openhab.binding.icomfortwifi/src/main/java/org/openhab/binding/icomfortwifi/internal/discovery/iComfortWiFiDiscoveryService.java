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
/**
  * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.icomfortwifi.internal.discovery;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.icomfortwifi.internal.api.models.response.SystemInfo;
import org.openhab.binding.icomfortwifi.internal.api.models.response.ZoneStatus;
import org.openhab.binding.icomfortwifi.internal.handler.iComfortWiFiAccountStatusListener;
import org.openhab.binding.icomfortwifi.internal.handler.iComfortWiFiBridgeHandler;
import org.openhab.binding.icomfortwifi.internal.iComfortWiFiBindingConstants;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link iComfortWiFiDiscoveryService} class is capable of discovering the available data from iComfortWiFi
 *
 * @author Konstantin Panchenko - Initial contribution
 *
 */
@NonNullByDefault
public class iComfortWiFiDiscoveryService extends AbstractDiscoveryService
        implements iComfortWiFiAccountStatusListener {
    public final Logger logger = LoggerFactory.getLogger(iComfortWiFiDiscoveryService.class);
    public static final int TIMEOUT = 5;

    public iComfortWiFiBridgeHandler bridge;
    public ThingUID bridgeUID;

    public iComfortWiFiDiscoveryService(iComfortWiFiBridgeHandler bridge) {
        super(iComfortWiFiBindingConstants.SUPPORTED_THING_TYPES_UIDS, TIMEOUT);

        this.bridge = bridge;
        this.bridgeUID = this.bridge.getThing().getUID();
        this.bridge.addAccountStatusListener(this);
    }

    @Override
    protected void startScan() {
        discoverDevices();
    }

    @Override
    protected void startBackgroundDiscovery() {
        discoverDevices();
    }

    @Override
    protected synchronized void stopScan() {
        super.stopScan();
        removeOlderResults(getTimestampOfLastScan());
    }

    @Override
    public void accountStatusChanged(ThingStatus status) {
        if (status == ThingStatus.ONLINE) {
            discoverDevices();
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
        bridge.removeAccountStatusListener(this);
    }

    //
    public void discoverDevices() {
        if (bridge.getThing().getStatus() != ThingStatus.ONLINE) {
            logger.debug("iComfortWiFi Gateway not online, scanning postponed");
            return;
        }

        for (SystemInfo systemInfo : bridge.getiComfortWiFiSystemsInfo().systemInfo) {
            addSystemDiscoveryResult(systemInfo);
            for (ZoneStatus zone : systemInfo.getZonesStatus().zoneStatus) {
                addZoneDiscoveryResult(systemInfo.systemName, zone);
            }
        }

        stopScan();
    }

    public void addSystemDiscoveryResult(SystemInfo systemInfo) {
        String id = systemInfo.gatewaySN;
        String name = systemInfo.systemName;
        ThingUID thingUID = new ThingUID(iComfortWiFiBindingConstants.THING_TYPE_ICOMFORT_THERMOSTAT, bridgeUID, id);

        Map<String, Object> properties = new HashMap<>(2);
        properties.put(iComfortWiFiBindingConstants.PROPERTY_ID, id);
        properties.put(iComfortWiFiBindingConstants.PROPERTY_NAME, name);

        addDiscoveredThing(thingUID, properties, name);
    }

    public void addZoneDiscoveryResult(String systemName, ZoneStatus zone) {
        String zoneID = zone.getZoneID();
        String name = zone.zoneName + " (" + systemName + ")";
        ThingUID thingUID = new ThingUID(iComfortWiFiBindingConstants.THING_TYPE_ICOMFORT_ZONE, bridgeUID, zoneID);

        Map<String, Object> properties = new HashMap<>(2);
        properties.put(iComfortWiFiBindingConstants.PROPERTY_ID, zoneID.toString());
        properties.put(iComfortWiFiBindingConstants.PROPERTY_NAME, name);

        addDiscoveredThing(thingUID, properties, name);
    }

    public void addDiscoveredThing(ThingUID thingUID, Map<String, Object> properties, String displayLabel) {
        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                .withBridge(bridgeUID).withLabel(displayLabel).build();
        thingDiscovered(discoveryResult);
    };
}
