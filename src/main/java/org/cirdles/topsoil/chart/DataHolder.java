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
import javafx.beans.property.ObjectProperty;
import javafx.scene.chart.XYChart.Data;

/**
 * A base class that has the ability to extract value from the extra data of as <code>Data</code> object.
 * @see Data
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public abstract class DataHolder {
    private final Data data;

    public DataHolder(Data data) {
        this.data = data;
    }
    
    protected <T> T getField(String field) {
        return ((ObjectProperty<T>) ((Map) data.getExtraValue()).get(field)).get();
    }
}
