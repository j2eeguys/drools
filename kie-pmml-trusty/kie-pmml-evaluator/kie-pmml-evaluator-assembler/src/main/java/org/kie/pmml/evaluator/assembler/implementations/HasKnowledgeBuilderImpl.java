/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.evaluator.assembler.implementations;

import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.memorycompiler.JavaConfiguration;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.kie.dependencies.HasKnowledgeBuilder;

public class HasKnowledgeBuilderImpl implements HasKnowledgeBuilder {

    private final KnowledgeBuilderImpl knowledgeBuilder;

    public HasKnowledgeBuilderImpl(KnowledgeBuilderImpl knowledgeBuilder) {
        this.knowledgeBuilder = knowledgeBuilder;
    }

    @Override
    public ClassLoader getClassLoader() {
        return knowledgeBuilder.getRootClassLoader();
    }

    @Override
    public KnowledgeBuilder getKnowledgeBuilder() {
        return knowledgeBuilder;
    }


    /**
     * Compile the given sources and add them to given <code>Classloader</code> of the current instance.
     * Returns the <code>Class</code> with the given <b>fullClassName</b>
     * @param sourcesMap
     * @param fullClassName
     * @return
     */
    @Override
    public Class<?> compileAndLoadClass(Map<String, String> sourcesMap, String fullClassName) {
        ClassLoader classLoader = getClassLoader();
        if (!(classLoader instanceof ProjectClassLoader)) {
            throw new IllegalStateException("Expected ProjectClassLoader, received " + classLoader.getClass().getName());
        }
        ProjectClassLoader projectClassLoader = (ProjectClassLoader) classLoader;
        final Map<String, byte[]> byteCode = KieMemoryCompiler.compileNoLoad(sourcesMap, projectClassLoader, JavaConfiguration.CompilerType.ECLIPSE);
        byteCode.forEach(projectClassLoader::defineClass);
        try {
            return projectClassLoader.loadClass(fullClassName);
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }
}
