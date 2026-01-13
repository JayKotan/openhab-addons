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
package org.openhab.binding.icomfortwifi.internal.dto;

import javax.measure.Unit;
import javax.measure.quantity.Temperature;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.library.unit.ImperialUnits;
import org.openhab.core.library.unit.SIUnits;

import com.google.gson.annotations.SerializedName;

/**
 * Collection of custom enumerations used by the iComfort Wi‑Fi API.
 *
 * Each enum maps directly to the API’s numeric or string‑encoded values.
 */
@SuppressWarnings("unused")
public final class CustomTypes {

    // ---------------------------------------------------------------------
    // AlertStatus
    // ---------------------------------------------------------------------

    public enum AlertStatus {
        @SerializedName("0")
        CLEARED(0),

        @SerializedName("1")
        ACTIVE(1),

        UNKNOWN(-1);

        private final int alertValue;

        AlertStatus(int alertValue) {
            this.alertValue = alertValue;
        }

        public int getAlertValue() {
            return alertValue;
        }

        public static AlertStatus fromValue(@Nullable Integer v) {
            if (v == null) {
                return UNKNOWN;
            }
            for (AlertStatus s : values()) {
                if (s.alertValue == v) {
                    return s;
                }
            }
            return UNKNOWN;
        }
    }

    // ---------------------------------------------------------------------
    // AwayStatus
    // ---------------------------------------------------------------------

    public enum AwayStatus {
        @SerializedName("0")
        AWAY_OFF(0),

        @SerializedName("1")
        AWAY_ON(1),

        UNKNOWN(-1);

        private final int awayValue;

        AwayStatus(int awayValue) {
            this.awayValue = awayValue;
        }

        public int getAwayValue() {
            return awayValue;
        }

        public static AwayStatus fromValue(@Nullable Integer v) {
            if (v == null) {
                return UNKNOWN;
            }
            for (AwayStatus s : values()) {
                if (s.awayValue == v) {
                    return s;
                }
            }
            return UNKNOWN;
        }
    }

    // ---------------------------------------------------------------------
    // ConnectionStatus
    // ---------------------------------------------------------------------

    public enum ConnectionStatus {
        @SerializedName("GOOD")
        GOOD,

        @SerializedName("BAD")
        BAD,

        UNKNOWN
    }

    // ---------------------------------------------------------------------
    // FanMode
    // ---------------------------------------------------------------------

    public enum FanMode {
        @SerializedName("0")
        AUTO(0),

        @SerializedName("1")
        ON(1),

        @SerializedName("2")
        CIRCULATE(2),

        UNKNOWN(-1);

        private final int fanModeValue;

        FanMode(int fanModeValue) {
            this.fanModeValue = fanModeValue;
        }

        public int getFanModeValue() {
            return fanModeValue;
        }

        public static FanMode fromValue(@Nullable Integer v) {
            if (v == null) {
                return UNKNOWN;
            }
            for (FanMode m : values()) {
                if (m.fanModeValue == v) {
                    return m;
                }
            }
            return UNKNOWN;
        }
    }

    // ---------------------------------------------------------------------
    // OperationMode
    // ---------------------------------------------------------------------

    public enum OperationMode {
        @SerializedName("0")
        OFF(0),

        @SerializedName("1")
        HEAT_ONLY(1),

        @SerializedName("2")
        COOL_ONLY(2),

        @SerializedName("3")
        HEAT_OR_COOL(3),

        UNKNOWN(-1);

        private final int operationModeValue;

        OperationMode(int operationModeValue) {
            this.operationModeValue = operationModeValue;
        }

        public int getOperationModeValue() {
            return operationModeValue;
        }

        public static OperationMode fromValue(@Nullable Integer v) {
            if (v == null) {
                return UNKNOWN;
            }
            for (OperationMode m : values()) {
                if (m.operationModeValue == v) {
                    return m;
                }
            }
            return UNKNOWN;
        }
    }

    // ---------------------------------------------------------------------
    // PreferredLanguage
    // ---------------------------------------------------------------------

    public enum PreferredLanguage {
        @SerializedName("0")
        ENGLISH(0),

        @SerializedName("1")
        FRENCH(1),

        @SerializedName("2")
        SPANISH(2),

        UNKNOWN(-1);

        private final int preferredLanguage;

        PreferredLanguage(int preferredLanguage) {
            this.preferredLanguage = preferredLanguage;
        }

        public int getPreferredLanguageValue() {
            return preferredLanguage;
        }

        public static PreferredLanguage fromValue(@Nullable Integer v) {
            if (v == null) {
                return UNKNOWN;
            }
            for (PreferredLanguage p : values()) {
                if (p.preferredLanguage == v) {
                    return p;
                }
            }
            return UNKNOWN;
        }
    }

    // ---------------------------------------------------------------------
    // RequestStatus
    // ---------------------------------------------------------------------

    public enum RequestStatus {
        @SerializedName("SUCCESS")
        SUCCESS,

        @SerializedName("FAILURE")
        FAILURE,

        UNKNOWN
    }

    // ---------------------------------------------------------------------
    // SystemStatus
    // ---------------------------------------------------------------------

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

        private final int systemStatusValue;

        SystemStatus(int systemStatusValue) {
            this.systemStatusValue = systemStatusValue;
        }

        public int getSystemStatusValue() {
            return systemStatusValue;
        }

        public static SystemStatus fromValue(@Nullable Integer v) {
            if (v == null) {
                return UNKNOWN;
            }
            for (SystemStatus s : values()) {
                if (s.systemStatusValue == v) {
                    return s;
                }
            }
            return UNKNOWN;
        }
    }

    // ---------------------------------------------------------------------
    // TempUnits
    // ---------------------------------------------------------------------

    public enum TempUnits {
        @SerializedName("0")
        FAHRENHEIT("0"),

        @SerializedName("1")
        CELSIUS("1"),

        UNKNOWN("unknown");

        private final String tempUnitsValue;

        TempUnits(String tempUnitsValue) {
            this.tempUnitsValue = tempUnitsValue;
        }

        public String getTempUnitsValue() {
            return tempUnitsValue;
        }

        public @NonNull Unit<@NonNull Temperature> getTemperatureUnit() {
            return "0".equals(tempUnitsValue) ? ImperialUnits.FAHRENHEIT : SIUnits.CELSIUS;
        }

        public static @Nullable TempUnits getCustomTemperatureUnit(@Nullable Unit<Temperature> tempUnit) {
            if (tempUnit == ImperialUnits.FAHRENHEIT) {
                return FAHRENHEIT;
            } else if (tempUnit == SIUnits.CELSIUS) {
                return CELSIUS;
            }
            return null;
        }
    }

    // ---------------------------------------------------------------------
    // UnifiedOperationMode
    // ---------------------------------------------------------------------

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

        private final String unifiedOperationModeValue;

        UnifiedOperationMode(String unifiedOperationModeValue) {
            this.unifiedOperationModeValue = unifiedOperationModeValue;
        }

        public String getUnifiedOperationModeValue() {
            return unifiedOperationModeValue;
        }

        @Override
        public String toString() {
            return unifiedOperationModeValue;
        }
    }
}
