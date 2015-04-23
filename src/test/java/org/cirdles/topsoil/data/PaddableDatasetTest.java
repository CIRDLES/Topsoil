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
package org.cirdles.topsoil.data;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author John Zeringue
 */
public class PaddableDatasetTest {

    private static final NumberField FIELD_A = new NumberField("A");
    private static final NumberField FIELD_B = new NumberField("B");
    private static final NumberField FIELD_C = new NumberField("C");

    private static final Entry ENTRY = new SimpleEntry();

    static {
        ENTRY.set(FIELD_A, 1);
        ENTRY.set(FIELD_B, 2);
        ENTRY.set(FIELD_C, 3);
    }

    private Dataset baseDataset;
    private PaddableDataset dataset;

    @Before
    public void buildDatasets() {
        baseDataset = new Dataset() {

            @Override
            public Optional<String> getName() {
                return Optional.of("Dataset Name");
            }

            @Override
            public List<Field> getFields() {
                return Arrays.asList(FIELD_A, FIELD_B, FIELD_C);
            }

            @Override
            public List<Entry> getEntries() {
                return Arrays.asList(ENTRY);
            }

        };

        dataset = new PaddableDataset(baseDataset);
    }

    /**
     * Test of getName method, of class PaddableDataset.
     */
    @Test
    public void testGetName() {
        assertEquals(baseDataset.getName(), dataset.getName());
    }

    /**
     * Test of getFields method, of class PaddableDataset.
     */
    @Test
    public void testGetFields() {
        assertTrue(dataset.getFields().containsAll(baseDataset.getFields()));
    }

    /**
     * Test of getEntries method, of class PaddableDataset.
     */
    @Test
    public void testGetEntries() {
        assertTrue(dataset.getEntries().containsAll(baseDataset.getEntries()));
    }

    /**
     * Test of padWith method, of class PaddableDataset.
     */
    @Test
    public void testPadWith() {
        NumberField fieldD = new NumberField("D");

        dataset.padWith(fieldD, 4);

        assertTrue(dataset.getFields().contains(fieldD));
        assertTrue(dataset.getEntries().stream()
                .allMatch(entry -> {
                    return entry.get(fieldD)
                            .map(value -> value.equals(4))
                            .orElse(false);
                }));
    }

}
