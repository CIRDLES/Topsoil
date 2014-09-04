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
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.cirdles.topsoil.table.Record;
import org.cirdles.topsoil.utils.TSVTableReader;
import org.cirdles.topsoil.utils.TSVTableWriter;
import org.cirdles.topsoil.utils.TableReader;
import org.cirdles.topsoil.utils.TableWriter;

/**
 * A table containing data used to generate charts. Implements some shortcuts.
 */
public class TSVTable extends TableView<Record> {

    private Path savePath;
    
    // JFB
    // zroe means ignore, positive means fill to this count if data not present
    private int requiredColumnCount;

    public TSVTable() {
        this.requiredColumnCount = 0;
        
        this.setOnKeyPressed((KeyEvent event) -> {
            if (event.isShortcutDown() && event.getCode().equals(KeyCode.V)) {
                pasteFromClipboard();
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

    public TSVTable(Path savePath) {
        this();

        this.savePath = savePath;
        load();
    }

    /**
     * Pastes the contents of the clipboard into this table.
     */
    public void pasteFromClipboard() {
        Tools.yesNoPrompt("Does the pasted data contain headers?", response -> {
            TableReader tableReader = new TSVTableReader(response);
            tableReader.read(Clipboard.getSystemClipboard().getString(), this);

            if (savePath != null) {
                saveToPath(savePath);
            }
        });
    }

    /**
     * Clears the items and columns in the table.
     */
    public void clear() {
        getItems().clear();
        getColumns().clear();
    }
    
    public void load() {
        if (savePath != null) {
            loadFromPath(savePath);
        }
    }

    public void loadFromPath(Path loadPath) {
        if (Files.exists(loadPath)) {
            TableReader tableReader = new TSVTableReader(true);
            try {
                tableReader.read(loadPath, this);
            } catch (IOException ex) {
                Logger.getLogger(Topsoil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void save() {
        if (savePath != null) {
            saveToPath(savePath);
        }
    }

    /**
     * Saves the table at the given path in TSV format.
     *
     * @param savePath
     */
    public void saveToPath(Path savePath) {
        if (savePath == null) {
            throw new IllegalArgumentException("Cannot save to null path.");
        }

        TableWriter<Record> tableWriter = new TSVTableWriter(true, requiredColumnCount);
        tableWriter.write(this, savePath);
    }
    
    public void saveToPathWithRequiredColumnCount(Path savePath){
        
    }
    
    public boolean hasRequiredColumnCount(){
        return getColumns().size() >= requiredColumnCount;
    }

    /**
     * Gets the saveToPath path.
     *
     * @return
     */
    public Path getSavePath() {
        return savePath;
    }

    /**
     * Sets the saveToPath path.
     *
     * @param savePath
     */
    public void setSavePath(Path savePath) {
        this.savePath = savePath;
    }

    /**
     * @param requiredColumnCount the requiredColumnCount to set
     */
    public void setRequiredColumnCount(int requiredColumnCount) {
        this.requiredColumnCount = requiredColumnCount;
    }

    /**
     * @return the requiredColumnCount
     */
    public int getRequiredColumnCount() {
        return requiredColumnCount;
    }
}
