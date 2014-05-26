/*
 * Copyright 2014 pfif.
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

import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.cirdles.topsoil.table.Record;
import org.cirdles.topsoil.utils.TSVTableReader;
import org.cirdles.topsoil.utils.TableReader;

/**
 * A table containing data used to generate charts. Implements some shortcut.
 * Since it implements
 * <code>ColumnSelectorDialog.ColumnSelectorDialogListener</code>, it is also
 * responsible of generating charts.
 */
public class TopsoilTable extends TableView<Record> {

    public TopsoilTable() {
        if (Files.exists(Topsoil.LAST_TABLE_PATH)) {
            TableReader tableReader = new TSVTableReader(true);
            try {
                tableReader.read(Topsoil.LAST_TABLE_PATH, this);
            } catch (IOException ex) {
                Logger.getLogger(Topsoil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.setOnKeyPressed((KeyEvent event) -> {
            if (event.isShortcutDown() && event.getCode().equals(KeyCode.V)) {
                Tools.pastFromClipboard(this);
            }
        });

        getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends Record> observable, Record oldValue, Record newValue) -> {
                    if (oldValue != null) {
                        oldValue.setSelected(false);
                    }
                    
                    newValue.setSelected(true);
                });
    }
}
