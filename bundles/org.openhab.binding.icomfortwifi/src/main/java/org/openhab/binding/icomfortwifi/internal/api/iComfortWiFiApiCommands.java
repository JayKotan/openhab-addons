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
package org.openhab.binding.icomfortwifi.internal.api;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.icomfortwifi.internal.dto.ReqSetAwayMode;

/**
 * Canonical API command builder for the Lennox iComfort Wi‑Fi service.
 *
 * All endpoints are grouped alphabetically and follow a consistent structure.
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - Clean rewrite for openHAB 5.x
 */
@NonNullByDefault
public final class iComfortWiFiApiCommands {

    private iComfortWiFiApiCommands() {
        // Utility class
    }

    // ---------------------------------------------------------------------
    // Base URI
    // ---------------------------------------------------------------------

    public static final class iComfortServiceURI {
        public static final String BASE_PATH = "/DBAcessService.svc";

        private iComfortServiceURI() {
        }

        public String getURI() {
            return "https://services.myicomfort.com:443" + BASE_PATH;
        }

        public static final class URI {
            public static final String HOST = "services.myicomfort.com";
            public static final String PORT = "443";
            public static final String PROTOCOL = "https:";

            private URI() {
            }
        }
    }

    // ---------------------------------------------------------------------
    // Command Builders
    // ---------------------------------------------------------------------

    public static String getCommandGetBuildingsInfo(String username) {
        String base = new iComfortServiceURI().getURI();
        return Objects.requireNonNull(base + "/GetBuildingsInfo?userid=" + username);
    }

    public static String getCommandGetGatewayInfo(String gatewaySN, String tempUnit) {
        String base = new iComfortServiceURI().getURI();
        return Objects.requireNonNull(base + "/GetGatewayInfo?gatewaysn=" + gatewaySN + "&tempunit=" + tempUnit);
    }

    public static String getCommandGetGatewaysAlerts(String gatewaySN, String languageNbr, String count) {
        String base = new iComfortServiceURI().getURI();
        return Objects.requireNonNull(
                base + "/GetGatewaysAlerts?gatewaysn=" + gatewaySN + "&lang_nbr=" + languageNbr + "&count=" + count);
    }

    public static String getCommandGetOwnerProfileInfo(String username) {
        String base = new iComfortServiceURI().getURI();
        return Objects.requireNonNull(base + "/GetOwnerProfileInfo?userid=" + username);
    }

    public static String getCommandGetSystemsInfo(String username) {
        String base = new iComfortServiceURI().getURI();
        return Objects.requireNonNull(base + "/GetSystemsInfo?userid=" + username);
    }

    public static String getCommandGetTStatAlerts(String gatewaySN, String count) {
        String base = new iComfortServiceURI().getURI();
        return Objects.requireNonNull(base + "/GetTStatAlerts?gatewaysn=" + gatewaySN + "&count=" + count);
    }

    public static String getCommandGetTStatInfoList(String gatewaySN, String tempUnit) {
        String base = new iComfortServiceURI().getURI();
        return Objects.requireNonNull(base + "/GetTStatInfoList?gatewaysn=" + gatewaySN + "&tempunit=" + tempUnit);
    }

    public static String getCommandSetAwayModeNew(ReqSetAwayMode req) {
        String base = new iComfortServiceURI().getURI();
        return Objects.requireNonNull(base + "/SetAwayModeNew" + "?gatewaysn=" + req.gatewaySN + "&zonenumber="
                + req.zoneNumber + "&awaymode=" + req.awayMode + "&heatsetpoint=" + req.heatSetPoint + "&coolsetpoint="
                + req.coolSetPoint + "&fanmode=" + req.fanMode + "&tempscale=" + req.preferredTemperatureUnit);
    }

    public static String getCommandSetTStatInfo() {
        String base = new iComfortServiceURI().getURI();
        return Objects.requireNonNull(base + "/SetTStatInfo");
    }

    public static String getCommandValidateUser(String username, Integer langNbr) {
        String base = new iComfortServiceURI().getURI();
        return Objects.requireNonNull(base + "/ValidateUser?username=" + username + "&lang_nbr=" + langNbr);
    }

    // ---------------------------------------------------------------------
    // Endpoint Metadata (Optional, for documentation)
    // ---------------------------------------------------------------------

    public static final class getBuildingsInfo {
        public static final String PATH = "/GetBuildingsInfo";

        private getBuildingsInfo() {
        }

        public static final class paramsDef {
            public static final String USER_ID = "userid";

            private paramsDef() {
            }
        }
    }

    public static final class getGatewayInfo {
        public static final String PATH = "/GetGatewayInfo";

        private getGatewayInfo() {
        }

        public static final class paramsDef {
            public static final String GATEWAY_SN = "gatewaysn";
            public static final String TEMP_UNIT = "tempunit";

            private paramsDef() {
            }
        }
    }

    public static final class getGatewaysAlerts {
        public static final String PATH = "/GetGatewaysAlerts";

        private getGatewaysAlerts() {
        }

        public static final class paramsDef {
            public static final String GATEWAY_SN = "gatewaysn";
            public static final String LANGUAGE_NBR = "lang_nbr";
            public static final String COUNT = "count";

            private paramsDef() {
            }
        }
    }

    public static final class getOwnerProfileInfo {
        public static final String PATH = "/GetOwnerProfileInfo";

        private getOwnerProfileInfo() {
        }

        public static final class paramsDef {
            public static final String USER_ID = "userid";

            private paramsDef() {
            }
        }
    }

    public static final class getSystemsInfo {
        public static final String PATH = "/GetSystemsInfo";

        private getSystemsInfo() {
        }

        public static final class paramsDef {
            public static final String USER_ID = "userid";

            private paramsDef() {
            }
        }
    }

    public static final class getTStatAlerts {
        public static final String PATH = "/GetTStatAlerts";

        private getTStatAlerts() {
        }

        public static final class paramsDef {
            public static final String GATEWAY_SN = "gatewaysn";
            public static final String COUNT = "count";

            private paramsDef() {
            }
        }
    }

    public static final class getTStatInfoList {
        public static final String PATH = "/GetTStatInfoList";

        private getTStatInfoList() {
        }

        public static final class paramsDef {
            public static final String GATEWAY_SN = "gatewaysn";
            public static final String TEMP_UNIT = "tempunit";

            private paramsDef() {
            }
        }
    }

    public static final class setAwayModeNew {
        public static final String PATH = "/SetAwayModeNew";

        private setAwayModeNew() {
        }

        public static final class paramsDef {
            public static final String GATEWAY_SN = "gatewaysn";
            public static final String ZONE_NUMBER = "zonenumber";
            public static final String AWAY_MODE = "awaymode";
            public static final String HEAT_SET_POINT = "heatsetpoint";
            public static final String COOL_SET_POINT = "coolsetpoint";
            public static final String FAN_MODE = "fanmode";
            public static final String TEMP_SCALE = "tempscale";

            private paramsDef() {
            }
        }
    }

    public static final class setTStatInfo {
        public static final String PATH = "/SetTStatInfo";

        private setTStatInfo() {
        }
    }

    public static final class validateUser {
        public static final String PATH = "/ValidateUser";

        private validateUser() {
        }

        public static final class paramsDef {
            public static final String USER_NAME = "username";
            public static final String LANGUAGE_NBR = "lang_nbr";
            public static final String PASSWORD = "password";

            private paramsDef() {
            }
        }
    }
}
