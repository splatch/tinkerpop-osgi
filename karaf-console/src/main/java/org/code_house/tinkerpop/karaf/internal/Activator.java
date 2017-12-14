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

import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngineFactory;
import org.apache.tinkerpop.gremlin.groovy.plugin.GremlinPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.ServiceTracker;

import javax.script.ScriptEngineFactory;
import java.util.Hashtable;


/**
 * Starting point for OSGi lifecycle.
 *
 * @author Łukasz Dywicki <luke@code-house.org>
 */
public class Activator implements BundleActivator {

    private ServiceRegistration<PluginRegistry> pluginRegistryRegistration;
    private ServiceRegistration<ScriptEngineFactory> scriptEngineFactoryRegistration;
    private BundleTracker bundleTracker;
    private ServiceTracker serviceTracker;

    @Override
    public void start(BundleContext context) throws Exception {
        DefaultPluginRegistry pluginRegistry = new DefaultPluginRegistry();

        pluginRegistryRegistration = context.registerService(PluginRegistry.class, pluginRegistry, new Hashtable<>());
        scriptEngineFactoryRegistration = context.registerService(ScriptEngineFactory.class, new GremlinGroovyScriptEngineFactory(), new Hashtable<>());

        bundleTracker = new BundleTracker<>(context, Bundle.ACTIVE, new PluginBundleTrackerCustomizer());
        bundleTracker.open();

        serviceTracker = new ServiceTracker<>(context, GremlinPlugin.class, new PluginServiceTrackerCustomizer(context, pluginRegistry));
        serviceTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (pluginRegistryRegistration != null) {
            pluginRegistryRegistration.unregister();
        }
        if (scriptEngineFactoryRegistration != null) {
            scriptEngineFactoryRegistration.unregister();
        }
        if (bundleTracker != null) {
            bundleTracker.close();
        }
        if (serviceTracker != null) {
            serviceTracker.close();
        }
    }

}
