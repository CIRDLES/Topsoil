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
package org.cirdles;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;

/**
 * Utility Class
 * Allow to select tables, rows and cells for the TestFX tests
 * 
 * @author parizotclement
 * @param <T>
 */
public final class TableSelector<T> {

    private final TableView<T> table;

    public TableSelector(TableView<T> table) {
        this.table = table;
    }

    /**
     * @param row Index of the row
     * @return the corresponding row
     */
    public TableRow<T> row(int row) {
        List<Node> current = table.getChildrenUnmodifiable();
        while (current.size() == 1) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        current = ((Parent) current.get(1)).getChildrenUnmodifiable();
        while (!(current.get(0) instanceof TableRow)) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        Node node = current.get(row);
        if (node instanceof TableRow) {
            return (TableRow<T>) node;
        } else {
            throw new RuntimeException("Expected Group with only TableRows as children");
        }
    }

    /**
     * @param row Index of the row
     * @param column Index of the column
     * @return the corresponding cell
     */
    public TableCell<T, ?> cell(int row, int column) {
        List<Node> current = row(row).getChildrenUnmodifiable();
        while (current.size() == 1 && !(current.get(0) instanceof TableCell)) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        Node node = current.get(column);
        if (node instanceof TableCell) {
            return (TableCell<T, ?>) node;
        } else {
            throw new RuntimeException("Expected TableRowSkin with only TableCells as children");
        }
    }
}
