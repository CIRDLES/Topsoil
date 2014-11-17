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
package org.cirdles.topsoil.app.chart;

import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class DataNumberColumn extends TableColumn<Map, ObjectProperty<Number>> {

    public DataNumberColumn(Object field) {
        setCellValueFactory(new MapValueFactory<>(field));
//        setCellFactory(TextFieldTableCell.<Map, ObjectProperty<Number>>forTableColumn(
//                new StringConverter<ObjectProperty<Number>>() {
//                    @Override
//                    public String toString(ObjectProperty<Number> t) {
//                        return t.toString();
//                    }
//
//                    @Override
//                    public ObjectProperty<Number> fromString(String string) {
//                        return new SimpleObjectProperty<>((Number) Double.valueOf(string));
//                    }
//                }));
        setOnEditCommit(cellEditEvent -> {
            ((Map) cellEditEvent.getTableView().getItems()
                    .get(cellEditEvent.getTablePosition().getRow()))
                    .put(field, cellEditEvent.getNewValue());
        });
    }
}
