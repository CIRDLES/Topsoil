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
import javax.annotation.Nullable;
import javax.inject.Inject;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for opening pages in the system default browser.
 *
 * @author Emily Coleman
 * @see WebBrowser
 */
public class DesktopWebBrowser implements WebBrowser {

    //***********************
    // Attributes
    //***********************

    /**
     * {@code Logger} for this class.
     */
    private static final Logger LOGGER
            = LoggerFactory.getLogger(DesktopWebBrowser.class);

    /**
     * System desktop.
     */
    private final Desktop desktop;

    //***********************
    // Constructors
    //***********************

    @Inject
    public DesktopWebBrowser(@Nullable Desktop desktop) {
        this.desktop = desktop;
    }

    //***********************
    // Methods
    //***********************

    /** {@inheritDoc}
     */
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

    /**
     * Checks whether browsin is supported.g
     *
     * @param uri   destination URI
     */
    private void checkDesktopAndBrowse(URI uri) {
        if (desktop != null) {
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                browse(uri);
            } else {
                TopsoilNotification.showNotification(
                        TopsoilNotification.NotificationType.ERROR,
                        "Unsupported",
                        "Browsing not supported."
                );
            }
        } else {
            TopsoilNotification.showNotification(
                    TopsoilNotification.NotificationType.ERROR,
                    "Unsupported",
                    "Browsing not supported."
            );
        }
    }

    /**
     * Attempts to open a link in the desktop's default browser
     *
     * @param uri destination URI
     */
    private void browse(URI uri) {
        try {
            desktop.browse(uri);
        } catch (IOException ex) {
            LOGGER.error(null, ex);
            TopsoilNotification.showNotification(
                    TopsoilNotification.NotificationType.ERROR,
                    "Error",
                    "Unable to open browser."
            );
        }
    }

}
