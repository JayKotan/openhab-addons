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
package org.openhab.binding.icomfortwifi.internal.api;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.icomfortwifi.internal.api.models.request.ReqSetAwayMode;

/**
 * List of iComfortWiFi API commands
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - made this @nullByFault
 *
 */
@NonNullByDefault
public final class iComfortWiFiApiCommands {

    public final static class iComfortServiceURI {
        public final class URI {
            public static final String HOST = "services.myicomfort.com";
            public static final String PORT = "443";
            public static final String PROTOCOL = "https:";

            public URI() {
            }
        }

        // public static final String BASE_PATH = "/DBAcessService.svc";
        public static final String BASE_PATH = "/DBAcessService.svc";

        public iComfortServiceURI() {
        }

        protected String getURI() {
            // @formatter:off
            StringBuilder urlBuilder = new StringBuilder(URI.PROTOCOL)
                    .append("//")
                    .append(URI.HOST)
                    .append(":")
                    .append(URI.PORT)
                    .append(BASE_PATH);
            // @formatter:on
            return urlBuilder.toString();
        }
    }

    public final static class validateUser {
        public static final String PATH = "/ValidateUser";

        public final class paramsDef {
            public static final String USER_NAME = "UserName";
            public static final String LANGUAGE_NBR = "lang_nbr";
            public static final String PASSWORD = "password";

            public paramsDef() {
            }
        }

        public validateUser() {
        }
    }

    public final static class getOwnerProfileInfo {
        public static final String PATH = "/GetOwnerProfileInfo";

        public final class paramsDef {
            public static final String UserId = "userid";

            public paramsDef() {
            }
        }

        public getOwnerProfileInfo() {
        }
    }

    public final static class getBuildingsInfo {
        public static final String PATH = "/GetBuildingsInfo";

        public final class paramsDef {
            public static final String UserId = "userid";

            public paramsDef() {
            }
        }

        public getBuildingsInfo() {
        }
    }

    public final static class getSystemsInfo {
        public static final String PATH = "/GetSystemsInfo";

        public final class paramsDef {
            public static final String UserId = "userid";

            public paramsDef() {
            }
        }

        public getSystemsInfo() {
        }
    }

    public final static class getGatewayInfo {
        public static final String PATH = "/GetGatewayInfo";

        public final class paramsDef {
            public static final String GATEWAY_SN = "gatewaysn";
            public static final String TEMP_UNIT = "tempunit";

            public paramsDef() {
            }
        }

        public getGatewayInfo() {
        }
    }

    public final static class getTStatInfoList {
        public static final String PATH = "/GetTStatInfoList";

        // @SuppressWarnings("unused")
        public final class paramsDef {
            public static final String GATEWAY_SN = "gatewaysn";
            public static final String TEMP_UNIT = "tempunit";
            public static final String CENTRAL_ZONED_AWAY = "Central_Zoned_Away";
            public static final String CANCEL_AWAY = "Cancel_Away";
            public static final String ZONE_NUMBER = "Zone_Number";

            public paramsDef() {
            }
        }

        public getTStatInfoList() {
        }
    }

    public final static class getGatewaysAlerts {
        public static final String PATH = "/GetGatewaysAlerts";

        public final class paramsDef {
            public static final String GATEWAY_SN = "gatewaysn";
            public static final String LANGUAGE_NBR = "lang_nbr";
            public static final String COUNT = "count";

            public paramsDef() {
            }
        }

        public getGatewaysAlerts() {
        }
    }

    public final static class setAwayModeNew {
        public static final String PATH = "/SetAwayModeNew";

        // @SuppressWarnings("unused")
        public final class paramsDef {
            public static final String GATEWAY_SN = "gatewaysn";
            public static final String ZONE_NUMBER = "zonenumber";
            public static final String AWAY_MODE = "awaymode";
            public static final String HEAT_SET_POINT = "heatsetpoint";
            public static final String COOL_SET_POINT = "coolsetpoint";
            public static final String FAN_MODE = "fanmode";
            public static final String TEMP_SCALE = "tempscale";

            public paramsDef() {
            }
        }

        public setAwayModeNew() {
        }
    }

    public final static class setTStatInfo {
        public static final String PATH = "/SetTStatInfo";

        // @SuppressWarnings("unused")
        public final class paramsDef {

            public paramsDef() {
            }
        }

        public setTStatInfo() {
        }
    }

    public static String getCommandValidateUser(String userName, Integer lngNumber) {
        // @formatter:off
        StringBuilder urlBuilder = new StringBuilder((new iComfortServiceURI()).getURI())
                .append(validateUser.PATH)
                .append("?")
                .append(validateUser.paramsDef.USER_NAME)
                .append("=")
                .append(userName)
                .append("&")
                .append(validateUser.paramsDef.LANGUAGE_NBR)
                .append("=")
                .append(lngNumber.toString());
        // @formatter:on
        return urlBuilder.toString();
    }

