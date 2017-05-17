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
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.NO;
import static javafx.scene.control.ButtonType.YES;

/**
 * A custom {@link Alert} for displaying a Yes/No option.
 *
 * @author John Zeringue
 * @see Alert
 */
public class YesNoAlert extends Alert {

    //***********************
    // Methods
    //***********************

    /**
     * Constructs a {@code YesNoAlert} with the specified message.
     *
     * @param contentText   a {@code String} message
     */
    public YesNoAlert(String contentText) {
        super(CONFIRMATION);

        setContentText(contentText);
        getButtonTypes().setAll(YES, NO, CANCEL);
    }

}
