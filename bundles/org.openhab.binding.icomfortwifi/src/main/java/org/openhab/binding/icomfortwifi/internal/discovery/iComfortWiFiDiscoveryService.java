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
package org.openhab.binding.icomfortwifi.internal.discovery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.icomfortwifi.internal.dto.SystemInfo;
import org.openhab.binding.icomfortwifi.internal.dto.ZoneStatus;
import org.openhab.binding.icomfortwifi.internal.dto.ZonesStatus;
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
 * @author Jason Kotan - Added @nullByDefault- updated Import section
 *
 */

@NonNullByDefault
public class iComfortWiFiDiscoveryService extends AbstractDiscoveryService
        implements iComfortWiFiAccountStatusListener {
    @SuppressWarnings("null")
    private final Logger logger = LoggerFactory.getLogger(iComfortWiFiDiscoveryService.class);
    private static final int TIMEOUT = 5;
    private final iComfortWiFiBridgeHandler bridge;
    private final ThingUID bridgeUID;

    public iComfortWiFiDiscoveryService(iComfortWiFiBridgeHandler bridge) {
        super(iComfortWiFiBindingConstants.SUPPORTED_THING_TYPES_UIDS, TIMEOUT);

        this.bridge = bridge;
        this.bridgeUID = this.bridge.getThing().getUID();
        this.bridge.addAccountStatusListener(this);
    }

    @Override
    protected void startScan() {
        logger.debug("DISCOVERY: startScan() called");
        discoverDevices();
    }

    @Override
    protected void startBackgroundDiscovery() {
        logger.debug("DISCOVERY: startBackgroundDiscovery() called");
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
            logger.debug("DISCOVERY: Bridge status changed to ONLINE, starting scan.");
            discoverDevices();
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
        bridge.removeAccountStatusListener(this);
    }

    public void discoverDevices() {
        logger.debug("DISCOVERY: discoverDevices() called. Bridge status={}", bridge.getThing().getStatus());

        if (bridge.getThing().getStatus() != ThingStatus.ONLINE) {
            logger.debug("DISCOVERY: Bridge offline, aborting scan");
            return;
        }

        // Defensive: read nullable systems info into a local variable
        var systemsInfo = bridge.getiComfortWiFiSystemsInfo();

        List<SystemInfo> systems = systemsInfo.getSystems();
        if (systems.isEmpty()) {
            logger.debug("DISCOVERY: No systems returned by API, aborting scan");
            stopScan();
            return;
        }

        logger.debug("DISCOVERY: systems returned by API = {}", systems.size());

        for (SystemInfo systemInfo : systems) {
            String gatewaySN = Objects.requireNonNullElse(systemInfo.gatewaySN, "UNKNOWN");
            String systemName = Objects.requireNonNullElse(systemInfo.systemName, "UNKNOWN");
            logger.debug("DISCOVERY: Found system {} ({})", gatewaySN, systemName);

            addSystemDiscoveryResult(systemInfo);

            ZonesStatus zonesStatus = systemInfo.getZonesStatusOrNull();
            if (zonesStatus == null) {
                logger.debug("DISCOVERY: No zones object for system {}, skipping zone discovery", gatewaySN);
                continue;
            }

            List<ZoneStatus> zoneList = zonesStatus.zoneStatus;
            if (zoneList == null || zoneList.isEmpty()) {
                logger.debug("DISCOVERY: No zones returned for system {}, skipping zone discovery", gatewaySN);
                continue;
            }

            for (ZoneStatus zone : zoneList) {
                String zoneId = Objects.requireNonNullElse(zone.getZoneID(), "UNKNOWN");
                String zoneName = Objects.requireNonNullElse(zone.zoneName, "UNKNOWN");
                logger.debug("DISCOVERY: Found zone {} ({})", zoneId, zoneName);
                addZoneDiscoveryResult(systemName, zone);
            }
        }

        stopScan();
    }

    public void addSystemDiscoveryResult(SystemInfo systemInfo) {
        // Capture everything from the unannotated model once

        String id = Objects.requireNonNull(systemInfo.gatewaySN);

        String name = systemInfo.systemName;

        ThingUID thingUID = new ThingUID(iComfortWiFiBindingConstants.THING_TYPE_ICOMFORT_THERMOSTAT, bridgeUID, id);

        Map<String, Object> properties = new HashMap<>(2);
        properties.put(iComfortWiFiBindingConstants.PROPERTY_ID, id);
        properties.put(iComfortWiFiBindingConstants.PROPERTY_NAME, name);

        addDiscoveredThing(thingUID, properties, name);
    }

    public void addZoneDiscoveryResult(String systemName, ZoneStatus zone) {
        // Bridge the gap between the Lennox API model and openHAB's @NonNull requirements

        String zoneID = zone.getZoneID();
        String name = zone.zoneName + " (" + systemName + ")";

        ThingUID thingUID = new ThingUID(iComfortWiFiBindingConstants.THING_TYPE_ICOMFORT_ZONE, bridgeUID, zoneID);

        Map<String, Object> properties = new HashMap<>(2);
        properties.put(iComfortWiFiBindingConstants.PROPERTY_ID, zoneID);
        properties.put(iComfortWiFiBindingConstants.PROPERTY_NAME, name);

        addDiscoveredThing(thingUID, properties, name);
    }

    public void addDiscoveredThing(ThingUID thingUID, Map<String, Object> properties, String displayLabel) {
        logger.debug("DISCOVERY: Publishing thing {} with label {}", thingUID, displayLabel);

        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                .withBridge(bridgeUID).withLabel(displayLabel).build();

        thingDiscovered(discoveryResult);
    }
}
