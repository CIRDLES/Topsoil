/*
 * Copyright 2015 CIRDLES.
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
package org.cirdles.topsoil.app;

import static org.cirdles.topsoil.app.ExpressionType.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author John Zeringue
 */
public class UncertaintyVariableFormatTest {
    
    private UncertaintyVariableFormat oneSigmaAbsolute;
    private UncertaintyVariableFormat oneSigmaPercentage;
    private UncertaintyVariableFormat twoSigmaAbsolute;
    private UncertaintyVariableFormat twoSigmaPercentage;
    
    @Before
    public void initializeUncertaintyVariableFormats() {
        oneSigmaAbsolute = new UncertaintyVariableFormat(1, ABSOLUTE);
        oneSigmaPercentage = new UncertaintyVariableFormat(1, PERCENTAGE);
        twoSigmaAbsolute = new UncertaintyVariableFormat(2, ABSOLUTE);
        twoSigmaPercentage = new UncertaintyVariableFormat(2, PERCENTAGE);
    }

    /**
     * Test of normalizeAbsolute method, of class UncertaintyVariableFormat.
     */
    @Test
    public void testNormalizeAbsolute() {
        assertEquals(3.24, oneSigmaAbsolute.normalizeAbsolute(3.24), 10e-10);
        assertEquals(3.24, twoSigmaAbsolute.normalizeAbsolute(6.48), 10e-10);
    }

    /**
     * Test of normalizePercentage method, of class UncertaintyVariableFormat.
     */
    @Test
    public void testNormalizePercentage() {
        assertEquals(2.72, oneSigmaPercentage.normalizePercentage(10, 27.2), 10e-10);
        assertEquals(2.72, twoSigmaPercentage.normalizePercentage(10, 54.4), 10e-10);
    }

    /**
     * Test of normalize method, of class UncertaintyVariableFormat.
     */
    @Test
    public void testNormalize_double_double() {
        assertEquals(3.07, oneSigmaAbsolute.normalize(3.07, 30.7), 10e-10);
        assertEquals(3.07, oneSigmaPercentage.normalize(10, 30.7), 10e-10);
        assertEquals(3.07, twoSigmaAbsolute.normalize(6.14, 30.7), 10e-10);
        assertEquals(3.07, twoSigmaPercentage.normalize(20, 30.7), 10e-10);
    }

    /**
     * Test of normalize method, of class UncertaintyVariableFormat.
     */
    @Test
    public void testNormalize_Number_Number() {
        // TODO
    }
    
}
