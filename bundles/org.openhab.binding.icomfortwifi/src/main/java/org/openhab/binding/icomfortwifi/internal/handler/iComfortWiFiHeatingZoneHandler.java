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

import javax.measure.Unit;
import javax.measure.quantity.Temperature;

import org.eclipse.jdt.annotation.Checks;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.AwayStatus;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.FanMode;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.OperationMode;
import org.openhab.binding.icomfortwifi.internal.dto.CustomTypes.UnifiedOperationMode;
import org.openhab.binding.icomfortwifi.internal.dto.GatewayInfo;
import org.openhab.binding.icomfortwifi.internal.dto.ZoneStatus;
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

@NonNullByDefault
public class iComfortWiFiHeatingZoneHandler extends BaseiComfortWiFiHandler {

    private final Logger logger = Checks.requireNonNull(LoggerFactory.getLogger(iComfortWiFiHeatingZoneHandler.class));

    private @Nullable ThingStatus tcsStatus;
    private @Nullable ZoneStatus zoneStatus;
    private @Nullable GatewayInfo gatewayInfo;

    public iComfortWiFiHeatingZoneHandler(Thing thing) {
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

        if (this.zoneStatus != null && this.gatewayInfo != null) {

            this.updateiComfortWiFiThingStatus(ThingStatus.ONLINE);

            if (tcsStatus != null && tcsStatus.equals(ThingStatus.OFFLINE)) {
                this.updateiComfortWiFiThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Controller offline");
                return;
            }

            ZoneStatus localStatus = Checks.requireNonNull(zoneStatus);

            if (!this.handleActiveFaults(localStatus)) {

                Unit<Temperature> prefUnit = localStatus.getTemperatureUnit();
                boolean isAway = "AWAY_ON".equals(localStatus.awayMode);

                // Update basic states
                this.updateState("temperature",
                        new QuantityType<>(Checks.requireNonNull(localStatus.indoorTemp), prefUnit));

                this.updateState("heat-set-point",
                        new QuantityType<>(Checks.requireNonNull(localStatus.heatSetPoint), prefUnit));

                this.updateState("cool-set-point",
                        new QuantityType<>(Checks.requireNonNull(localStatus.coolSetPoint), prefUnit));

                this.updateState("humidity",
                        new QuantityType<>(Checks.requireNonNull(localStatus.indoorHumidity), Units.PERCENT));

                this.updateState("system-status", new StringType(
                        localStatus.systemStatus != null ? localStatus.systemStatus.toString() : "IDLE"));

                this.updateState("operation-mode", new StringType(localStatus.operationMode.toString()));

                // Unified Operation Mode
                if (isAway) {
                    this.updateState("unified-operation-mode", new StringType("eco"));
                } else {
                    String modeString = switch (localStatus.operationMode) {
                        case HEAT_ONLY -> "heat";
                        case COOL_ONLY -> "cool";
                        case HEAT_OR_COOL -> "heatcool";
                        case OFF, UNKNOWN -> "off";
                    };
                    this.updateState("unified-operation-mode", new StringType(modeString));
                }

                this.updateState("away-mode",
                        new StringType(localStatus.awayMode != null ? localStatus.awayMode : "AWAY_OFF"));

                this.updateState("fan-mode", new StringType(localStatus.fanMode.toString()));
            }

        } else {
            this.updateiComfortWiFiThingStatus(ThingStatus.INITIALIZING);
        }
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

        iComfortWiFiBridgeHandler bridge = this.getiComfortWiFiBridge();
        if (bridge == null) {
            return;
        }

        String channelId = channelUID.getId();

        // Unified Operation Mode
        if ("unified-operation-mode".equals(channelId)) {
            try {
                UnifiedOperationMode mode = switch (cmdString) {
                    case "heatcool" -> UnifiedOperationMode.HEAT_COOL;
                    case "eco" -> UnifiedOperationMode.ECO;
                    case "cool" -> UnifiedOperationMode.COOL;
                    case "heat" -> UnifiedOperationMode.HEAT;
                    case "fan-only" -> UnifiedOperationMode.FAN_ONLY;
                    default -> UnifiedOperationMode.OFF;
                };

                this.handleUnifiedMode(bridge, currentStatus, mode, !"AWAY_ON".equals(currentStatus.awayMode));

            } catch (Exception e) {
                logger.warn("Error handling unified mode command: {}", command);
            }
            return;
        }

        // Temperature Setpoints
        QuantityType<Temperature> tempCommand = this.castToQuantityTypeTemperature(command);

        if (tempCommand != null && ("heat-set-point".equals(channelId) || "cool-set-point".equals(channelId))) {

            boolean isHeat = "heat-set-point".equals(channelId);

            // Preserve existing behavior: convert to the zone's reported unit (no forced conversion)
            double value = Checks.requireNonNull(tempCommand.toUnit(currentStatus.getTemperatureUnit())).doubleValue();

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
                    // No action for other channels
                }
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid command '{}' for channel '{}'", command, channelId);
        }
    }

    private void handleUnifiedMode(iComfortWiFiBridgeHandler bridge, ZoneStatus currentStatus,
            UnifiedOperationMode mode, boolean isAwayOff) {

        switch (mode) {
            case OFF:
                bridge.setZoneAwayMode(currentStatus, AwayStatus.AWAY_OFF.getAwayValue());
                bridge.setZoneOperationMode(currentStatus, OperationMode.OFF.getOperationModeValue());
                break;

            case HEAT:
                bridge.setZoneAwayMode(currentStatus, AwayStatus.AWAY_OFF.getAwayValue());
                bridge.setZoneOperationMode(currentStatus, OperationMode.HEAT_ONLY.getOperationModeValue());
                break;

            case COOL:
                bridge.setZoneAwayMode(currentStatus, AwayStatus.AWAY_OFF.getAwayValue());
                bridge.setZoneOperationMode(currentStatus, OperationMode.COOL_ONLY.getOperationModeValue());
                break;

            case HEAT_COOL:
                bridge.setZoneAwayMode(currentStatus, AwayStatus.AWAY_OFF.getAwayValue());
                bridge.setZoneOperationMode(currentStatus, OperationMode.HEAT_OR_COOL.getOperationModeValue());
                break;

            case FAN_ONLY:
                bridge.setZoneAwayMode(currentStatus, AwayStatus.AWAY_OFF.getAwayValue());
                bridge.setZoneOperationMode(currentStatus, OperationMode.OFF.getOperationModeValue());
                bridge.setZoneFanMode(currentStatus, FanMode.CIRCULATE.getFanModeValue());
                break;

            case ECO:
                if (isAwayOff) {
                    bridge.setZoneAwayMode(currentStatus, AwayStatus.AWAY_ON.getAwayValue());
                }
                break;

            default:
                logger.debug("Unified mode is UNKNOWN for zone {}", currentStatus.zoneNumber);
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
