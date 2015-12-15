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

    private static final String JAVA_VERSION = "Java version: " + System.getProperty("java.version");
    private static final String OPERATING_SYSTEM = "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version");

    @FXML
    private Label javaVersionLabel;

    @FXML
    private Label operatingSystemLabel;

    @FXML
    private Label topsoilVersionLabel;

    private ApplicationMetadata metadata;
    private WebBrowser webBrowser;

    @Inject
    public AboutDialogView(
            ApplicationMetadata metadata,
            WebBrowser webBrowser) {
        super(self -> {
            self.metadata = metadata;
            self.webBrowser = webBrowser;
        });
    }

    @FXML
    private void initialize() {
        javaVersionLabel.setText(JAVA_VERSION);
        operatingSystemLabel.setText(OPERATING_SYSTEM);
        topsoilVersionLabel.setText("Product version: " + metadata.getVersion());
    }

    @FXML
    void openGithubLink() {
        webBrowser.browse("https://github.com/CIRDLES/Topsoil");
    }

}
