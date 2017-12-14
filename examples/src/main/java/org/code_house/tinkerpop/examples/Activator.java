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
package org.code_house.tinkerpop.examples;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.List;

/**
 * @author Łukasz Dywicki <luke@code-house.org>
 */
public class Activator implements BundleActivator {
    private Graph graph;

    @Override
    public void start(BundleContext context) throws Exception {
        BaseConfiguration configuration = new BaseConfiguration();
        configuration.setProperty(Graph.GRAPH, TinkerGraph.class.getName());

        graph = TinkerGraph.open(configuration);

        Vertex foo = graph.addVertex("person");
        foo.property("name", "Lukasz Dywicki");
        System.out.println(foo + " " + foo.label() + " " + foo.id() + " " + foo.keys());
        Vertex bar = graph.addVertex("person");
        bar.property("name", "Paulina Dywicka");

        foo.addEdge("knows", bar);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (graph != null) {
            GraphTraversal<Edge, Edge> traversal = graph.traversal().E().hasLabel("knows");
            List<Edge> edges = traversal.toList();
            for (Edge edge : edges) {
                System.out.println(edge.inVertex().property("name") + " knows " + edge.outVertex().property("name"));
            }
        }
        graph.close();
    }
}
