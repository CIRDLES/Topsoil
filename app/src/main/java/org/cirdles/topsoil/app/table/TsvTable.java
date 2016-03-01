/*
 * Copyright 2016 CIRDLES.
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
package org.cirdles.topsoil.app.table;

import javafx.collections.FXCollections;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import org.cirdles.topsoil.app.Tools;
import org.cirdles.topsoil.app.dataset.reader.RawDataReader;
import org.cirdles.topsoil.app.dataset.reader.TsvRawDataReader;
import org.cirdles.topsoil.app.dataset.Dataset;
import org.cirdles.topsoil.app.dataset.RawData;
import org.cirdles.topsoil.app.dataset.SimpleDataset;
import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.dataset.field.NumberField;
import org.cirdles.topsoil.app.dataset.field.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A table containing data used to generate plots. Implements some shortcuts.
 */
public class TsvTable extends TableView<Entry> {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(TsvTable.class);

    private Dataset dataset;

    public TsvTable() {
        setEditable(true);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setOnKeyPressed(event -> {
            if (event.isShortcutDown() && event.getCode().equals(KeyCode.V)) {
                pasteFromClipboard();
            }
        });

        setSortPolicy(new TsvTableSortPolicy());

        //Context Menu
        setRowFactory(new EntryRowFactory());
    }

    public TsvTable(Dataset dataset) {
        this();
        setDataset(dataset);
    }

    public void setDataset(Dataset dataset) {
        clear();

        // create columns for fields
        dataset.getFields().stream().forEach(field -> {
            if (field instanceof NumberField) {
                NumberField numberField = (NumberField) field;
                getColumns().add(new EntryTableColumn<>(numberField));
            } else if (field instanceof TextField) {
                TextField textField = (TextField) field;
                getColumns().add(new EntryTableColumn<>(textField));
            }
        });

        // set data
        setItems(FXCollections.observableList(dataset.getEntries()));

        this.dataset = dataset;
    }

    public Dataset getDataset() {
        return dataset;
    }

    /**
     * Pastes the contents of the clipboard into this table.
     */
    public void pasteFromClipboard() {
        Tools.yesNoPrompt("Does the pasted data contain headers?", response -> {
            RawDataReader tableReader = new TsvRawDataReader(response);

            try {
                RawData rawData = tableReader.read(
                        Clipboard.getSystemClipboard().getString());

                Dataset dataset = new SimpleDataset(
                        "Untitled dataset",
                        rawData);

                setDataset(dataset);
            } catch (IOException ex) {
                LOGGER.error(null, ex);
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

}
