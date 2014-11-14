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

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.scene.chart.ValueAxis;

/**
 * A TickGenerator that is bound to a given ValueAxis.
 * <p>
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class AxisTickGenerator extends TickGenerator {

    private static final List<Number> EMPTY_LIST = new ArrayList<>(0);
    private final ValueAxis axis;

    public AxisTickGenerator(ValueAxis axis) {
        super();
        this.axis = axis;
        
        setup();
    }

    public AxisTickGenerator(ValueAxis axis, double anchorTick, double tickUnit) {
        super(anchorTick, tickUnit);
        this.axis = axis;
        
        setup();
    }

    private void setup() {
        minorTickCountProperty().bindBidirectional(axis.minorTickCountProperty());
        
        anchorTickProperty()
                .addListener((ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) -> {
                    if (!isAutoTicking()) {
                        axis.invalidateRange(EMPTY_LIST);
                        axis.requestAxisLayout();
                    }
                });

        autoTickingProperty()
                .addListener((ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) -> {
                    axis.invalidateRange(EMPTY_LIST);
                    axis.requestAxisLayout();
                });

        tickUnitProperty()
                .addListener((ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) -> {
                    if (!isAutoTicking()) {
                        axis.invalidateRange(EMPTY_LIST);
                        axis.requestAxisLayout();
                    }
                });
    }
}
