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
package org.code_house.tinkerpop.itests;

import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;

import java.io.File;
import java.util.EnumSet;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import static org.junit.Assert.*;

/**
 * Test of tinkerpop feature set.
 *
 * @author Łukasz Dywicki <luke@code-house.org>
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class TinkerPopFeaturesTest {

    @Inject
    private FeaturesService featuresService;

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public Option[] config() {
        String karafVersion = MavenUtils.getArtifactVersion("org.apache.karaf", "apache-karaf");
        MavenArtifactUrlReference frameworkURL = maven("org.apache.karaf", "apache-karaf").type("zip").versionAsInProject();

        return options(
            karafDistributionConfiguration().karafVersion(karafVersion).frameworkUrl(frameworkURL).unpackDirectory(new File("target")),
            keepRuntimeFolder(),
            editConfigurationFilePut("etc/org.ops4j.pax.url.mvn.cfg", "org.ops4j.pax.url.mvn.repositories", ""),
            editConfigurationFileExtend("etc/org.apache.karaf.features.cfg", "featuresRepositories", "," + maven("org.code-house.tinkerpop", "features").versionAsInProject().classifier("features").type("xml").getURL())
        );
    }

    @Test
    public void shouldInstallGremlinCoreFeature() throws Exception {
        installFeature("gremlin-core");
    }

    @Test
    public void shouldInstallTinkergraphFeature() throws Exception {
        installFeature("tinkergraph-gremlin");
    }

    private void installFeature(String featureName) throws Exception {
        featuresService.installFeature(featureName, EnumSet.of(FeaturesService.Option.Verbose, FeaturesService.Option.PrintExecptionPerFeature, FeaturesService.Option.PrintBundlesToRefresh));

        assertTrue("Feature " + featureName + " is not installed", featuresService.isInstalled(featuresService.getFeature(featureName)));
    }

}
