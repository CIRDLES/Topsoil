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
package org.cirdles.topsoil.app.util.dialog;

import javafx.scene.control.Alert;

/**
 * A simple interface for custom {@link Alert} classes.
 *
 * @author Emily Coleman
 * @see ErrorAlerter
 */
public interface Alerter {

    /**
     * Displays an {@code Alert} with the specified message.
     *
     * @param message a {@code String} message
     */
    void alert(String message);

}
