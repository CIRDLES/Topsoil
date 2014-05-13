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
package org.cirdles.topsoil.chart.concordia;

import javafx.scene.chart.XYChart;
import org.cirdles.topsoil.chart.DataConverter;
import org.cirdles.topsoil.table.Field;
import org.cirdles.topsoil.table.Record;

/**
 * <p>
 * Implementation of a <code>DataConverter</code> that generate an
 * <code>ErrorEllipse</code> from a <code>map</code>.</p>
 * <ul>
 * <li>The fields used from the <code>map</code> to <code>get</code> the value
 * to make the <code>ErrorEllipse</code> are passed to the constructor.</li>
 * <li>The <code>map</code> must be passed to the property
 * <code>extraValue</code> of the <code>XYChart.Data</code></li>
 * </ul>
 *
 * @see DataConverter
 * @see ErrorEllipse
 */
public class RecordToErrorEllipseConverter implements DataConverter<ErrorEllipse> {

    private final Field<Number> key_x;
    private final Field<Number> key_y;
    private final Field<Number> key_sigmax;
    private final Field<Number> key_sigmay;
    private final Field<Number> key_rho;

    public RecordToErrorEllipseConverter(Field<Number> key_x, Field<Number> key_y,
            Field<Number> key_sigmax, Field<Number> key_sigmay,
            Field<Number> key_rho) {
        this.key_x = key_x;
        this.key_y = key_y;
        this.key_sigmax = key_sigmax;
        this.key_sigmay = key_sigmay;
        this.key_rho = key_rho;
    }

    @Override
    public ErrorEllipse convert(XYChart.Data data) {
        Record ellipse_data = (Record) data.getExtraValue();

        double x = ellipse_data.getValue(key_x).doubleValue();
        double y = ellipse_data.getValue(key_y).doubleValue();
        double sigmax = ellipse_data.getValue(key_sigmax).doubleValue();
        double sigmay = ellipse_data.getValue(key_sigmay).doubleValue();
        double rho = ellipse_data.getValue(key_rho).doubleValue();

        return new ErrorEllipse() {

            @Override
            public double getX() {
                return ellipse_data.getValue(key_x).doubleValue();
            }

            @Override
            public double getY() {
                return ellipse_data.getValue(key_y).doubleValue();
            }

            @Override
            public double getSigmaX() {
                return ellipse_data.getValue(key_sigmax).doubleValue();
            }

            @Override
            public double getSigmaY() {
                return ellipse_data.getValue(key_sigmay).doubleValue();
            }

            @Override
            public double getRho() {
                return ellipse_data.getValue(key_rho).doubleValue();
            }

            @Override
            public boolean getSelected() {
                return ellipse_data.getSelected();
            }
        };
    }

}
