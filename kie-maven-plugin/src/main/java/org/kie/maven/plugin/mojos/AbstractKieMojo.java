/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.maven.plugin.mojos;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.kie.maven.plugin.KieMavenPluginContext;
import org.kie.memorycompiler.JavaConfiguration;

import static org.kie.maven.plugin.helpers.ExecModelModeHelper.execModelParameterEnabled;

public abstract class AbstractKieMojo extends AbstractMojo {

    @Parameter(property = "dumpKieSourcesFolder", defaultValue = "")
    private String dumpKieSourcesFolder;

    @Parameter(property = "generateModel", defaultValue = "YES_WITHDRL")
    // DROOLS-5663 align kie-maven-plugin default value for generateModel configuration flag
    private String generateModel;

    @Parameter(property = "generateDMNModel", defaultValue = "no")
    private String generateDMNModel;

    @Parameter(required = true, defaultValue = "${project.build.resources}")
    protected List<Resource> resources;

    @Parameter(property = "validateDMN", defaultValue = "VALIDATE_SCHEMA,VALIDATE_MODEL,ANALYZE_DECISION_TABLE")
    protected String validateDMN;

    @Parameter(required = true, defaultValue = "${project.basedir}")
    private File projectDir;

    @Parameter(required = true, defaultValue = "${project.build.directory}")
    private File targetDirectory;

    @Parameter
    private Map<String, String> properties;

    @Parameter(required = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession mavenSession;

    @Parameter(defaultValue = "${project.resources}", required = true, readonly = true)
    private List<org.apache.maven.model.Resource> resourcesDirectories;

    /**
     * Directory containing the generated JAR.
     */
    @Parameter(required = true, defaultValue = "${project.build.outputDirectory}")
    private File outputDirectory;

    @Parameter(required = true, defaultValue = "${project.build.testSourceDirectory}")
    private File testDir;

    /**
     * Project resources folder.
     */
    @Parameter(required = true, defaultValue = "src/main/resources")
    private File resourceFolder;

    @Parameter(property = "javaCompiler", defaultValue = "ecj")
    private String javaCompiler;

    protected KieMavenPluginContext getKieMavenPluginContext() {
        return new KieMavenPluginContext(dumpKieSourcesFolder,
                                         generateModel,
                                         generateDMNModel,
                                         resources,
                                         validateDMN,
                                         projectDir,
                                         targetDirectory,
                                         properties,
                                         project,
                                         mavenSession,
                                         resourcesDirectories,
                                         outputDirectory,
                                         testDir,
                                         resourceFolder,
                                         isModelParameterEnabled(),
                                         getCompilerType(),
                                         getLog());
    }

    private boolean isModelParameterEnabled() {
        return execModelParameterEnabled(generateModel);
    }

    private JavaConfiguration.CompilerType getCompilerType() {
        return javaCompiler.equalsIgnoreCase("native") ? JavaConfiguration.CompilerType.NATIVE :
                JavaConfiguration.CompilerType.ECLIPSE;
    }
}
