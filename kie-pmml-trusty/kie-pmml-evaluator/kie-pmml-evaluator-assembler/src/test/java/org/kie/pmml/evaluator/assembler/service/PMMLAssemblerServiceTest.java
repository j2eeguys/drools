/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.evaluator.assembler.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class PMMLAssemblerServiceTest {

    private static Map<String, String[]> resourcePathMap;

    @BeforeAll
    public static void setup() {
        resourcePathMap = new HashMap<>();
        resourcePathMap.put("/a/linux/path/a-dashed-name.pmml", new String[]{"AdashednameFactory", "adashedname"});
        resourcePathMap.put("/a_/l inux/p-a_th/an_underscored_name.pmml", new String[]{"AnunderscorednameFactory", "anunderscoredname"});
        resourcePathMap.put("/a_/l inux/p-a_th/a spaced name.pmml", new String[]{"AspacednameFactory", "aspacedname"});
        resourcePathMap.put("/A_/L inux/pAtH/AnUpperCasedMame.pmml", new String[]{"AnUpperCasedMameFactory", "anuppercasedmame"});
        resourcePathMap.put("C:\\from\\window\\w-ind_ow Path\\AnUpperCasedMame.pmml", new String[]{"AnUpperCasedMameFactory", "anuppercasedmame"});
        resourcePathMap.put("C:\\from/window\\mixed Path/AnUpperCasedMame.pmml", new String[]{"AnUpperCasedMameFactory", "anuppercasedmame"});
    }


    @Test
    void getFactoryClassNamePackageNameResourceNoFile() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Resource resource = new MockResource();
            PMMLAssemblerService.getFactoryClassNamePackageName(resource);
        });
    }

    @Test
    void getFactoryClassNamePackageNameResourceWithFile() {
        resourcePathMap.forEach((sourcePath, comparison) -> {
            Resource resource = new MockResource(sourcePath);
            String[] retrieved = PMMLAssemblerService.getFactoryClassNamePackageName(resource);
            commonVerifyStrings(retrieved, comparison);
        });
    }

    @Test
    void testGetFactoryClassNamePackageName() {
        resourcePathMap.forEach((sourcePath, comparison) -> {
            String[] retrieved = PMMLAssemblerService.getFactoryClassNamePackageName(sourcePath);
            commonVerifyStrings(retrieved, comparison);
        });
    }

    private void commonVerifyStrings(String[] toVerify, String[] comparison) {
        assertThat(comparison.length).isEqualTo(toVerify.length);
        assertThat(toVerify.length).isEqualTo(2);
        for (int i = 0; i < toVerify.length; i ++) {
        	assertThat(toVerify[i]).isEqualTo(comparison[i]);
        }
    }

    private static class  MockResource implements Resource {

        private String sourcePath;

        public MockResource() {
        }

        public MockResource(String sourcePath) {
            this.sourcePath = sourcePath;
        }


        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public Reader getReader() throws IOException {
            return null;
        }

        @Override
        public String getSourcePath() {
            return sourcePath;
        }

        @Override
        public String getTargetPath() {
            return null;
        }

        @Override
        public ResourceType getResourceType() {
            return null;
        }

        @Override
        public ResourceConfiguration getConfiguration() {
            return null;
        }

        @Override
        public Resource setSourcePath(String path) {
            return null;
        }

        @Override
        public Resource setTargetPath(String path) {
            return null;
        }

        @Override
        public Resource setResourceType(ResourceType type) {
            return null;
        }

        @Override
        public Resource setConfiguration(ResourceConfiguration conf) {
            return null;
        }
    }
}