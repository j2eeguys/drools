/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.efesto.compilationmanager.api.service;

import java.util.List;

import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.memorycompiler.KieMemoryCompiler;

/**
 * The compilation-related interface to be implemented by engine-plugin.
 * It will be looked for with SPI, so each engine should declare that implementation inside
 * <code>src/main/resources/META-INF/services/org.kie.efesto.compilationmanager.api.service.KieCompilerService</code> file
 */
public interface KieCompilerService {


    <T extends EfestoResource> boolean canManageResource(T toProcess);

    /**
     * Produce one <code>E</code> from the given <code>T</code>
     * <p>
     * Implementation are also required to generate a "mapping" class, i.e. a class specific for the given
     * model responsible to list all the other generated ones; engine-specific runtimes will look for such
     * class to know if it can manage given resource
     *
     * @param toProcess
     * @param memoryCompilerClassLoader
     * @return
     */
    <T extends EfestoResource, E extends EfestoCompilationOutput> List<E> processResource(T toProcess, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader);

}
