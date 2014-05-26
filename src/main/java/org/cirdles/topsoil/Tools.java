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
package org.cirdles.topsoil;

import java.util.function.Consumer;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.Region;
import static org.cirdles.topsoil.Topsoil.LAST_TABLE_PATH;
import org.cirdles.topsoil.table.Record;
import org.cirdles.topsoil.utils.TSVTableReader;
import org.cirdles.topsoil.utils.TSVTableWriter;
import org.cirdles.topsoil.utils.TableReader;
import org.cirdles.topsoil.utils.TableWriter;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

/**
 * Shortcut tools to be used anywhere in the program.
 */
public class Tools {

    /**
     * Prompts the user for a yes or no response with a custom message. If the user selects yes or no, the callback
     * function is called with a boolean indicating the result. Otherwise, the user may choose to cancel the action and
     * the dialog will close without any side effects.
     *
     * @param message the message to display to the user inside the dialog box
     * @param callback the function to be called if the action is not canceled
     */
    public static void yesNoPrompt(String message, Consumer<Boolean> callback) {
        Action response = Dialogs.create()
                .title(Topsoil.APP_NAME)
                .message(message)
                .showConfirm();

        if (response != Dialog.Actions.CANCEL) {
            callback.accept(response == Dialog.Actions.YES);
        }
    }

    public static Label label_minsize(String textlabel) {
        LabelUsePrefSize label = new LabelUsePrefSize(textlabel);
        label.setMinWidth(Region.USE_PREF_SIZE);
        return label;
    }
    
    public static void pasteFromClipboard(TableView<Record> dataTable){
        Tools.yesNoPrompt("Does the pasted data contain headers?", response -> {
            TableReader tableReader = new TSVTableReader(response);
            tableReader.read(Clipboard.getSystemClipboard().getString(), dataTable);

            saveTable(dataTable);
        });
    }
    
    public static void saveTable(TableView<Record> dataTable){
        if(!dataTable.getItems().isEmpty()){
            TableWriter<Record> tableWriter = new TSVTableWriter(true);
            tableWriter.write(dataTable, LAST_TABLE_PATH);
        } else {
           LAST_TABLE_PATH.toFile().delete();
        }
    }
    
    public static void clearTable(TableView<Record> dataTable){
        dataTable.getItems().clear();
        dataTable.getColumns().clear();
    }
}
