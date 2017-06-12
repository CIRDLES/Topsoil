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
package org.cirdles.topsoil.app.browse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.awt.Desktop;
import java.io.IOException;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author Emily
 */
public class DesktopWebBrowserTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Desktop desktop;

    private WebBrowser browser;

    @Before
    public void setUp() {
        browser = new DesktopWebBrowser(desktop);
    }

    @Test
    public void testBrowse() throws IOException {
        // preconditions
        when(desktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);

        // logic/steps
        browser.browse("http://www.google.com");

        // postconditions
        verify(desktop).browse(any());
    }

    @Test
    public void testBrowseWhenBrowseIsNotSupported() {
        when(desktop.isSupported(Desktop.Action.BROWSE)).thenReturn(false);

        browser.browse("http://www.google.com");
    }

    @Test
    public void testBrowseWhenDesktopThrowsException() throws IOException {
        when(desktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);
        doThrow(IOException.class).when(desktop).browse(any());

        browser.browse("http://www.google.com");
    }

    @Test
    public void testBrowseWhenDesktopIsNotSupported() {
        browser = new DesktopWebBrowser(null);

        browser.browse("http://www.google.com");
    }
}
