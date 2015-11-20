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
import org.cirdles.topsoil.app.util.Alerter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Emily
 */
public class DesktopWebBrowser implements WebBrowser {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(DesktopWebBrowser.class);

    private final Desktop desktop;
    private final Alerter alerter;

    @Inject
    public DesktopWebBrowser(@Nullable Desktop desktop, Alerter alerter) {
        this.desktop = desktop;
        this.alerter = alerter;
    }

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
        if (desktop != null) {
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                browse(uri);
            } else {
                alerter.alert("Browsing not supported");
            }
        } else {
            alerter.alert("Desktop not supported");
        }
    }

    void browse(URI uri) {
        try {
            desktop.browse(uri);
        } catch (IOException ex) {
            LOGGER.error(null, ex);
            alerter.alert("Browser could not be opened.");
        }
    }

}
