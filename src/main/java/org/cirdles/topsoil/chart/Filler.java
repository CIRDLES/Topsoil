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

import javafx.scene.Node;
import javafx.scene.chart.XYChart;

/**
 * Works much like plotter, but instead should return a node to provide the fill for the argument. This allows layouts
 * like putting all outlines in front of all fills, because it decouples the outline from the fill.
 * 
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 * @param <T>
 */
public abstract class Filler<T> {
    private final XYChart chart;

    public Filler(XYChart chart) {
        this.chart = chart;
    }
    
    public abstract Node fill(T fillable);
    
    protected double mapXToDisplay(double x) {
        return chart.getXAxis().getDisplayPosition(x);
    }

    protected double mapYToDisplay(double y) {
        return chart.getYAxis().getDisplayPosition(y);
    }
}
