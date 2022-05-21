/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.impl;

import java.util.Collections;
import java.util.List;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.compiler.DMNProfile;

public class VoidDMNRuntimeKB implements DMNRuntimeKB {

    @Override
    public List<DMNModel> getModels() {
        return Collections.emptyList();
    }

    @Override
    public DMNModel getModel(String namespace, String modelName) {
        return null;
    }

    @Override
    public DMNModel getModelById(String namespace, String modelId) {
        return null;
    }

    @Override
    public List<DMNProfile> getProfiles() {
        return Collections.emptyList();
    }

    @Override
    public List<DMNRuntimeEventListener> getListeners() {
        return Collections.emptyList();
    }

    @Override
    public ClassLoader getRootClassLoader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InternalKnowledgeBase getInternalKnowledgeBase() {
        throw new UnsupportedOperationException();
    }

    @Override
    public KieRuntimeFactory getKieRuntimeFactory(String kieBaseName) {
        throw new UnsupportedOperationException();
    }
}
