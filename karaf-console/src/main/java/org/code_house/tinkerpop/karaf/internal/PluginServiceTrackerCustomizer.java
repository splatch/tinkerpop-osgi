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

import org.apache.tinkerpop.gremlin.groovy.jsr223.DependencyManager;
import org.apache.tinkerpop.gremlin.groovy.plugin.GremlinPlugin;
import org.apache.tinkerpop.gremlin.groovy.plugin.GremlinPluginException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Łukasz Dywicki <luke@code-house.org>
 */
public class PluginServiceTrackerCustomizer implements ServiceTrackerCustomizer<GremlinPlugin, GremlinPlugin> {

    private final Logger logger = LoggerFactory.getLogger(PluginServiceTrackerCustomizer.class);

    private final BundleContext bundleContext;
    private final PluginRegistry pluginRegistry;

    public PluginServiceTrackerCustomizer(BundleContext bundleContext, PluginRegistry pluginRegistry) {
        this.bundleContext = bundleContext;
        this.pluginRegistry = pluginRegistry;
    }

    @Override
    public GremlinPlugin addingService(ServiceReference<GremlinPlugin> reference) {
        GremlinPlugin gremlinPlugin = bundleContext.getService(reference);

        pluginRegistry.addPlugin(gremlinPlugin);
        logger.info("Successful registration of plugin {}", gremlinPlugin.getName());
        return gremlinPlugin;
    }

    @Override
    public void modifiedService(ServiceReference<GremlinPlugin> reference, GremlinPlugin service) {

    }

    @Override
    public void removedService(ServiceReference<GremlinPlugin> reference, GremlinPlugin service) {
        pluginRegistry.removePlugin(service);
        bundleContext.ungetService(reference);
    }

}
