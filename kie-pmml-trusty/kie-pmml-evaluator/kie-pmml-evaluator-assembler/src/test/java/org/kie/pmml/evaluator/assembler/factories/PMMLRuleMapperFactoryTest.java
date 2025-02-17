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

import static org.assertj.core.api.Assertions.assertThat;

public class PMMLRuleMapperFactoryTest {

    @Test
    void getPredictionRuleMapperSourceWithoutPackage() {
        final String fullRuleName = "FULL_RULE_NAME";
        String retrieved = PMMLRuleMapperFactory.getPMMLRuleMapperSource(fullRuleName);
        assertThat(retrieved).isNotNull();
        String expected = String.format("public final static Model model = new %s();", fullRuleName);
        assertThat(retrieved).contains(expected);
    }

    @Test
    void getPredictionRuleMapperSourceWithPackage() {
        final String packageName = "PACKAGE";
        final String ruleName = "RULE_NAME";
        final String fullRuleName = packageName + "." + ruleName;
        String retrieved = PMMLRuleMapperFactory.getPMMLRuleMapperSource(fullRuleName);
        assertThat(retrieved).isNotNull();
        String expected = String.format("package %s;", packageName);
        assertThat(retrieved).contains(expected);
        expected = String.format("public final static Model model = new %s();", fullRuleName);
        assertThat(retrieved).contains(expected);
    }
}