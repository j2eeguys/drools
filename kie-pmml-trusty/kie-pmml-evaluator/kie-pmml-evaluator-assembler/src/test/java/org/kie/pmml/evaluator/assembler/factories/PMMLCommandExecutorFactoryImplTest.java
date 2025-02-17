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

package org.kie.pmml.evaluator.assembler.factories;

import org.junit.jupiter.api.Test;
import org.kie.internal.pmml.PMMLCommandExecutor;
import org.kie.pmml.evaluator.assembler.command.PMMLCommandExecutorImpl;

import static org.assertj.core.api.Assertions.assertThat;

public class PMMLCommandExecutorFactoryImplTest {

    @Test
    void newPMMLCommandExecutor() {
        PMMLCommandExecutorFactoryImpl factory = new PMMLCommandExecutorFactoryImpl();
        PMMLCommandExecutor retrieved = factory.newPMMLCommandExecutor();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(PMMLCommandExecutorImpl.class);
    }
}