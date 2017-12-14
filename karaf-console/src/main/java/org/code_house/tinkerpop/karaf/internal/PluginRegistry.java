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

import org.apache.tinkerpop.gremlin.groovy.plugin.GremlinPlugin;

import java.util.Set;

/**
 * @author Łukasz Dywicki <luke@code-house.org>
 */
public interface PluginRegistry {

    void addPlugin(GremlinPlugin plugin);
    void removePlugin(GremlinPlugin plugin);

    Set<GremlinPlugin> getPlugins();
}
