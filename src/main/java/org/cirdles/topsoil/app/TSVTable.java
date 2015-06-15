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
package org.cirdles.topsoil.app;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.cirdles.topsoil.app.table.EntryTableColumn;
import org.cirdles.topsoil.app.dataset.reader.TSVDatasetReader;
import org.cirdles.topsoil.app.dataset.reader.DatasetReader;
import org.cirdles.topsoil.dataset.Dataset;
import org.cirdles.topsoil.dataset.DatasetResource;
import org.cirdles.topsoil.dataset.entry.Entry;
import org.cirdles.topsoil.dataset.field.NumberField;
import org.cirdles.topsoil.dataset.field.TextField;

/**
 * A table containing data used to generate charts. Implements some shortcuts.
 */
public class TSVTable extends TableView<Entry> {

    private Optional<DatasetResource> datasetResource = Optional.empty();
    private Optional<Dataset> unsavedDataset = Optional.empty(); //Due to pasteFromClipboard

    public TSVTable() {
        this.setEditable(true);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        this.setOnKeyPressed((KeyEvent event) -> {
            if (event.isShortcutDown() && event.getCode().equals(KeyCode.V)) {
                pasteFromClipboard();
            }
        });

        //Context Menu
        setRowFactory(new EntryRowFactory());
    }

    public void setDatasetResource(DatasetResource datasetResource) {
        clear();

        // create columns for fields
        datasetResource.getDataset().getFields().stream().forEach(field -> {
            if (field instanceof NumberField) {
                NumberField numberField = (NumberField) field;
                getColumns().add(new EntryTableColumn<>(numberField));
            } else if (field instanceof TextField) {
                TextField textField = (TextField) field;
                getColumns().add(new EntryTableColumn<>(textField));
            }
        });

        // set data
        setItems(FXCollections.observableList(datasetResource.getDataset().getEntries()));

        this.datasetResource = Optional.of(datasetResource);
        unsavedDataset = Optional.empty();
    }

    //Method used by tests and by PasteFromClipboard method for unsaved data
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

        unsavedDataset = Optional.ofNullable(dataset);
    }

    public Optional<DatasetResource> getDatasetResource() {
        return datasetResource;
    }

    public Optional<Dataset> getDataset() {
        return unsavedDataset;
    }

    /**
     * Pastes the contents of the clipboard into this table.
     */
    public void pasteFromClipboard() {
        Tools.yesNoPrompt("Does the pasted data contain headers?", response -> {
            DatasetReader tableReader = new TSVDatasetReader(response);

            try {
                Dataset dataset
                        = tableReader.read(Clipboard.getSystemClipboard().getString());
                setDataset(dataset);
            } catch (IOException ex) {
                Logger.getLogger(TSVTable.class.getName()).log(Level.SEVERE, null, ex);
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
