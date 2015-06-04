/*
 * Copyright 2014 CIRDLES.
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
package org.cirdles.topsoil.app;

import java.util.function.Consumer;
import javafx.scene.control.Alert;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static javafx.scene.control.ButtonType.YES;
import org.cirdles.topsoil.app.metadata.ApplicationMetadata;
import org.cirdles.topsoil.app.utils.YesNoAlert;

/**
 * Shortcut tools to be used anywhere in the program.
 */
public class Tools {

    private static ApplicationMetadata metadata;

    /**
     * Prompts the user for a yes or no response with a custom message. If the
     * user selects yes or no, the callback function is called with a boolean
     * indicating the result. Otherwise, the user may choose to cancel the
     * action and the dialog will close without any side effects.
     *
     * @param message the message to display to the user inside the dialog box
     * @param callback the function to be called if the action is not canceled
     */
    public static void yesNoPrompt(String message, Consumer<Boolean> callback) {
        Alert alert = new YesNoAlert(message);
        alert.setTitle(metadata.getName());

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData() != CANCEL_CLOSE) {
                callback.accept(buttonType == YES);
            }
        });
    }

}
