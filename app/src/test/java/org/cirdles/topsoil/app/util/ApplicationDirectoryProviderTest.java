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
package org.cirdles.topsoil.app.util;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.cirdles.topsoil.app.metadata.ApplicationMetadata;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by johnzeringue on 9/9/15.
 */
public class ApplicationDirectoryProviderTest {

    @Rule
    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ApplicationMetadata metadata;

    private ApplicationDirectoryProvider applicationDirectoryProvider;

    @Test
    public void testGetOnLinux() throws Exception {
        when(metadata.getName()).thenReturn("Topsoil");

        applicationDirectoryProvider = new ApplicationDirectoryProvider(
                metadata,
                Jimfs.newFileSystem(Configuration.unix()),
                null,
                "Linux",
                "/home/testuser");

        Path applicationDirectory = applicationDirectoryProvider.get();

        assertThat(applicationDirectory, hasToString("/home/testuser/.topsoil"));
        assertThat(Files.exists(applicationDirectory), is(true));
    }

    @Test
    public void testGetOnMacOS() throws Exception {
        when(metadata.getName()).thenReturn("Topsoil");

        applicationDirectoryProvider = new ApplicationDirectoryProvider(
                metadata,
                Jimfs.newFileSystem(Configuration.osX()),
                null,
                "Mac OS X",
                "/Users/testuser");

        Path applicationDirectory = applicationDirectoryProvider.get();

        assertThat(
                applicationDirectory,
                hasToString("/Users/testuser/Library/Application Support/Topsoil"));

        assertThat(Files.exists(applicationDirectory), is(true));
    }

    @Test
    public void testGetOnWindows() throws Exception {
        when(metadata.getName()).thenReturn("Topsoil");

        applicationDirectoryProvider = new ApplicationDirectoryProvider(
                metadata,
                Jimfs.newFileSystem(Configuration.windows()),
                "C:\\User\\Test User\\AppData\\Roaming",
                "Windows XP",
                null);

        Path applicationDirectory = applicationDirectoryProvider.get();

        assertThat(
                applicationDirectory,
                hasToString("C:\\User\\Test User\\AppData\\Roaming\\Topsoil"));

        assertThat(Files.exists(applicationDirectory), is(true));
    }

}
