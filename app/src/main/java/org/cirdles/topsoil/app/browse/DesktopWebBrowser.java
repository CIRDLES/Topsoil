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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Emily
 */
public class DesktopWebBrowser implements WebBrowser {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(DesktopWebBrowser.class);

    @Override
    public void browse(String uriString) {
        try {
            URI uri = new URI(uriString);
            checkDesktopAndBrowse(uri);
        } catch (URISyntaxException ex) {
            LOGGER.error(null, ex);
            throw new RuntimeException(ex);
        }
    }

    void checkDesktopAndBrowse(URI uri) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();

            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                browse(desktop, uri);
            } else {
                errorAlert("Browsing not supported");
            }
        } else {
            errorAlert("Desktop not supported");
        }
    }

    void browse(Desktop desktop, URI uri) {
        try {
            desktop.browse(uri);
        } catch (IOException ex) {
            LOGGER.error(null, ex);
            errorAlert("Browser could not be opened.");
        }
    }

    void errorAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(alertMessage);
        alert.showAndWait();
    }

}
