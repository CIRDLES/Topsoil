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
package org.cirdles.topsoil.chart;

import java.util.Map;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 * @param <T>
 */
public class MapTableColumn<T> extends TableColumn<Map, T> {
    private Object key;
    
    public MapTableColumn(Object keyarg) {
        key = keyarg;
        setCellValueFactory(new MapValueFactory<>(key));
    }

    public MapTableColumn(Object key, String text) {
        this(key);
        setText(text);
    }
    
    public Object getKey() {
        return key;
    }
}
