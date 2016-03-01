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
package org.cirdles.topsoil.app.dataset.field;

import javafx.util.StringConverter;
import org.cirdles.topsoil.app.dataset.entry.SimpleEntry;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author John Zeringue
 */
public class NumberFieldTest {

    private Field<Double> numberField;
    private StringConverter<Double> stringConverter;

    @Before
    public void setUpField() {
        numberField = new NumberField("Test Field");
        stringConverter = numberField.getStringConverter();
    }

    @Test
    public void testGenerics() {
        new SimpleEntry().set(numberField, 3.07); // should compile
    }

    @Test
    public void testStringConverterConvertsDouble() {
        assertEquals("3.07", stringConverter.toString(3.07));
    }

    @Test
    public void testStringConverterConvertsNullDouble() {
        assertEquals("---", stringConverter.toString(null));
    }

    @Test
    public void testStringConverterConvertsString() {
        assertEquals(3.07, stringConverter.fromString("3.07"), 10e-10);
    }

}
