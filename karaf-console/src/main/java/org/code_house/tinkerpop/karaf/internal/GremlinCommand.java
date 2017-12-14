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

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.SubShellAction;
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.groovy.plugin.GremlinPluginException;

import javax.script.ScriptEngineFactory;
import java.util.ArrayList;

/**
 * @author Łukasz Dywicki <luke@code-house.org>
 */
@Command(scope = "gremlin", name = "query")
public class GremlinCommand extends SubShellAction implements Action {

    private final GremlinGroovyScriptEngine scriptEngine;

    @Argument(index = 0, name = "script", description = "Script to execute", required = true, multiValued = true)
    private String script;

    public GremlinCommand(ScriptEngineFactory engine, PluginRegistry pluginRegistry) throws GremlinPluginException {
        scriptEngine = (GremlinGroovyScriptEngine) engine.getScriptEngine();

        scriptEngine.loadPlugins(new ArrayList<>(pluginRegistry.getPlugins()));
    }

    @Override
    public Object execute(CommandSession session) throws Exception {
        return scriptEngine.eval(script);
    }
}
