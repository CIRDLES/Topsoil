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
package org.cirdles.topsoil.app;

import java.util.Optional;
import java.util.function.Consumer;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import org.cirdles.topsoil.dataset.DatasetResource;

/**
 *
 * @author parizotclement
 */
public class CloseTabEventHandler implements EventHandler<Event> {

    private final Consumer<Boolean> saveDataTable;

    public CloseTabEventHandler(Consumer<Boolean> callback) {
        this.saveDataTable = callback;
    }

    @Override
    public void handle(Event event) {
        Tab tab = (Tab) event.getTarget();

        if (tab.getContent() instanceof TSVTable) {

            tab.getTabPane().getSelectionModel().select(tab);

            TSVTable table = (TSVTable) tab.getContent();
            Optional<DatasetResource> datasetResource = table.getDatasetResource();

            if (datasetResource.isPresent()) {

                if (datasetResource.get().isOpen()) {
                    datasetResource.get().close();
                } else { //the dataset has been deleted in the meanwhile
                    handleNotSaved(event);
                }

            } else { //the dataset hasn't been saved yet
                handleNotSaved(event);
            }
        }
    }

    private void handleNotSaved(Event event) {
        confirmationDialog().ifPresent((ButtonType choice) -> {
            switch (choice.getText()) {
                case "Save":
                    saveDataTable.accept(true);
                    break;
                case "Cancel":
                    event.consume();
                    break;
            }
        });
    }

    private Optional<ButtonType> confirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("You have unsaved changes. Are you sure you want to close this table ?");
        alert.getButtonTypes().clear(); //Remove "Ok" button
        alert.getButtonTypes().addAll(new ButtonType("Save"), new ButtonType("Discard"), ButtonType.CANCEL);

        return alert.showAndWait();
    }
}
