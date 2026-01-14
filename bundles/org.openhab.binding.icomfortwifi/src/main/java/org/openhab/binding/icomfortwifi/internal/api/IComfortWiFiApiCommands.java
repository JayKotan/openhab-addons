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
 * Canonical API command builder for the Lennox iComfort WiFi service.
 *
 * All endpoints are grouped alphabetically and follow a consistent structure.
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - Clean rewrite for openHAB 5.2
 */
@NonNullByDefault
public final class IComfortWiFiApiCommands {

    private IComfortWiFiApiCommands() {
        // Utility class
    }

    // ---------------------------------------------------------------------
    // Base URI
    // ---------------------------------------------------------------------

    public static final class IComfortServiceUri {
        public static final String BASE_PATH = "/DBAcessService.svc";

        private IComfortServiceUri() {
        }

        public String getUri() {
            return "https://services.myicomfort.com:443" + BASE_PATH;
        }

        public static final class Uri {
            public static final String HOST = "services.myicomfort.com";
            public static final String PORT = "443";
            public static final String PROTOCOL = "https:";

            private Uri() {
            }
        }
    }

    // ---------------------------------------------------------------------
    // Command Builders
    // ---------------------------------------------------------------------

    public static String getCommandGetBuildingsInfo(String username) {
        String base = new IComfortServiceUri().getUri();
        return Objects.requireNonNull(base + "/GetBuildingsInfo?userid=" + username);
    }

    public static String getCommandGetGatewayInfo(String gatewaySN, String tempUnit) {
        String base = new IComfortServiceUri().getUri();
        return Objects.requireNonNull(base + "/GetGatewayInfo?gatewaysn=" + gatewaySN + "&tempunit=" + tempUnit);
    }

    public static String getCommandGetGatewaysAlerts(String gatewaySN, String languageNbr, String count) {
        String base = new IComfortServiceUri().getUri();
        return Objects.requireNonNull(
                base + "/GetGatewaysAlerts?gatewaysn=" + gatewaySN + "&lang_nbr=" + languageNbr + "&count=" + count);
    }

    public static String getCommandGetOwnerProfileInfo(String username) {
        String base = new IComfortServiceUri().getUri();
        return Objects.requireNonNull(base + "/GetOwnerProfileInfo?userid=" + username);
    }

    public static String getCommandGetSystemsInfo(String username) {
        String base = new IComfortServiceUri().getUri();
        return Objects.requireNonNull(base + "/GetSystemsInfo?userid=" + username);
    }

    public static String getCommandGetTStatAlerts(String gatewaySN, String count) {
        String base = new IComfortServiceUri().getUri();
        return Objects.requireNonNull(base + "/GetTStatAlerts?gatewaysn=" + gatewaySN + "&count=" + count);
    }

    public static String getCommandGetTStatInfoList(String gatewaySN, String tempUnit) {
        String base = new IComfortServiceUri().getUri();
        return Objects.requireNonNull(base + "/GetTStatInfoList?gatewaysn=" + gatewaySN + "&tempunit=" + tempUnit);
    }

    public static String getCommandSetAwayModeNew(ReqSetAwayMode req) {
        String base = new IComfortServiceUri().getUri();
        return Objects.requireNonNull(base + "/SetAwayModeNew" + "?gatewaysn=" + req.gatewaySN + "&zonenumber="
                + req.zoneNumber + "&awaymode=" + req.awayMode + "&heatsetpoint=" + req.heatSetPoint + "&coolsetpoint="
                + req.coolSetPoint + "&fanmode=" + req.fanMode + "&tempscale=" + req.preferredTemperatureUnit);
    }

    public static String getCommandSetTStatInfo() {
        String base = new IComfortServiceUri().getUri();
        return Objects.requireNonNull(base + "/SetTStatInfo");
    }

    public static String getCommandValidateUser(String username, Integer langNbr) {
        String base = new IComfortServiceUri().getUri();
        return Objects.requireNonNull(base + "/ValidateUser?username=" + username + "&lang_nbr=" + langNbr);
    }

    // ---------------------------------------------------------------------
    // Endpoint Metadata (Optional, for documentation)
    // ---------------------------------------------------------------------

    public static final class GetBuildingsInfo {
        public static final String PATH = "/GetBuildingsInfo";

        private GetBuildingsInfo() {
        }

        public static final class ParamsDef {
            public static final String USER_ID = "userid";

            private ParamsDef() {
            }
        }
    }

    public static final class GetGatewayInfo {
        public static final String PATH = "/GetGatewayInfo";

        private GetGatewayInfo() {
        }

        public static final class ParamsDef {
            public static final String GATEWAY_SN = "gatewaysn";
            public static final String TEMP_UNIT = "tempunit";

            private ParamsDef() {
            }
        }
    }

    public static final class GetGatewaysAlerts {
        public static final String PATH = "/GetGatewaysAlerts";

        private GetGatewaysAlerts() {
        }

        public static final class ParamsDef {
            public static final String GATEWAY_SN = "gatewaysn";
            public static final String LANGUAGE_NBR = "lang_nbr";
            public static final String COUNT = "count";

            private ParamsDef() {
            }
        }
    }

    public static final class GetOwnerProfileInfo {
        public static final String PATH = "/GetOwnerProfileInfo";

        private GetOwnerProfileInfo() {
        }

        public static final class ParamsDef {
            public static final String USER_ID = "userid";

            private ParamsDef() {
            }
        }
    }

    public static final class GetSystemsInfo {
        public static final String PATH = "/GetSystemsInfo";

        private GetSystemsInfo() {
        }

        public static final class ParamsDef {
            public static final String USER_ID = "userid";

            private ParamsDef() {
            }
        }
    }

    public static final class GetTStatAlerts {
        public static final String PATH = "/GetTStatAlerts";

        private GetTStatAlerts() {
        }

        public static final class ParamsDef {
            public static final String GATEWAY_SN = "gatewaysn";
            public static final String COUNT = "count";

            private ParamsDef() {
            }
        }
    }

    public static final class GetTStatInfoList {
        public static final String PATH = "/GetTStatInfoList";

        private GetTStatInfoList() {
        }

        public static final class ParamsDef {
            public static final String GATEWAY_SN = "gatewaysn";
            public static final String TEMP_UNIT = "tempunit";

            private ParamsDef() {
            }
        }
    }

    public static final class SetAwayModeNew {
        public static final String PATH = "/SetAwayModeNew";

        private SetAwayModeNew() {
        }

        public static final class ParamsDef {
            public static final String GATEWAY_SN = "gatewaysn";
            public static final String ZONE_NUMBER = "zonenumber";
            public static final String AWAY_MODE = "awaymode";
            public static final String HEAT_SET_POINT = "heatsetpoint";
            public static final String COOL_SET_POINT = "coolsetpoint";
            public static final String FAN_MODE = "fanmode";
            public static final String TEMP_SCALE = "tempscale";

            private ParamsDef() {
            }
        }
    }

    public static final class SetTStatInfo {
        public static final String PATH = "/SetTStatInfo";

        private SetTStatInfo() {
        }
    }

    public static final class ValidateUser {
        public static final String PATH = "/ValidateUser";

        private ValidateUser() {
        }

        public static final class ParamsDef {
            public static final String USER_NAME = "username";
            public static final String LANGUAGE_NBR = "lang_nbr";
            public static final String PASSWORD = "password";

            private ParamsDef() {
            }
        }
    }
}
