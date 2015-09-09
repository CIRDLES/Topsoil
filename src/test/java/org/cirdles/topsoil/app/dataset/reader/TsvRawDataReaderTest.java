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
package org.cirdles.topsoil.app.dataset.reader;

import org.cirdles.topsoil.dataset.RawData;
import org.cirdles.topsoil.dataset.field.Field;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author John Zeringue
 */
public class TsvRawDataReaderTest {

    private static final String tsvWithHeaders
            = (""
            + "A B C\n"
            + "1 2 3\n"
            + "4 5 6\n"
            + "").replaceAll(" ", "\t");

    private static final String tsvWithoutHeaders
            = (""
            + "1 2 3\n"
            + "4 5 6\n"
            + "").replaceAll(" ", "\t");

    private RawDataReader rawDataReaderWithHeaders;
    private RawDataReader rawDataReaderWithoutHeaders;

    @Before
    public void setUpDatasetReader() {
        rawDataReaderWithHeaders = new TsvRawDataReader();
        rawDataReaderWithoutHeaders = new TsvRawDataReader(false);
    }

    @Test
    public void testReadsTSVFileWithHeaders() throws IOException {
        RawData rawData = rawDataReaderWithHeaders.read(tsvWithHeaders);

        assertEquals(3, rawData.getFields().size());
        assertEquals(2, rawData.getEntries().size());

        Field fieldA = rawData.getFields().get(0);
        Field fieldC = rawData.getFields().get(2);

        assertEquals("A", fieldA.getName());
        assertEquals("C", fieldC.getName());

        assertEquals(1., rawData.getEntries().get(0).get(fieldA).get());
        assertEquals(6., rawData.getEntries().get(1).get(fieldC).get());
    }

    @Test
    public void testReadsTSVFileWithoutHeaders() throws IOException {
        RawData rawData = rawDataReaderWithoutHeaders.read(tsvWithoutHeaders);

        assertEquals(3, rawData.getFields().size());
        assertEquals(2, rawData.getEntries().size());

        Field field0 = rawData.getFields().get(0);
        Field field2 = rawData.getFields().get(2);

        assertEquals("Field 0", field0.getName());
        assertEquals("Field 2", field2.getName());

        assertEquals(1., rawData.getEntries().get(0).get(field0).get());
        assertEquals(6., rawData.getEntries().get(1).get(field2).get());
    }

    @Test
    public void testReadsEmptyFileAsEmptyDataset() throws IOException {
        RawData rawData = rawDataReaderWithHeaders.read("");

        assertTrue(rawData.getFields().isEmpty());
        assertTrue(rawData.getEntries().isEmpty());
    }

    @Test
    public void testReadsUnacknowledgedHeaders() throws IOException {
        // shouldn't throw exception
        rawDataReaderWithoutHeaders.read(tsvWithHeaders);
    }

}
