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
package org.cirdles.topsoil.app.table;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import org.junit.After;

/**
 *
 * @author John Zeringue
 */
public class TSVDataSetTest {

    private static final FileSystem FILE_SYSTEM
            = Jimfs.newFileSystem(Configuration.unix());

    private static final String NAME_WITHOUT_SPACES = "Test";
    private static final String NAME_WITH_SPACES = "Test Data Set";

    private static final String HEADERS_FLAG = "__headers__";
    private static final String OPEN_FLAG = "__open__";

    private static final Path PATH_CLOSED_WITHOUT_HEADERS
            = FILE_SYSTEM.getPath(NAME_WITHOUT_SPACES + ".tsv");

    private static final Path PATH_CLOSED_WITH_HEADERS
            = FILE_SYSTEM.getPath(NAME_WITHOUT_SPACES + HEADERS_FLAG + ".tsv");

    private static final Path PATH_CLOSED_WITH_HEADERS_AND_SPACES
            = FILE_SYSTEM.getPath(NAME_WITH_SPACES + HEADERS_FLAG + ".tsv");

    private static final Path PATH_OPEN_WITHOUT_HEADERS
            = FILE_SYSTEM.getPath(NAME_WITHOUT_SPACES + OPEN_FLAG + ".tsv");

    private static final Path PATH_OPEN_WITH_HEADERS
            = FILE_SYSTEM.getPath(
                    NAME_WITHOUT_SPACES + HEADERS_FLAG + OPEN_FLAG + ".tsv");

    private static final Path PATH_OPEN_WITH_HEADERS_AND_SPACES
            = FILE_SYSTEM.getPath(
                    NAME_WITH_SPACES + HEADERS_FLAG + OPEN_FLAG + ".tsv");

    private static final Collection<Path> PATHS = Arrays.asList(
            PATH_CLOSED_WITHOUT_HEADERS,
            PATH_CLOSED_WITH_HEADERS,
            PATH_CLOSED_WITH_HEADERS_AND_SPACES,
            PATH_OPEN_WITHOUT_HEADERS,
            PATH_OPEN_WITH_HEADERS,
            PATH_OPEN_WITH_HEADERS_AND_SPACES
    );

    private TSVDataSet instanceClosedWithoutHeaders;
    private TSVDataSet instanceClosedWithHeaders;
    private TSVDataSet instanceOpenWithHeadersAndSpaces;

    @Before
    public void instantiateDataSets() {
        instanceClosedWithoutHeaders
                = new TSVDataSet(PATH_CLOSED_WITHOUT_HEADERS);

        instanceClosedWithHeaders = new TSVDataSet(PATH_CLOSED_WITH_HEADERS);

        instanceOpenWithHeadersAndSpaces
                = new TSVDataSet(PATH_OPEN_WITH_HEADERS_AND_SPACES);
    }

    @Before
    public void createFiles() throws IOException {
        Files.createFile(PATH_CLOSED_WITHOUT_HEADERS);
        Files.createFile(PATH_CLOSED_WITH_HEADERS);
        Files.createFile(PATH_OPEN_WITH_HEADERS_AND_SPACES);
    }

    @After
    public void deleteFiles() throws IOException {
        for (Path path : PATHS) {
            Files.deleteIfExists(path);
        }
    }

    /**
     * Test of getName method, of class TSVDataSet.
     */
    @Test
    public void testGetName() {
        assertEquals(NAME_WITHOUT_SPACES, instanceClosedWithoutHeaders.getName());
        assertEquals(NAME_WITHOUT_SPACES, instanceClosedWithHeaders.getName());
        assertEquals(NAME_WITH_SPACES, instanceOpenWithHeadersAndSpaces.getName());
    }

    /**
     * Test of getTSVPath method, of class TSVDataSet.
     */
    @Test
    public void testGetPath() {
        assertEquals(PATH_CLOSED_WITHOUT_HEADERS, instanceClosedWithoutHeaders.getPath());

        assertEquals(PATH_CLOSED_WITH_HEADERS,
                instanceClosedWithHeaders.getPath());

        assertEquals(PATH_OPEN_WITH_HEADERS_AND_SPACES, instanceOpenWithHeadersAndSpaces.getPath());
    }

