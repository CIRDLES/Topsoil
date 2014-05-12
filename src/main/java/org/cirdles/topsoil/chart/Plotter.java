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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public abstract class Plotter<T, S extends StyleAccessor> {
    private final XYChart chart;
    protected final ObjectProperty<S> style;

    public Plotter(XYChart chart, S style_arg) {
        this.chart = chart;
        style = new SimpleObjectProperty<>(style_arg);
    }
    
    public abstract Node plot(T plottable);
    
    protected double mapXToDisplay(double x) {
        return chart.getXAxis().getDisplayPosition(x);
    }

    protected double mapYToDisplay(double y) {
        return chart.getYAxis().getDisplayPosition(y);
    }
}
