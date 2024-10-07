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
package org.openhab.binding.icomfortwifi.internal.handler;

import static org.openhab.core.library.unit.Units.PERCENT;

import javax.measure.Unit;
import javax.measure.quantity.Temperature;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.AwayStatus;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.FanMode;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.OperationMode;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.TempUnits;
import org.openhab.binding.icomfortwifi.internal.api.models.response.CustomTypes.UnifiedOperationMode;
import org.openhab.binding.icomfortwifi.internal.api.models.response.ZoneStatus;
import org.openhab.binding.icomfortwifi.internal.iComfortWiFiBindingConstants;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.unit.ImperialUnits;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.uom.SI;

/**
 * The {@link iComfortWiFiHeatingZoneHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Updated Thingstatus to @Nullable. Improved the handling of temp units in Handlecommand
 */
public class iComfortWiFiHeatingZoneHandler extends BaseiComfortWiFiHandler {

    public final Logger logger = LoggerFactory.getLogger(iComfortWiFiHeatingZoneHandler.class);
    public @Nullable ThingStatus tcsStatus;
    public ZoneStatus zoneStatus;

    public iComfortWiFiHeatingZoneHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    public void update(ThingStatus tcsStatus, ZoneStatus zoneStatus) {
        this.tcsStatus = tcsStatus;
        this.zoneStatus = zoneStatus;
        // Make the zone offline when the related tcs is offline
        // If the related display is not a thing, ignore this
        if (tcsStatus != null && tcsStatus.equals(ThingStatus.OFFLINE)) {
            updateiComfortWiFiThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Display Controller offline");
        } else if (zoneStatus == null) {
            updateiComfortWiFiThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Status not found, check the zone id");
        } else if (handleActiveFaults(zoneStatus) == false) {
            updateiComfortWiFiThingStatus(ThingStatus.ONLINE);

            updateState(iComfortWiFiBindingConstants.ZONE_TEMPERATURE_CHANNEL, new QuantityType<>(zoneStatus.indoorTemp,
                    zoneStatus.preferredTemperatureUnit.getTemperatureUnit()));
            updateState(iComfortWiFiBindingConstants.ZONE_HUMIDITY_CHANNEL,
                    new QuantityType<>(zoneStatus.indoorHumidity, PERCENT));
            updateState(iComfortWiFiBindingConstants.ZONE_SYSTEM_STATUS_CHANNEL,
                    new StringType(zoneStatus.systemStatus.toString()));
            updateState(iComfortWiFiBindingConstants.ZONE_OPERATION_MODE_CHANNEL,
                    new StringType(zoneStatus.operationMode.toString()));
            if (zoneStatus.awayMode == AwayStatus.AWAY_ON) {
                updateState(iComfortWiFiBindingConstants.ZONE_UNIFIED_OPERATION_MODE_CHANNEL,
                        new StringType(UnifiedOperationMode.ECO.toString()));
            } else if (zoneStatus.operationMode == OperationMode.OFF) {
                updateState(iComfortWiFiBindingConstants.ZONE_UNIFIED_OPERATION_MODE_CHANNEL,
                        new StringType(UnifiedOperationMode.OFF.toString()));
            } else if (zoneStatus.operationMode == OperationMode.HEAT_ONLY) {
                updateState(iComfortWiFiBindingConstants.ZONE_UNIFIED_OPERATION_MODE_CHANNEL,
                        new StringType(UnifiedOperationMode.HEAT.toString()));
                updateState(iComfortWiFiBindingConstants.ZONE_SET_POINT_CHANNEL, new QuantityType<>(
                        zoneStatus.heatSetPoint, zoneStatus.preferredTemperatureUnit.getTemperatureUnit()));
            } else if (zoneStatus.operationMode == OperationMode.COOL_ONLY) {
                updateState(iComfortWiFiBindingConstants.ZONE_UNIFIED_OPERATION_MODE_CHANNEL,
                        new StringType(UnifiedOperationMode.COOL.toString()));
                updateState(iComfortWiFiBindingConstants.ZONE_SET_POINT_CHANNEL, new QuantityType<>(
                        zoneStatus.coolSetPoint, zoneStatus.preferredTemperatureUnit.getTemperatureUnit()));
            } else if (zoneStatus.operationMode == OperationMode.HEAT_OR_COOL) {
                updateState(iComfortWiFiBindingConstants.ZONE_UNIFIED_OPERATION_MODE_CHANNEL,
                        new StringType(UnifiedOperationMode.HEAT_COOL.toString()));
            }
            updateState(iComfortWiFiBindingConstants.ZONE_AWAY_MODE_CHANNEL,
                    new StringType(zoneStatus.awayMode.toString()));
            updateState(iComfortWiFiBindingConstants.ZONE_FAN_MODE_CHANNEL,
                    new StringType(zoneStatus.fanMode.toString()));
            updateState(iComfortWiFiBindingConstants.ZONE_HEAT_SET_POINT_CHANNEL, new QuantityType<>(
                    zoneStatus.heatSetPoint, zoneStatus.preferredTemperatureUnit.getTemperatureUnit()));
            updateState(iComfortWiFiBindingConstants.ZONE_COOL_SET_POINT_CHANNEL, new QuantityType<>(
                    zoneStatus.coolSetPoint, zoneStatus.preferredTemperatureUnit.getTemperatureUnit()));
        }
    }

