/*
 * Copyright 2016 CIRDLES.
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

import java.util.Properties;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.browse.WebBrowser;
import org.cirdles.topsoil.app.metadata.ApplicationMetadata;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;

import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 *
 * @author Emily
 */
public class AboutDialogViewTest extends ApplicationTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ApplicationMetadata metadata;

    @Mock
    private WebBrowser browser;

    @Mock
    private Properties systemProperties;

    @Override
    public void start(Stage stage) throws Exception {
        when(metadata.getVersion()).thenReturn("v1.0.0");
        when(systemProperties.getProperty("os.name")).thenReturn("Linux");
        when(systemProperties.getProperty("os.version")).thenReturn("1.0");
        when(systemProperties.getProperty("java.version")).thenReturn("1.8.0_60");

        Parent root = new AboutDialogView(metadata, systemProperties, browser);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testShowsTopsoilVersion() {
        verifyThat("#topsoilVersionLabel", hasText("Product version: v1.0.0"));
    }

    @Test
    public void testShowsOperatingSystem() {
        verifyThat("#operatingSystemLabel", hasText("OS: Linux 1.0"));
    }

    @Test
    public void testShowsJavaVersion() {
        verifyThat("#javaVersionLabel", hasText("Java version: 1.8.0_60"));
    }

}
