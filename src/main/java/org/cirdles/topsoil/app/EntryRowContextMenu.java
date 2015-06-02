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

import static java.lang.Double.NaN;
import java.util.Collection;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.WindowEvent;
import org.cirdles.topsoil.app.table.EntryTableColumn;
import org.cirdles.topsoil.dataset.entry.Entry;
import org.cirdles.topsoil.dataset.entry.SimpleEntry;
import static org.cirdles.topsoil.dataset.field.Fields.ROW;
import static org.cirdles.topsoil.dataset.field.Fields.SELECTED;

/**
 *
 * @author parizotclement
 */
public class EntryRowContextMenu extends ContextMenu {

    private final TableRow<Entry> row;

    private MenuItem toggleSelectedMenuItem;
    private MenuItem addRowMenuItem;
    private MenuItem removeRowMenuItem;

    public EntryRowContextMenu(TableRow<Entry> row) {
        this.row = row;

        initializeThis();
    }

    TableView<Entry> getTable() {
        return row.getTableView();
    }

    void selectEntries() {
        getTable().getSelectionModel().getSelectedItems().stream()
                .forEach(entry -> {
                    entry.set(SELECTED, true);

                    entry.get(ROW).ifPresent(entryRow -> entryRow.setOpacity(1));
                });
    }

    void deselectEntries() {
        getTable().getSelectionModel().getSelectedItems().stream()
                .forEach(entry -> {
                   entry.set(SELECTED, false);

                   entry.get(ROW).ifPresent(entryRow -> entryRow.setOpacity(0.35));
                });
    }

    MenuItem toggleSelectedMenuItem() {
        toggleSelectedMenuItem = new MenuItem();

        toggleSelectedMenuItem.setOnAction(event -> {
            if (row.getItem().get(SELECTED).orElse(true)) {
                deselectEntries();
            } else {
                selectEntries();
            }

            getTable().getSelectionModel().clearSelection();
        });

        return toggleSelectedMenuItem;
    }

    MenuItem addRowMenuItem() {
        addRowMenuItem = new MenuItem("Add row");

        addRowMenuItem.setOnAction(event -> {
            int nextIndex = row.getIndex() + 1;
            Entry entry = new SimpleEntry();

            getTable().getColumns().stream().forEach(tableColumn -> {
                EntryTableColumn column = (EntryTableColumn) tableColumn;
                entry.set(column.getField(), NaN);
            });

            getTable().getItems().add(nextIndex, entry);
            getTable().getSelectionModel().clearAndSelect(nextIndex);
        });

        return addRowMenuItem;
    }

    MenuItem removeRowMenuItem() {
        removeRowMenuItem  = new MenuItem();

        removeRowMenuItem .setOnAction(event -> {
            Collection<Entry> selectedItems = getTable().getSelectionModel().getSelectedItems();

            getTable().getItems().removeAll(selectedItems);
            getTable().getSelectionModel().clearSelection();
        });

        return removeRowMenuItem ;
    }

    String toggleSelectedMenuItemText() {
        return row.getItem().get(SELECTED).orElse(true) ? "Deselect" : "Select";
    }

    void updateToggleSelectedMenuItem() {
        toggleSelectedMenuItem.setText(toggleSelectedMenuItemText());
    }

    boolean multipleItemsSelected() {
        return getTable().getSelectionModel().getSelectedItems().size() > 1;
    }

    void updateAddRowMenuItem() {
        addRowMenuItem.setDisable(multipleItemsSelected());
    }

    String removeRowMenuItemText() {
        return multipleItemsSelected() ? "Remove rows" : "Remove row";
    }

    void updateRemoveRowMenuItem() {
        removeRowMenuItem.setText(removeRowMenuItemText());
    }

    void updateMenuItems(WindowEvent event) {
        updateToggleSelectedMenuItem();
        updateAddRowMenuItem();
        updateRemoveRowMenuItem();
    }

    private void initializeThis() {
        getItems().addAll(
                toggleSelectedMenuItem(),
                addRowMenuItem(),
                removeRowMenuItem()
        );

        setOnShowing(this::updateMenuItems);
    }
}
