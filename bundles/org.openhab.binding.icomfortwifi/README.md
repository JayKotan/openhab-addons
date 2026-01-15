<!-- markdownlint-disable MD033 MD013 MD026 MD036 MD040 -->


# iComfort WiFi Binding

This [iComfort WiFi binding](https://www.myicomfort.com/Default.aspx) integrates Lennox iComfort WiFi thermostats into openHAB using the Lennox Cloud API.

> **Note:** This binding is compatible with older iComfort WiFi systems. It will not work with newer S30, E30, or M30 Smart Hub systems.

## Supported Things

| Thing Type | ID         | Description                                    |
|------------|------------|------------------------------------------------|
| bridge     | account    | Connection to the Lennox iComfort Wi‑Fi API.   |
| thing      | thermostat | Displays the last alert (even if cleared).     |
| thing      | zone       | A specific climate zone (temperature & modes). |


Next paragraph starts here…

## Thing Configuration

### Bridge (account)

The account bridge serves as the central connection point to the Lennox API.

| Parameter        | Type    | Required | Default | Description                          |
|------------------|---------|----------|---------|--------------------------------------|
| userName         | text    | Yes      | —       | Your Lennox iComfort login email.    |
| password         | text    | Yes      | —       | Your Lennox iComfort password.       |
| refreshInterval  | integer | No       | 30      | Seconds between data refreshes.      |

### Zone (zone)

| Parameter | Type | Description                               |
|-----------|------|-------------------------------------------|
| id        | text | The unique ID of the zone.                |
| name      | text | The name of the zone (e.g., Living Room). |

## Channels

The zone thing type supports the following channels:

## Channels

The zone thing type exposes the following channels:

| Channel ID        | Item Type            | Label            | Description                                                                    |
|-------------------|----------------------|------------------|--------------------------------------------------------------------------------|
| temperature       | Number:Temperature   | Temperature      | Current indoor temperature.                                                    |
| humidity          | Number:Dimensionless | Humidity         | Current indoor humidity.                                                       |
| system-status     | String               | System Status    | Current HVAC activity (HEATING, COOLING, IDLE, WAITING, EMERGENCY_HEAT).       |
| operation-mode    | String               | Operation Mode   | Primary system mode (HEAT_ONLY, COOL_ONLY, HEAT_OR_COOL, OFF).                 |
| fan-mode          | String               | Fan Mode         | Fan behavior (AUTO, ON, CIRCULATE).                                            |
| away-mode         | String               | Away Mode        | Raw thermostat Away state (AWAY_ON / AWAY_OFF).                                |
| heat-set-point    | Number:Temperature   | Heat Setpoint    | Target heating temperature.                                                    |
| cool-set-point    | Number:Temperature   | Cool Setpoint    | Target cooling temperature.                                                    |
| program-schedule  | String               | Program Schedule | Active schedule name (e.g., summer, winter, spring fall, save energy, custom). |


## Full Example

### icomfort.things

```things
Bridge icomfortwifi:account:myaccount [
    userName="user@email.com",
    password="your_password",
    refreshInterval=60
] {
    Thing thermostat my_display [ id="DISPLAY_123" ]
    Thing zone living_room [ id="ZONE_1", name="Main Living" ]
}

## Full Example

### icomfort.things

Bridge icomfortwifi:account:myaccount [ userName="user@email.com", password="your_password", refreshInterval=60 ] {
    Thing thermostat my_display [ id="DISPLAY_123" ]
    Thing zone living_room [ id="ZONE_1", name="Main Living" ]
}

### icomfort.items

// Zone items
Number:Temperature Thermostat_Temperature "Temperature [%.1f %unit%]" <temperature> (gWholeHouse) {channel="icomfortwifi:zone:demoaccount:home_zone_1:Temperature"}
Number:Dimensionless Thermostat_Humidity "Humidity [%.1f %unit%]" <humidity> (gWholeHouse) {channel="icomfortwifi:zone:demoaccount:home_zone_1:Humidity"}
String Living_Thermostat_Status "System Status [%s]" <heating> (gWholeHouse) {channel="icomfortwifi:zone:demoaccount:home_zone_1:SystemStatus"}
String Living_Thermostat_Mode "Operation Mode [%s]" <heating> (gWholeHouse) {channel="icomfortwifi:zone:demoaccount:home_zone_1:OperationMode"}
String Living_Thermostat_Away_Mode "Away Mode [%s]" <heating> (gWholeHouse) {channel="icomfortwifi:zone:demoaccount:home_zone_1:AwayMode"}
Switch Living_Thermostat_EcoMode "Eco Mode" <energy> { channel="icomfortwifi:zone:myaccount:living_room:eco-mode" }
String Living_Thermostat_Fan_Mode "Fan Mode [%s]" <fan> (gWholeHouse) {channel="icomfortwifi:zone:demoaccount:home_zone_1:FanMode"}
Number:Temperature Thermostat_Cool_Point "Cool Set Point [%.1f %unit%]" <temperature> (gWholeHouse) {channel="icomfortwifi:zone:demoaccount:home_zone_1:CoolSetPoint"}
Number:Temperature Thermostat_Heat_Point "Heat Set Point [%.1f %unit%]" <temperature> (gWholeHouse) {channel="icomfortwifi:zone:demoaccount:home_zone_1:HeatSetPoint"}

// Thermostat Alerts
String Alarm_Description "Alarm Description [%s]" <alarm> (gWholeHouse) {channel="icomfortwifi:thermostat:demoaccount:thermostat_1:alertsAndReminders#AlarmDescription"}
Number Alarm_Code "Alarm Code [%s]" <alarm> (gWholeHouse) {channel="icomfortwifi:thermostat:demoaccount:thermostat_1:alertsAndReminders#AlarmNbr"}
String Alarm_Type "Alarm Type [%s]" <alarm> (gWholeHouse) {channel="icomfortwifi:thermostat:demoaccount:thermostat_1:alertsAndReminders#AlarmType"}
String Alarm_Status "Alarm Status [%s]" <alarm> (gWholeHouse) {channel="icomfortwifi:thermostat:demoaccount:thermostat_1:alertsAndReminders#AlarmStatus"}
String Alarm_DateTimeSet "Alarm Date Time Set [%s]" <alarm> (gWholeHouse) {channel="icomfortwifi:thermostat:demoaccount:thermostat_1:alertsAndReminders#DateTimeSet"}
String Alarm_DateTimeReset "Alarm Date Time Reset [%s]" <alarm> (gWholeHouse) {channel="icomfortwifi:thermostat:demoaccount:thermostat_1:alertsAndReminders#DateTimeReset"}

### icomfort.sitemap

sitemap icomfort label="Thermostat Control" {
    Frame label="Climate Control" {
        Text item=Thermostat_Temperature
        Text item=Thermostat_Humidity
        Text item=Thermostat_Status
        Selection item=Thermostat_Mode mappings=[IDLE="System is idle", HEATING="System is heating", COOLING="System is cooling", WAITING="System is waiting", EMERGENCY_HEAT="System is emergency heating"]
        Switch item=Thermostat_Away_Mode mappings=[AWAY_ON="Away", AWAY_OFF="Not Away"]
        Selection item=Thermostat_Fan_Mode mappings=[AUTO="Auto", ON="On", CIRCULATE="Circulate"]
        Setpoint item=Thermostat_Cool_Point
        Setpoint item=Thermostat_Heat_Point
    }
    Frame label="System Alerts" {
        Text item=Alarm_Description
        Text item=Alarm_Code
        Text item=Alarm_Type
        Text item=Alarm_Status
        Text item=Alarm_DateTimeSet
        Setpoint item=Alarm_Number
    }
}

## Credits & Thanks

This binding was based on the Nest and EVO Home bindings. Original code and development credits go to Konstantin Panchenko. This version has been updated by Jason Kotan for openHAB 5.2.

---

**Footnote:** During testing, the heat and cool set points may take a minute or two to become adjustable after the binding comes online. Once initialized, they operate normally.
