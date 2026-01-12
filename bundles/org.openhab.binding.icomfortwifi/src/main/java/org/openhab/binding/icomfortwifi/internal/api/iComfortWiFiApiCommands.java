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
// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package org.openhab.binding.icomfortwifi.internal.api;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.icomfortwifi.internal.dto.ReqSetAwayMode;

/**
 * *
 * 
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Added @nullByDefault- updated Import section
 *
 */
@SuppressWarnings("unused")
@NonNullByDefault
public final class iComfortWiFiApiCommands {
    public iComfortWiFiApiCommands() {
    }

    public static String getCommandValidateUser(String username, Integer lngNumber) {
        String uri = new iComfortServiceURI().getURI();
        String url = uri + "/ValidateUser" + "?username=" + username + "&lang_nbr=" + lngNumber;

        return Objects.requireNonNull(url);
    }

    public static String getCommandGetOwnerProfileInfo(String username) {
        String base = new iComfortServiceURI().getURI();
        String url = base + "/GetOwnerProfileInfo" + "?userid=" + username;
        return Objects.requireNonNull(url);
    }

    public static String getCommandGetBuildingsInfo(String username) {
        String base = new iComfortServiceURI().getURI();
        String url = base + "/GetBuildingsInfo" + "?userid=" + username;
        return Objects.requireNonNull(url);
    }

    public static String getCommandGetSystemsInfo(String username) {
        String base = new iComfortServiceURI().getURI();
        String url = base + "/GetSystemsInfo" + "?userid=" + username;
        return Objects.requireNonNull(url);
    }

    public static String getCommandGetGatewayInfo(String gatewaySN, String tempUnit) {
        String base = new iComfortServiceURI().getURI();
        String url = base + "/GetGatewayInfo" + "?gatewaysn=" + gatewaySN + "&tempunit=" + tempUnit;
        return Objects.requireNonNull(url);
    }

    public static String getCommandGetTStatInfoList(String gatewaySN, String tempUnit) {
        String base = new iComfortServiceURI().getURI();
        String url = base + "/GetTStatInfoList" + "?gatewaysn=" + gatewaySN + "&tempunit=" + tempUnit;
        return Objects.requireNonNull(url);
    }

    public static String getCommandSetAwayModeNew(ReqSetAwayMode reqSetAway) {
        String base = new iComfortServiceURI().getURI();
        String url = base + "/SetAwayModeNew" + "?gatewaysn=" + reqSetAway.gatewaySN + "&zonenumber="
                + reqSetAway.zoneNumber + "&awaymode=" + reqSetAway.awayMode + "&heatsetpoint="
                + reqSetAway.heatSetPoint + "&coolsetpoint=" + reqSetAway.coolSetPoint + "&fanmode="
                + reqSetAway.fanMode + "&tempscale=" + reqSetAway.preferredTemperatureUnit;
        return Objects.requireNonNull(url);
    }

    public static String getCommandSetTStatInfo() {
        String base = new iComfortServiceURI().getURI();
        String url = base + "/SetTStatInfo";
        return Objects.requireNonNull(url);
    }

    public static String getCommandGetGatewaysAlerts(String gatewaySN, String languageNbr, String count) {
        String base = new iComfortServiceURI().getURI();
        String url = base + "/GetGatewaysAlerts" + "?gatewaysn=" + gatewaySN + "&lang_nbr=" + languageNbr + "&count="
                + count;
        return Objects.requireNonNull(url);
    }

    public static final class getBuildingsInfo {
        public static final String PATH = "/GetBuildingsInfo";

        private getBuildingsInfo() {
        }

        public static final class paramsDef {
            public static final String UserId = "userid";

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
            public static final String UserId = "userid";

            private paramsDef() {
            }
        }
    }

    public static final class getSystemsInfo {
        public static final String PATH = "/GetSystemsInfo";

        private getSystemsInfo() {
        }

        public static final class paramsDef {
            public static final String UserId = "userid";

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
            public static final String CENTRAL_ZONED_AWAY = "Central_Zoned_Away";
            public static final String CANCEL_AWAY = "Cancel_Away";
            public static final String ZONE_NUMBER = "Zone_Number";

            private paramsDef() {
            }
        }
    }

    public static final class iComfortServiceURI {
        public static final String BASE_PATH = "/DBAcessService.svc";

        public iComfortServiceURI() {
        }

        private String getURI() {
            return "https://services.myicomfort.com:443/DBAcessService.svc";
        }

        public static final class URI {
            public static final String HOST = "services.myicomfort.com";
            public static final String PORT = "443";
            public static final String PROTOCOL = "https:";

            private URI() {
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

        public static final class paramsDef {
            private paramsDef() {
            }
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
