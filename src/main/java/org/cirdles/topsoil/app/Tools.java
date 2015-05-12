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

import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Alert;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static javafx.scene.control.ButtonType.YES;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.util.StringConverter;
import static org.cirdles.topsoil.app.Topsoil.LAST_TABLE_PATH;
import org.cirdles.topsoil.app.utils.TSVDatasetReader;
import org.cirdles.topsoil.app.utils.TSVDatasetWriter;
import org.cirdles.topsoil.app.utils.DatasetReader;
import org.cirdles.topsoil.app.utils.DatasetWriter;
import org.cirdles.topsoil.app.utils.YesNoAlert;
import org.cirdles.topsoil.data.Dataset;
import org.cirdles.topsoil.data.Entry;

/**
 * Shortcut tools to be used anywhere in the program.
 */
public class Tools {

    private static final String LOGGER_NAME = Tools.class.getName();

    public static final StringConverter<Number> DYNAMIC_STRING_CONVERTER = new StringConverter<Number>() {

        @Override
        public String toString(Number object) {
            return String.format("%.10f", object.doubleValue()).replaceFirst("[\\.,]?0+$", "");
        }

        @Override
        public Number fromString(String string) {
            return Double.valueOf(string);
        }
    };

    public static final StringConverter<Number> DYNAMIC_NUMBER_CONVERTER_TO_INTEGER = new StringConverter<Number>() {

        @Override
        public String toString(Number object) {
            return String.valueOf(object.intValue());
        }

        @Override
        public Number fromString(String string) {
            return Integer.valueOf(string);
        }
    };

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
        alert.setTitle(Topsoil.APP_NAME);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData() != CANCEL_CLOSE) {
                callback.accept(buttonType == YES);
            }
        });
    }

    public static void pasteFromClipboard(TSVTable dataTable) {
        Tools.yesNoPrompt("Does the pasted data contain headers?", response -> {
            DatasetReader tableReader = new TSVDatasetReader(response);

            try {
                Dataset dataset
                        = tableReader.read(Clipboard.getSystemClipboard().getString());

            } catch (IOException ex) {
                Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
            }

            saveTable(dataTable);
        });
    }

    public static void saveTable(TSVTable dataTable) {
        if (!dataTable.getItems().isEmpty()) {
            DatasetWriter tableWriter = new TSVDatasetWriter(dataTable.getRequiredColumnCount());
            try {
                tableWriter.write(dataTable.getDataset(), LAST_TABLE_PATH);
            } catch (IOException ex) {
                Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            boolean lastTableFileDeleted = LAST_TABLE_PATH.toFile().delete();

            if (!lastTableFileDeleted) {
                Logger.getLogger(LOGGER_NAME).log(Level.FINE,
                        "Last table file was not able to be deleted");
            }
        }
    }

    public static void clearTable(TableView<Entry> dataTable) {
        dataTable.getItems().clear();
        dataTable.getColumns().clear();
    }
}
