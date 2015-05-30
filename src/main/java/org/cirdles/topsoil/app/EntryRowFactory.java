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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.cirdles.topsoil.dataset.entry.Entry;
import static org.cirdles.topsoil.dataset.field.Fields.ROW;
import static org.cirdles.topsoil.dataset.field.Fields.SELECTED;

/**
 * The row Factory for the TSVTable 
 * Define the Context Menu
 * 
 * @author parizotclement
 */
public class EntryRowFactory implements Callback<TableView<Entry>, TableRow<Entry>> {
        
    @Override
    public TableRow<Entry> call(TableView<Entry> param) {
        TableRow<Entry> row = new TableRow<>();
        row.setContextMenu(new EntryRowContextMenu(row));
        
        row.itemProperty().addListener((observable, oldValue, newValue) -> {
            Optional.ofNullable(newValue).ifPresent(entry -> {
                entry.set(ROW, row);
                
                if (entry.get(SELECTED).orElse(true)) {
                    row.setOpacity(1);
                } else {
                    row.setOpacity(0.35);
                }
            });
        
        });
        
        return row;
    }

}
