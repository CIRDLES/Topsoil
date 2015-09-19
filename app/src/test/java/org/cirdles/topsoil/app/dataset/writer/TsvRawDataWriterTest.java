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
package org.cirdles.topsoil.app.dataset.writer;

import org.cirdles.topsoil.dataset.RawData;
import org.cirdles.topsoil.dataset.entry.Entry;
import org.cirdles.topsoil.dataset.entry.SimpleEntry;
import org.cirdles.topsoil.dataset.field.Field;
import org.cirdles.topsoil.dataset.field.NumberField;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author John Zeringue
 */
public class TsvRawDataWriterTest {

    private static final Field<Double> FIELD_A;
    private static final Field<Double> FIELD_B;
    private static final Field<Double> FIELD_C;
    private static final Field<Double> FIELD_D;

    private static final List<Field<?>> FIELDS = Arrays.asList(
            FIELD_A = new NumberField("A"),
            FIELD_B = new NumberField("B"),
            FIELD_C = new NumberField("C"),
            FIELD_D = new NumberField("D")
    );

    private RawData rawData;

    public Entry dummyEntry() {
        Entry entry = new SimpleEntry();

        entry.set(FIELD_A, 1.);
        entry.set(FIELD_B, 2.);
        entry.set(FIELD_C, 3.);
        entry.set(FIELD_D, 4.);

        return entry;
    }

    @Before
    public void setUpDataset() {
        List<Entry> entries = new ArrayList<>();

        entries.add(dummyEntry());
        entries.add(dummyEntry());

        rawData = new RawData(FIELDS, entries);
    }

    private static final String EXPECTED_WITH_DEFAULT_CONSTRUCTOR = (""
            + "\"A\"   \"B\"   \"C\"   \"D\"\n"
            + "\"1.0\" \"2.0\" \"3.0\" \"4.0\"\n"
            + "\"1.0\" \"2.0\" \"3.0\" \"4.0\"\n"
            + "").replaceAll(" +", "\t");

    @Test
    public void testWriteWithDefaultConstructor() throws Exception {
        RawDataWriter rawDataWriter = new TsvRawDataWriter();

        String tsvString = rawDataWriter.write(rawData);

        assertEquals(EXPECTED_WITH_DEFAULT_CONSTRUCTOR, tsvString);
    }

}
