/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.reteoo;

import org.drools.core.common.DefaultFactHandle;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FactHandleTest {
    /*
     * Class under test for void FactHandleImpl(long)
     */
    @Test
    public void testFactHandleImpllong() {
        final DefaultFactHandle f0 = new DefaultFactHandle( 134,
                                                            "cheese" );
        assertThat(f0.getId()).isEqualTo(134);
        assertThat(f0.getRecency()).isEqualTo(134);
    }

    /*
     * Class under test for boolean equals(Object)
     */
    @Test
    public void testEqualsObject() {
        final DefaultFactHandle f0 = new DefaultFactHandle( 134,
                                                            "cheese" );
        final DefaultFactHandle f1 = new DefaultFactHandle( 96,
                                                            "cheese" );
        final DefaultFactHandle f3 = new DefaultFactHandle( 96,
                                                            "cheese" );

        assertThat(f0).as("f0 should not equal f1").isNotEqualTo(f1);
        assertThat(f3).isEqualTo(f1);
        assertThat(f3).isNotSameAs(f1);
    }

    @Test
    public void testHashCode() {
        final DefaultFactHandle f0 = new DefaultFactHandle( 234,
                                                            "cheese" );
        assertThat(f0.getObjectHashCode()).isEqualTo("cheese".hashCode());

        assertThat(f0).hasSameHashCodeAs(234);
    }

    @Test 
    public void testInvalidate() {
        final DefaultFactHandle f0 = new DefaultFactHandle( 134,
                                                            "cheese" );
        assertThat(f0.getId()).isEqualTo(134);

        f0.invalidate();
        // invalidate no longer sets the id to -1
        assertThat(f0.getId()).isEqualTo(134);
    }

}