    /**
     * Test of hasHeaders method, of class TSVDataSet.
     */
    @Test
    public void testHasHeaders() {
        assertFalse(instanceClosedWithoutHeaders.hasHeaders());
        assertTrue(instanceClosedWithHeaders.hasHeaders());
        assertTrue(instanceOpenWithHeadersAndSpaces.hasHeaders());
    }

    /**
     * Test of isOpen method, of class TSVDataSet.
     */
    @Test
    public void testIsOpen() {
        assertFalse(instanceClosedWithoutHeaders.isOpen());
        assertFalse(instanceClosedWithHeaders.isOpen());
        assertTrue(instanceOpenWithHeadersAndSpaces.isOpen());
    }

    /**
     * Test of open method, of class TSVDataSet.
     */
    @Test
    public void testOpen() {
        instanceClosedWithoutHeaders.open();
        assertTrue(instanceClosedWithoutHeaders.isOpen());
        assertFalse(Files.exists(PATH_CLOSED_WITHOUT_HEADERS));
        assertTrue(Files.exists(PATH_OPEN_WITHOUT_HEADERS));

        instanceClosedWithHeaders.open();
        assertTrue(instanceClosedWithHeaders.isOpen());
        assertFalse(Files.exists(PATH_CLOSED_WITH_HEADERS));
        assertTrue(Files.exists(PATH_OPEN_WITH_HEADERS));

        instanceOpenWithHeadersAndSpaces.open();
        assertTrue(instanceOpenWithHeadersAndSpaces.isOpen());
        assertTrue(Files.exists(PATH_OPEN_WITH_HEADERS_AND_SPACES));
    }

    /**
     * Test of close method, of class TSVDataSet.
     */
    @Test
    public void testClose() {
        instanceClosedWithoutHeaders.close();
        assertFalse(instanceClosedWithoutHeaders.isOpen());
        assertTrue(Files.exists(PATH_CLOSED_WITHOUT_HEADERS));

        instanceClosedWithHeaders.close();
        assertFalse(instanceClosedWithHeaders.isOpen());
        assertTrue(Files.exists(PATH_CLOSED_WITH_HEADERS));

        instanceOpenWithHeadersAndSpaces.close();
        assertFalse(instanceOpenWithHeadersAndSpaces.isOpen());
        assertFalse(Files.exists(PATH_OPEN_WITH_HEADERS_AND_SPACES));
        assertTrue(Files.exists(PATH_CLOSED_WITH_HEADERS_AND_SPACES));
    }

    /**
     * Test of tsvPathMarkedAsOpen method, of class TSVDataSet.
     */
    @Test
    public void testPathMarkedAsOpen() {
        assertEquals(PATH_OPEN_WITHOUT_HEADERS,
                instanceClosedWithoutHeaders.tsvPathMarkedAsOpen());

        assertEquals(PATH_OPEN_WITH_HEADERS,
                instanceClosedWithHeaders.tsvPathMarkedAsOpen());

        assertEquals(PATH_OPEN_WITH_HEADERS_AND_SPACES,
                instanceOpenWithHeadersAndSpaces.tsvPathMarkedAsOpen());
    }

    /**
     * Test of tsvPathNotMarkedAsOpen method, of class TSVDataSet.
     */
    @Test
    public void testPathNotMarkedAsOpen() {
        assertEquals(PATH_CLOSED_WITHOUT_HEADERS,
                instanceClosedWithoutHeaders.tsvPathNotMarkedAsOpen());

        assertEquals(PATH_CLOSED_WITH_HEADERS,
                instanceClosedWithHeaders.tsvPathNotMarkedAsOpen());

        assertEquals(PATH_CLOSED_WITH_HEADERS_AND_SPACES,
                instanceOpenWithHeadersAndSpaces.tsvPathNotMarkedAsOpen());
    }

}