    public static String getCommandGetOwnerProfileInfo(String userName) {
        // @formatter:off
        StringBuilder urlBuilder = new StringBuilder((new iComfortServiceURI()).getURI())
                .append(getOwnerProfileInfo.PATH)
                .append("?")
                .append(getOwnerProfileInfo.paramsDef.UserId)
                .append("=")
                .append(userName);
        // @formatter:on
        return urlBuilder.toString();
    }

    public static String getCommandGetBuildingsInfo(String userName) {
        // @formatter:off
        StringBuilder urlBuilder = new StringBuilder((new iComfortServiceURI()).getURI())
                .append(getBuildingsInfo.PATH)
                .append("?")
                .append(getBuildingsInfo.paramsDef.UserId)
                .append("=")
                .append(userName);
        // @formatter:on
        return urlBuilder.toString();
    }

    public static String getCommandGetSystemsInfo(String userName) {
        // @formatter:off
        StringBuilder urlBuilder = new StringBuilder((new iComfortServiceURI()).getURI())
                .append(getSystemsInfo.PATH)
                .append("?")
                .append(getSystemsInfo.paramsDef.UserId)
                .append("=")
                .append(userName);
        // @formatter:on
        return urlBuilder.toString();
    }

    public static String getCommandGetGatewayInfo(String gatewaySN, String tempUnit) {
        // @formatter:off
        StringBuilder urlBuilder = new StringBuilder((new iComfortServiceURI()).getURI())
                .append(getGatewayInfo.PATH)
                .append("?")
                .append(getGatewayInfo.paramsDef.GATEWAY_SN)
                .append("=")
                .append(gatewaySN)
                .append("&")
                .append(getGatewayInfo.paramsDef.TEMP_UNIT)
                .append("=")
                .append(tempUnit);
        // @formatter:on
        return urlBuilder.toString();
    }

    public static String getCommandGetTStatInfoList(String gatewaySN, String tempUnit) {
        // @formatter:off
        StringBuilder urlBuilder = new StringBuilder((new iComfortServiceURI()).getURI())
                .append(getTStatInfoList.PATH)
                .append("?")
                .append(getTStatInfoList.paramsDef.GATEWAY_SN)
                .append("=")
                .append(gatewaySN)
                .append("&")
                .append(getTStatInfoList.paramsDef.TEMP_UNIT)
                .append("=")
                .append(tempUnit);
        // @formatter:on
        return urlBuilder.toString();
    }

    public static String getCommandSetAwayModeNew(ReqSetAwayMode reqSetAway) {
        // @formatter:off
        StringBuilder urlBuilder = new StringBuilder((new iComfortServiceURI()).getURI())
                .append(setAwayModeNew.PATH)
                .append("?")
                .append(setAwayModeNew.paramsDef.GATEWAY_SN)
                .append("=")
                .append(reqSetAway.gatewaySN.toString())
                .append("&")
                .append(setAwayModeNew.paramsDef.ZONE_NUMBER)
                .append("=")
                .append(reqSetAway.zoneNumber.toString())
                .append("&")
                .append(setAwayModeNew.paramsDef.AWAY_MODE)
                .append("=")
                .append(reqSetAway.awayMode.toString())
                .append("&")
                .append(setAwayModeNew.paramsDef.HEAT_SET_POINT)
                .append("=")
                .append(reqSetAway.heatSetPoint.toString())
                .append("&")
                .append(setAwayModeNew.paramsDef.COOL_SET_POINT)
                .append("=")
                .append(reqSetAway.coolSetPoint.toString())
                .append("&")
                .append(setAwayModeNew.paramsDef.FAN_MODE)
                .append("=")
                .append(reqSetAway.fanMode.toString())
                .append("&")
                .append(setAwayModeNew.paramsDef.TEMP_SCALE)
                .append("=")
                .append(reqSetAway.preferedTemperatureUnit.toString());
        // @formatter:on
        return urlBuilder.toString();
    }

    public static String getCommandSetTStatInfo() {
        // @formatter:off
        StringBuilder urlBuilder = new StringBuilder((new iComfortServiceURI()).getURI())
                .append(setTStatInfo.PATH);
        // @formatter:on
        return urlBuilder.toString();
    }

    public static String getCommandGetGatewaysAlerts(String gatewaySN, String languageNbr, String count) {
        // @formatter:off
        StringBuilder urlBuilder = new StringBuilder((new iComfortServiceURI()).getURI())
                .append(getGatewaysAlerts.PATH)
                .append("?")
                .append(getGatewaysAlerts.paramsDef.GATEWAY_SN)
                .append("=")
                .append(gatewaySN)
                .append("&")
                .append(getGatewaysAlerts.paramsDef.LANGUAGE_NBR)
                .append("=")
                .append(languageNbr)
                .append("&")
                .append(getGatewaysAlerts.paramsDef.COUNT)
                .append("=")
                .append(count);
        // @formatter:on
        return urlBuilder.toString();
    }
}
