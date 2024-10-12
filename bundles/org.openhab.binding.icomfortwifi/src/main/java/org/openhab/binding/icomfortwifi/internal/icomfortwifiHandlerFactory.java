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
package org.openhab.binding.icomfortwifi.internal;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.icomfortwifi.internal.discovery.iComfortWiFiDiscoveryService;
import org.openhab.binding.icomfortwifi.internal.handler.iComfortWiFiBridgeHandler;
import org.openhab.binding.icomfortwifi.internal.handler.iComfortWiFiHeatingZoneHandler;
import org.openhab.binding.icomfortwifi.internal.handler.iComfortWiFiTemperatureControlSystemHandler;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the thing factory for this binding
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kotan - Added @nonNullByDefault. Change the @Refrence with @Activate iComfortWiFiHandlerFactory code
 *
 */
@Component(service = ThingHandlerFactory.class, configurationPid = "binding.icomfortwifi")
@NonNullByDefault
public class iComfortWiFiHandlerFactory extends BaseThingHandlerFactory {

    private final Map<ThingUID, @Nullable ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();
    private HttpClient httpClient;

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return iComfortWiFiBindingConstants.SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();
        if (thingTypeUID.equals(iComfortWiFiBindingConstants.THING_TYPE_ICOMFORT_ACCOUNT)) {
            iComfortWiFiBridgeHandler bridge = new iComfortWiFiBridgeHandler((Bridge) thing, httpClient);
            registeriComfortWiFiDiscoveryService(bridge);
            return bridge;
        } else if (thingTypeUID.equals(iComfortWiFiBindingConstants.THING_TYPE_ICOMFORT_THERMOSTAT)) {
            return new iComfortWiFiTemperatureControlSystemHandler(thing);
        } else if (thingTypeUID.equals(iComfortWiFiBindingConstants.THING_TYPE_ICOMFORT_ZONE)) {
            return new iComfortWiFiHeatingZoneHandler(thing);
        }
        // Throw an exception if no matching handler found
        return null;
    }

    public void registeriComfortWiFiDiscoveryService(iComfortWiFiBridgeHandler iComfortWiFiBridgeHandler) {
        iComfortWiFiDiscoveryService discoveryService = new iComfortWiFiDiscoveryService(iComfortWiFiBridgeHandler);

        this.discoveryServiceRegs.put(iComfortWiFiBridgeHandler.getThing().getUID(),
                bundleContext.registerService(DiscoveryService.class.getName(), discoveryService, new Hashtable<>()));
    }

    @Override
    public ThingHandler registerHandler(Thing thing) {
        return super.registerHandler(thing);
    }

    @Override
    protected void removeHandler(ThingHandler thingHandler) {
        if (thingHandler instanceof iComfortWiFiBridgeHandler) {
            ServiceRegistration<?> serviceReg = this.discoveryServiceRegs.get(thingHandler.getThing().getUID());
            if (serviceReg != null) {
                iComfortWiFiDiscoveryService service = (iComfortWiFiDiscoveryService) bundleContext
                        .getService(serviceReg.getReference());
                if (service != null) {
                    service.deactivate();
                }
                serviceReg.unregister();
                discoveryServiceRegs.remove(thingHandler.getThing().getUID());
            }
        }
    }

    @Activate
    public iComfortWiFiHandlerFactory(@Reference final HttpClientFactory httpClientFactory) {
        HttpClient client = httpClientFactory.getCommonHttpClient();
        this.httpClient = client; // This should now not be null
    }

    // @Reference
    // protected void setHttpClientFactory(HttpClientFactory httpClientFactory) {
    // this.httpClient = httpClientFactory.getCommonHttpClient();
    // }

    // protected void unsetHttpClientFactory(HttpClientFactory httpClientFactory) {
    // this.httpClient = null;
    // }
}