    // @SuppressWarnings("unchecked")
    @Override
    public void handleCommand(@NonNull ChannelUID channelUID, @Nullable Command command) {
        if (command == RefreshType.REFRESH) {
            update(tcsStatus, zoneStatus);
        } else {
            iComfortWiFiBridgeHandler bridge = getiComfortWiFiBridge();
            if (bridge != null) {
                String channelId = channelUID.getId();
                QuantityType<Temperature> tempCommand = castToQuantityTypeTemperature(command);
                if (tempCommand != null) {
                    Unit<Temperature> tempUnit = tempCommand.getUnit();
                    if (tcsStatus != null && tcsStatus.equals(ThingStatus.OFFLINE)) {
                        bridge.setAlternateTemperatureUnit(TempUnits.getCustomTemperatureUnit(tempUnit));
                    }
                    if (iComfortWiFiBindingConstants.ZONE_UNIFIED_OPERATION_MODE_CHANNEL.equals(channelId)) {
                        logger.debug("Executing unified command");
                        String commandStr = command != null ? command.toString().toLowerCase() : ""; // Ensure command
                                                                                                     // is not null
                        UnifiedOperationMode mode;
                        try {
                            mode = UnifiedOperationMode.valueOf(commandStr.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            logger.warn("Unknown UnifiedOperationMode: {}", commandStr);
                            return;
                        }
                        switch (mode) {
                            case ECO:
                                if (zoneStatus.awayMode == AwayStatus.AWAY_OFF) {
                                    logger.debug("Setting Heating Zone to Away mode with command {}", commandStr);
                                    bridge.setZoneAwayMode(zoneStatus, AwayStatus.AWAY_ON.getAwayValue());
                                }
                                break;
                            case OFF:
                                logger.debug("Setting Heating Zone to Off mode with command {}", commandStr);
                                bridge.setZoneAwayMode(zoneStatus, AwayStatus.AWAY_OFF.getAwayValue());
                                bridge.setZoneFanMode(zoneStatus, FanMode.AUTO.getFanModeValue());
                                bridge.setZoneOperationMode(zoneStatus, OperationMode.OFF.getOperationModeValue());
                                break;
                            case COOL:
                                logger.debug("Setting Heating Zone to Cool mode with command {}", commandStr);
                                bridge.setZoneAwayMode(zoneStatus, AwayStatus.AWAY_OFF.getAwayValue());
                                bridge.setZoneFanMode(zoneStatus, FanMode.AUTO.getFanModeValue());
                                bridge.setZoneOperationMode(zoneStatus,
                                        OperationMode.COOL_ONLY.getOperationModeValue());
                                break;
                            case HEAT:
                                logger.debug("Setting Heating Zone to Heat mode with command {}", commandStr);
                                bridge.setZoneAwayMode(zoneStatus, AwayStatus.AWAY_OFF.getAwayValue());
                                bridge.setZoneFanMode(zoneStatus, FanMode.AUTO.getFanModeValue());
                                bridge.setZoneOperationMode(zoneStatus,
                                        OperationMode.HEAT_ONLY.getOperationModeValue());
                                break;
                            case HEAT_COOL:
                                logger.debug("Setting Heating Zone to HeatCool mode with command {}", commandStr);
                                bridge.setZoneAwayMode(zoneStatus, AwayStatus.AWAY_OFF.getAwayValue());
                                bridge.setZoneFanMode(zoneStatus, FanMode.AUTO.getFanModeValue());
                                bridge.setZoneOperationMode(zoneStatus,
                                        OperationMode.HEAT_OR_COOL.getOperationModeValue());
                                break;
                            case FAN_ONLY:
                                logger.debug("Setting Heating Zone to Fan Only mode with command {}", commandStr);
                                bridge.setZoneAwayMode(zoneStatus, AwayStatus.AWAY_OFF.getAwayValue());
                                bridge.setZoneOperationMode(zoneStatus, OperationMode.OFF.getOperationModeValue());
                                bridge.setZoneFanMode(zoneStatus, FanMode.CIRCULATE.getFanModeValue());
                                break;
                            default:
                                logger.warn("Unhandled UnifiedOperationMode: {}", mode);
                        }
                    } else if (iComfortWiFiBindingConstants.ZONE_AWAY_MODE_CHANNEL.equals(channelId)) {
                        // Check if command is valid before accessing its value
                        if (command != null) {
                            bridge.setZoneAwayMode(zoneStatus, AwayStatus.valueOf(command.toString()).getAwayValue());
                        }
                    } else if (zoneStatus.awayMode == AwayStatus.AWAY_OFF
                            && zoneStatus.operationMode != OperationMode.OFF) {
                        logger.debug("Zone is not in Away mode and command is not unified, executing the command");
                        if (iComfortWiFiBindingConstants.ZONE_COOL_SET_POINT_CHANNEL.equals(channelId)) {
                            bridge.setZoneCoolingPoint(zoneStatus, tempCommand.doubleValue());
                        } else if (iComfortWiFiBindingConstants.ZONE_HEAT_SET_POINT_CHANNEL.equals(channelId)) {
                            bridge.setZoneHeatingPoint(zoneStatus, tempCommand.doubleValue());
                        } else if (iComfortWiFiBindingConstants.ZONE_SET_POINT_CHANNEL.equals(channelId)) {
                            if (zoneStatus.operationMode == OperationMode.HEAT_ONLY) {
                                bridge.setZoneHeatingPoint(zoneStatus, tempCommand.doubleValue());
                            } else if (zoneStatus.operationMode == OperationMode.COOL_ONLY) {
                                bridge.setZoneCoolingPoint(zoneStatus, tempCommand.doubleValue());
                            } else {
                                logger.debug("Zone is not in Heat or Cool mode, cannot use Temperature Set Point");
                            }
                        } else if (iComfortWiFiBindingConstants.ZONE_OPERATION_MODE_CHANNEL.equals(channelId)) {
                            // Check if command is valid before accessing its value
                            if (command != null) {
                                bridge.setZoneOperationMode(zoneStatus,
                                        OperationMode.valueOf(command.toString()).getOperationModeValue());
                            }
                        } else if (iComfortWiFiBindingConstants.ZONE_FAN_MODE_CHANNEL.equals(channelId)) {
                            // Check if command is valid before accessing its value
                            if (command != null) {
                                bridge.setZoneFanMode(zoneStatus,
                                        FanMode.valueOf(command.toString()).getFanModeValue());
                            }
                        }
                    } else {
                        logger.debug("Zone is in Away mode and command is not unified, not executing the command");
                    }
                }
            }
        }
    }

    /**
     * Safely casts a Command to QuantityType<Temperature> if possible.
     *
     * @param command the Command to cast
     * @return the casted QuantityType<Temperature> or null if not possible
     */
    @Nullable
    private QuantityType<Temperature> castToQuantityTypeTemperature(@Nullable Command command) {
        if (command instanceof QuantityType<?>) {
            QuantityType<?> qt = (QuantityType<?>) command;
            Unit<?> unit = qt.getUnit();
            if (unit.isCompatible(SI.CELSIUS) || unit.isCompatible(ImperialUnits.FAHRENHEIT)) {
                @SuppressWarnings("unchecked")
                QuantityType<Temperature> tempCommand = (QuantityType<Temperature>) qt;
                return tempCommand;
            }
        }
        return null;
    }

    public boolean handleActiveFaults(ZoneStatus zoneStatus) {
        if (zoneStatus.hasActiveFaults()) {
            updateiComfortWiFiThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    zoneStatus.getActiveFault());
            return true;
        }
        return false;
    }
}
