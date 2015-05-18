/*
 * Copyright 2014 CIRDLEs.
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

import java.beans.EventHandler;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import org.cirdles.topsoil.dataset.entry.Entry;
import org.cirdles.topsoil.dataset.field.Field;

/**
 *
 * @author CIRDLES
 * @param <T>
 */
public class EntryTableColumn<T> extends TableColumn<Entry, T> {
    
    private final Field<T> field;

    public EntryTableColumn(Field<T> field) {
        setText(field.getName());

        setCellValueFactory((CellDataFeatures<Entry, T> param) -> new ObservableValueBase<T>() {
            
            @Override
            public T getValue() {
                return param.getValue().get(field).get();
            }
        });
        setCellFactory(TextFieldTableCell.forTableColumn(field.getStringConverter()));
        
        setOnEditCommit(new CellEditEventHandler<>(field));

        this.field = field;
    }
    
    public Field<T> getField() {
        return field;
    }
    
}
