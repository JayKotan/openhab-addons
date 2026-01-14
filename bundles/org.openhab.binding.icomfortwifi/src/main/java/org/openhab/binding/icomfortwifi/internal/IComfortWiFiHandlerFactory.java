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
package org.openhab.binding.icomfortwifi.internal;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.icomfortwifi.internal.discovery.IComfortWiFiDiscoveryService;
import org.openhab.binding.icomfortwifi.internal.handler.IComfortWiFiBridgeHandler;
import org.openhab.binding.icomfortwifi.internal.handler.IComfortWiFiHeatingZoneHandler;
import org.openhab.binding.icomfortwifi.internal.handler.IComfortWiFiTemperatureControlSystemHandler;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * 
 *
 * @author Konstantin Panchenko - Initial contribution
 * @author Jason Kota - Updated for openHAB 5.x compliance
 */
@Component(service = { ThingHandlerFactory.class })
@NonNullByDefault
public class IComfortWiFiHandlerFactory extends BaseThingHandlerFactory {

    private final Map<ThingUID, @Nullable ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();

    private @Nullable HttpClient httpClient;
    private @Nullable HttpClientFactory httpClientFactory;
    private @Nullable BundleContext bundleContext;

    @Reference
    protected void setHttpClientFactory(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }

    @Activate
    public void activate(ComponentContext componentContext) {
        super.activate(componentContext);
        this.bundleContext = componentContext.getBundleContext();
        this.httpClient = Objects.requireNonNull(httpClientFactory).getCommonHttpClient();
    }

    @SuppressWarnings("unused")
    @Deactivate
    public void deactivate() {
        this.bundleContext = null;
        this.httpClient = null;
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return IComfortWiFiBindingConstants.SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(IComfortWiFiBindingConstants.THING_TYPE_ICOMFORT_ACCOUNT)) {
            HttpClient client = Objects.requireNonNull(this.httpClient);
            IComfortWiFiBridgeHandler bridge = new IComfortWiFiBridgeHandler((Bridge) thing, client);
            registeriComfortWiFiDiscoveryService(bridge);
            return bridge;
        }

        if (thingTypeUID.equals(IComfortWiFiBindingConstants.THING_TYPE_ICOMFORT_THERMOSTAT)) {
            return new IComfortWiFiTemperatureControlSystemHandler(thing);
        }

        if (thingTypeUID.equals(IComfortWiFiBindingConstants.THING_TYPE_ICOMFORT_ZONE)) {
            return new IComfortWiFiHeatingZoneHandler(thing);
        }

        return null;
    }

    public void registeriComfortWiFiDiscoveryService(IComfortWiFiBridgeHandler bridge) {
        BundleContext ctx = Objects.requireNonNull(this.bundleContext);
        IComfortWiFiDiscoveryService discoveryService = new IComfortWiFiDiscoveryService(bridge);
        ServiceRegistration<?> reg = ctx.registerService(DiscoveryService.class.getName(), discoveryService,
                new Hashtable<>());
        discoveryServiceRegs.put(bridge.getThing().getUID(), reg);
    }

    @Override
    protected void removeHandler(ThingHandler thingHandler) {
        if (thingHandler instanceof IComfortWiFiBridgeHandler) {
            ThingUID uid = thingHandler.getThing().getUID();
            ServiceRegistration<?> reg = discoveryServiceRegs.get(uid);

            if (reg != null) {
                BundleContext ctx = this.bundleContext;
                if (ctx != null) {
                    Object svc = ctx.getService(reg.getReference());
                    if (svc instanceof IComfortWiFiDiscoveryService discoveryService) {
                        discoveryService.deactivate();
                    }
                }

                reg.unregister();
                discoveryServiceRegs.remove(uid);
            }
        }
    }
}
