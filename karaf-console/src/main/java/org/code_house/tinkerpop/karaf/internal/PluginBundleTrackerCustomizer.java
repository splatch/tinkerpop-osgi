/*
 * (C) Copyright 2016 Code-House and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.code_house.tinkerpop.karaf.internal;

import com.google.common.collect.ImmutableMap;
import org.apache.tinkerpop.gremlin.groovy.plugin.GremlinPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * Bundle tracker fetching META-INF/services entries used by gremlin console.
 *
 * @author Łukasz Dywicki <luke@code-house.org>
 */
public class PluginBundleTrackerCustomizer implements BundleTrackerCustomizer<List<ServiceRegistration<GremlinPlugin>>> {

    private final Logger logger = LoggerFactory.getLogger(PluginBundleTrackerCustomizer.class);

    @Override
    public List<ServiceRegistration<GremlinPlugin>> addingBundle(Bundle bundle, BundleEvent event) {
        URL entry = bundle.getEntry("/META-INF/services/" + GremlinPlugin.class.getName());

        if (entry == null) {
            logger.trace("Ignoring bundle {}. No META-INF entries found", bundle.getSymbolicName());
            return null;
        }

        List<ServiceRegistration<GremlinPlugin>> plugins = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(entry.openStream()))) {
            String pluginClassName;
            while ((pluginClassName = reader.readLine()) != null) {
                try {
                    Class<?> pluginClass = bundle.loadClass(pluginClassName);

                    if (GremlinPlugin.class.isAssignableFrom(pluginClass)) {
                        Object pluginInstance = pluginClass.newInstance();
                        ServiceRegistration<GremlinPlugin> registration = bundle.getBundleContext().registerService(
                            GremlinPlugin.class, (GremlinPlugin) pluginInstance, getProperties(bundle));
                        plugins.add(registration);
                    }
                } catch (ClassNotFoundException e) {
                    logger.error("Ignored gremlin console extension bundle {} declares plugin {} which is not visible from it's class loader", bundle.getSymbolicName(), pluginClassName, e);
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("Ignored gremlin console plugin {} which breaks contract by not defining no-arg constructor", pluginClassName, e);
                }
            }
        } catch (IOException e) {
            logger.error("Unable to read service entries from {} declared by {}", entry, bundle.getSymbolicName(), e);
        }

        logger.info("Loaded and registered {} plugins from extension bundle {}", plugins.size(), bundle.getSymbolicName());
        return plugins;
    }

    private Dictionary<String, Object> getProperties(Bundle bundle) {
        return new Hashtable<>(ImmutableMap.of(
            "provider", bundle.getSymbolicName(),
            "version", bundle.getVersion()
        ));
    }

    @Override
    public void modifiedBundle(Bundle bundle, BundleEvent event, List<ServiceRegistration<GremlinPlugin>> object) {
        removedBundle(bundle, event, object);
        addingBundle(bundle, event);
    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent event, List<ServiceRegistration<GremlinPlugin>> object) {
        if (object != null) {
            for (ServiceRegistration<GremlinPlugin> pluginServiceRegistration : object) {
                pluginServiceRegistration.unregister();
            }
        }
    }

}
