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

import com.johnzeringue.extendsfx.annotation.ResourceBundle;
import com.johnzeringue.extendsfx.layout.CustomVBox;
import java.util.Properties;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javax.inject.Inject;
import org.cirdles.topsoil.app.browse.WebBrowser;
import org.cirdles.topsoil.app.metadata.ApplicationMetadata;

/**
 *
 * @author Emily
 */
@ResourceBundle("Resources")
public class AboutDialogView extends CustomVBox<AboutDialogView> {

    @FXML
    private Label javaVersionLabel;

    @FXML
    private Label operatingSystemLabel;

    @FXML
    private Label topsoilVersionLabel;

    private ApplicationMetadata metadata;
    private Properties systemProperties;
    private WebBrowser webBrowser;

    @Inject
    public AboutDialogView(
            ApplicationMetadata metadata,
            Properties systemProperties,
            WebBrowser webBrowser) {
        super(self -> {
            self.metadata = metadata;
            self.systemProperties = systemProperties;
            self.webBrowser = webBrowser;
        });
    }

    private String operatingSystem() {
        String osName = systemProperties.getProperty("os.name");
        String osVersion = systemProperties.getProperty("os.version");
        return "OS: " + osName + " " + osVersion;
    }

    private String javaVersion() {
        return "Java version: " + systemProperties.getProperty("java.version");
    }

    @FXML
    private void initialize() {
        javaVersionLabel.setText(javaVersion());
        operatingSystemLabel.setText(operatingSystem());
        topsoilVersionLabel.setText("Product version: " + metadata.getVersion());
    }

    @FXML
    void openGithubLink() {
        webBrowser.browse("https://github.com/CIRDLES/Topsoil");
    }

}
