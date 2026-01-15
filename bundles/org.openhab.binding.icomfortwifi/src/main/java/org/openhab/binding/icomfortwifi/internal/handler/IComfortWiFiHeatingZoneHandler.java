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

import java.util.Map;

import javax.measure.Unit;
import javax.measure.quantity.Temperature;

import org.eclipse.jdt.annotation.Checks;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.AwayStatus;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.FanMode;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.OperationMode;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.SystemStatus;
import org.openhab.binding.icomfortwifi.internal.dto.GatewayInfo;
import org.openhab.binding.icomfortwifi.internal.dto.SystemsInfo;
import org.openhab.binding.icomfortwifi.internal.dto.ZoneStatus;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for Lennox iComfort Wi-Fi Heating Zones.
 * * @author Konstantin Panchenko - Initial contribution
 * 
 * @author Jason Kota - Updated for openHAB 5.x compliance
 */
@NonNullByDefault
public class IComfortWiFiHeatingZoneHandler extends BaseIComfortWiFiHandler {

    private final Logger logger = Checks.requireNonNull(LoggerFactory.getLogger(IComfortWiFiHeatingZoneHandler.class));

    // Protected fields to support internal logic
    protected @Nullable SystemsInfo systemsInfo;
    protected int alertsCount = 20;

    private @Nullable ThingStatus tcsStatus;
    private @Nullable ZoneStatus zoneStatus;
    private @Nullable GatewayInfo gatewayInfo;

    public IComfortWiFiHeatingZoneHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    public void update(@Nullable ThingStatus tcsStatus, @Nullable ZoneStatus zoneStatus,
            @Nullable GatewayInfo gatewayInfo) {
        this.tcsStatus = tcsStatus;
        this.zoneStatus = zoneStatus;
        this.gatewayInfo = gatewayInfo;

        // If missing data, still initializing
        if (zoneStatus == null || gatewayInfo == null) {
            updateiComfortWiFiThingStatus(ThingStatus.INITIALIZING);
            return;
        }

        // Controller offline?
        ThingStatus safeStatus = (tcsStatus != null) ? tcsStatus : ThingStatus.UNKNOWN;
        if (ThingStatus.OFFLINE.equals(safeStatus)) {
            updateiComfortWiFiThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Controller offline");
            return;
        }

        updateiComfortWiFiThingStatus(ThingStatus.ONLINE);

        ZoneStatus localStatus = Checks.requireNonNull(zoneStatus);

        // Handle faults first
        if (handleActiveFaults(localStatus)) {
            return;
        }

        // Temperature units
        Unit<Temperature> prefUnit = Checks.requireNonNull(localStatus.getTemperatureUnit());

        // Promote numeric fields
        Number indoorTemp = Checks.requireNonNull(localStatus.indoorTemp);
        Number heatSetPoint = Checks.requireNonNull(localStatus.heatSetPoint);
        Number coolSetPoint = Checks.requireNonNull(localStatus.coolSetPoint);
        Number indoorHumidity = Checks.requireNonNull(localStatus.indoorHumidity);

        // Promote enums
        OperationMode opMode = Checks.requireNonNull(localStatus.operationMode);
        FanMode fanMode = Checks.requireNonNull(localStatus.fanMode);

        // System status
        SystemStatus sys = localStatus.systemStatus;
        String systemStatusStr = (sys != null) ? sys.toString() : "IDLE";

        // ---------------------------------------------------------------------
        // Numeric channels
        // ---------------------------------------------------------------------
        updateState("temperature", new QuantityType<>(indoorTemp.doubleValue(), prefUnit));
        updateState("heat-set-point", new QuantityType<>(heatSetPoint.doubleValue(), prefUnit));
        updateState("cool-set-point", new QuantityType<>(coolSetPoint.doubleValue(), prefUnit));
        updateState("humidity", new QuantityType<>(indoorHumidity.doubleValue(), Units.PERCENT));

        // ---------------------------------------------------------------------
        // String channels
        // ---------------------------------------------------------------------
        updateState("system-status", new StringType(systemStatusStr));
        updateState("operation-mode", new StringType(opMode.toString()));
        updateState("fan-mode", new StringType(fanMode.toString()));

        // ---------------------------------------------------------------------
        // Program Schedule (friendly name if available)
        // ---------------------------------------------------------------------
        Integer scheduleIndex = localStatus.getProgramScheduleSelection();
        String scheduleState = "unknown";

        if (scheduleIndex != null) {
            try {
                String raw = localStatus.getScheduleName();
                Map<Integer, String> map = java.util.Collections.emptyMap();

                if (raw != null && !raw.trim().isEmpty()) {
                    map = org.openhab.binding.icomfortwifi.internal.api.IComfortWiFiApiClient
                            .parseScheduleNameString(raw);
                }
                String name = map.get(scheduleIndex);
                scheduleState = (name != null && !name.isEmpty()) ? name : Integer.toString(scheduleIndex);
            } catch (Exception e) {
                scheduleState = Integer.toString(scheduleIndex);
            }
        }

        updateState("program-schedule", new StringType(scheduleState));

        // ---------------------------------------------------------------------
        // Away mode
        // ---------------------------------------------------------------------
        boolean isAway = (localStatus.awayMode != null && localStatus.awayMode == 1);
        updateState("away-mode", isAway ? OnOffType.ON : OnOffType.OFF);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, @Nullable Command command) {
        ZoneStatus currentStatus = this.zoneStatus;
        if (currentStatus == null || command == null) {
            return;
        }

        String cmdString = command.toString();

        if (command == RefreshType.REFRESH) {
            this.update(this.tcsStatus, currentStatus, this.gatewayInfo);
            return;
        }

        IComfortWiFiBridgeHandler bridge = this.getiComfortWiFiBridge();
        if (bridge == null) {
            return;
        }

        String channelId = channelUID.getId();

        // Away Mode
        if ("away-mode".equals(channelId) && command instanceof OnOffType onOff) {
            bridge.setZoneAwayMode(currentStatus, (onOff == OnOffType.ON ? 1 : 0));
            return;
        }

        // Temperature Setpoints
        QuantityType<Temperature> tempCommand = this.castToQuantityTypeTemperature(command);

        if (tempCommand != null && ("heat-set-point".equals(channelId) || "cool-set-point".equals(channelId))) {
            boolean isHeat = "heat-set-point".equals(channelId);

            // Preserve existing behavior: convert to the zone's reported unit (no forced conversion)
            Unit<Temperature> rawUnit = currentStatus.getTemperatureUnit();
            Unit<Temperature> unit = Checks.requireNonNull(rawUnit);

            QuantityType<Temperature> converted = Checks.requireNonNull(tempCommand.toUnit(unit));
            double value = converted.doubleValue();

            if (isHeat) {
                bridge.setZoneHeatingPoint(currentStatus, value);
            } else {
                bridge.setZoneCoolingPoint(currentStatus, value);
            }

            return;
        }
        // Other Modes
        try {
            switch (channelId) {
                case "away-mode" -> bridge.setZoneAwayMode(currentStatus, AwayStatus.valueOf(cmdString).getAwayValue());
                case "operation-mode" -> bridge.setZoneOperationMode(currentStatus,
                        OperationMode.valueOf(cmdString).getOperationModeValue());
                case "fan-mode" -> bridge.setZoneFanMode(currentStatus, FanMode.valueOf(cmdString).getFanModeValue());
                default -> {
                }
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid command '{}' for channel '{}'", command, channelId);
        }
    }

    @SuppressWarnings("unchecked")
    private @Nullable QuantityType<Temperature> castToQuantityTypeTemperature(@Nullable Command command) {
        if (command instanceof QuantityType<?> qt) {
            if (qt.getUnit().getDimension().equals(SIUnits.CELSIUS.getDimension())) {
                return (QuantityType<Temperature>) qt;
            }
        }
        return null;
    }

    public boolean handleActiveFaults(ZoneStatus zoneStatus) {
        if (zoneStatus.hasActiveFaults()) {
            this.updateiComfortWiFiThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    zoneStatus.getActiveFault());
            return true;
        }
        return false;
    }
}
