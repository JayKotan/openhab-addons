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

import javax.measure.Unit;
import javax.measure.quantity.Temperature;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.library.unit.ImperialUnits;
import org.openhab.core.library.unit.SIUnits;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the zone status
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Updated Imports
 */
@NonNullByDefault
public class CustomTypes {

    public enum RequestStatus {
        @SerializedName("SUCCESS")
        SUCCESS,
        @SerializedName("FAILURE")
        FAILURE,
        UNKNOWN;
    }

    public enum TempUnits {
        @SerializedName("0")
        FAHRENHEIT("0"),
        @SerializedName("1")
        CELSIUS("1"),
        UNKNOWN("unknown");

        public String tempUnitsValue;

        // Revert to public constructor
        TempUnits(String tempUnitsValue) {
            this.tempUnitsValue = tempUnitsValue;
        }

        public String getTempUnitsValue() {
            return this.tempUnitsValue;
        }

        // @SuppressWarnings("null")
        public Unit<Temperature> getTemperatureUnit() {
            switch (this.tempUnitsValue) {
                case "0":
                    return ImperialUnits.FAHRENHEIT;
                case "1":
                    return SIUnits.CELSIUS;
                default:
                    throw new IllegalArgumentException("Invalid tempUnitsValue: " + this.tempUnitsValue);
            }
        }

        public static TempUnits getCustomTemperatureUnit(Unit<Temperature> tempUnit) {
            if (tempUnit == ImperialUnits.FAHRENHEIT) {
                return FAHRENHEIT;
            } else if (tempUnit == SIUnits.CELSIUS) {
                return CELSIUS;
            } else {
                return UNKNOWN;
            }
        }
    }

    public enum OperationMode {
        @SerializedName("2")
        COOL_ONLY(2),
        @SerializedName("1")
        HEAT_ONLY(1),
        @SerializedName("3")
        HEAT_OR_COOL(3),
        @SerializedName("0")
        OFF(0),
        UNKNOWN(-1);

        public Integer operationModeValue;

        // Revert to public constructor
        OperationMode(Integer operationModeValue) {
            this.operationModeValue = operationModeValue;
        }

        public Integer getOperationModeValue() {
            return this.operationModeValue;
        }
    }

    public enum UnifiedOperationMode {
        @SerializedName("0")
        OFF("off"),
        @SerializedName("1")
        HEAT("heat"),
        @SerializedName("2")
        COOL("cool"),
        @SerializedName("3")
        HEAT_COOL("heatcool"),
        @SerializedName("6")
        FAN_ONLY("fan-only"),
        @SerializedName("13")
        ECO("eco"),

        UNKNOWN("-1");

        public String unifiedOperationModeValue;

        // Revert to public constructor
        UnifiedOperationMode(String unifiedOperationModeValue) {
            this.unifiedOperationModeValue = unifiedOperationModeValue;
        }

        public String getUnifiedOperationModeValue() {
            return this.unifiedOperationModeValue;
        }

        @Override
        public String toString() {
            return this.unifiedOperationModeValue;
        }
    }

    public enum SystemStatus {
        @SerializedName("0")
        IDLE(0),
        @SerializedName("1")
        HEATING(1),
        @SerializedName("2")
        COOLING(2),
        @SerializedName("3")
        WAITING(3),
        @SerializedName("4")
        EMERGENCY_HEAT(4),
        UNKNOWN(-1);

        public Integer systemStatusValue;

        // Revert to public constructor
        SystemStatus(Integer systemStatusValue) {
            this.systemStatusValue = systemStatusValue;
        }

        public Integer getSystemStatusValue() {
            return this.systemStatusValue;
        }
    }

    public enum AwayStatus {
        @SerializedName("0")
        AWAY_OFF(0),
        @SerializedName("1")
        AWAY_ON(1),
        UNKNOWN(-1);

        public Integer awayValue;

        // Revert to public constructor
        AwayStatus(Integer awayValue) {
            this.awayValue = awayValue;
        }

        public Integer getAwayValue() {
            return this.awayValue;
        }
    }

    public enum FanMode {
        @SerializedName("0")
        AUTO(0),
        @SerializedName("1")
        ON(1),
        @SerializedName("2")
        CIRCULATE(2),
        UNKNOWN(-1);

        public Integer fanModeValue;

        // Revert to public constructor
        FanMode(Integer fanModeValue) {
            this.fanModeValue = fanModeValue;
        }

        public Integer getFanModeValue() {
            return this.fanModeValue;
        }
    }

    public enum ConnectionStatus {
        @SerializedName("GOOD")
        GOOD,
        @SerializedName("BAD")
        BAD,
        UNKNOWN;
    }

    public enum PreferedLanguage {
        @SerializedName("0")
        ENGLISH(0),
        @SerializedName("1")
        FRENCH(1),
        @SerializedName("2")
        SPANISH(2),
        UNKNOWN(-1);

        public Integer preferedLanguage;

        // Revert to public constructor
        PreferedLanguage(Integer preferedLanguage) {
            this.preferedLanguage = preferedLanguage;
        }

        public Integer getPreferedLanguageValue() {
            return this.preferedLanguage;
        }
    }

    public enum AlertStatus {
        @SerializedName("0")
        ALERT_CLEARED(0),
        @SerializedName("1")
        ALERT_RAISED(1),
        UNKNOWN(-1);

        public Integer alertValue;

        // Revert to public constructor
        AlertStatus(Integer alertValue) {
            this.alertValue = alertValue;
        }

        public Integer getAlertValue() {
            return this.alertValue;
        }
    }

    // Schedule Mode - Under Development
    public enum ProgramScheduleSelection {
        @SerializedName("0")
        SUMMER(0),
        @SerializedName("2")
        SPRING_FALL(2),
        @SerializedName("1")
        WINTER(1),
        @SerializedName("3")
        SAVE_ENERGY(3),
        @SerializedName("4")
        CUSTOM(4),
        UNKNOWN(-1);

        public Integer programScheduleSelectionValue;

        // Revert to public constructor
        ProgramScheduleSelection(Integer programScheduleSelectionValue) {
            this.programScheduleSelectionValue = programScheduleSelectionValue;
        }

        public Integer getProgramScheduleSelectionValue() {
            return this.programScheduleSelectionValue;
        }
    }

    public enum ProgramScheduleMode {
        @SerializedName("0")
        MANUAL("0"),
        @SerializedName("1")
        SCHEDULE("1"),
        UNKNOWN("-1");

        public String programScheduleModeValue;

        // Revert to public constructor
        ProgramScheduleMode(String programScheduleModeValue) {
            this.programScheduleModeValue = programScheduleModeValue;
        }

        public String getProgramScheduleModeValue() {
            return this.programScheduleModeValue;
        }
    }
}
