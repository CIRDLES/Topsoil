/*
 * Copyright 2014 pfif.
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
package org.cirdles.topsoil;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author pfif
 */
public class ToolsTest {
    
    @Test
    public void testSuperscriptParser() {
        String toBeConverted = "sometext^0123456789^sometext_0123456789_";
        String converted = Tools.SUPERSCRIPTPARSER_CONVERTER.toString(toBeConverted);
        
        Assert.assertEquals("sometext\u2070\u00B9\u00B2\u00B3\u2074\u2075\u2076\u2077\u2078\u2079"
                + "sometext\u2080\u2081\u2082\u2083\u2084\u2085\u2086\u2087\u2088\u2089",
                            converted);
        
        Assert.assertEquals(toBeConverted, Tools.SUPERSCRIPTPARSER_CONVERTER.fromString(converted));
    }
}
