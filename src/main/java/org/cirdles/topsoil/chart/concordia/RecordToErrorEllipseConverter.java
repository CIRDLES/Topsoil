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

import javafx.scene.chart.XYChart.Data;
import org.cirdles.topsoil.chart.DataConverter;
import org.cirdles.topsoil.table.Field;
import org.cirdles.topsoil.table.Record;

/**
 * Implementation of a <code>DataConverter</code> that generates an <code>ErrorEllipse</code> from a <code>Data</code>
 * with a <code>Record</code> attached. The fields passed into the constructor are used to map values from the
 * <code>Record</code> into new <code>ErrorEllipse</code>s. The <code>Record</code>s must be set as the extra values of
 * the <code>Data</code> objects.
 *
 * @see DataConverter
 * @see ErrorEllipse
 * @see Record
 * @see Field
 * @see Data
 */
public class RecordToErrorEllipseConverter implements DataConverter<ErrorEllipse> {

    private final Field<Number> xField;
    private final Field<Number> sigmaXField;
    private final Field<Number> yField;
    private final Field<Number> sigmaYField;
    private final Field<Number> rhoField;

    /**
     * Creates a new converter that instantiates new <code>ErrorEllipse</code>s from <code>Record</code>s. The
     * constructor parameters specify the fields that should correspond to x, sigmaX, y, sigmaY, and rho in the
     * converted <code>ErrorEllipse</code>s.
     *
     * @param xField the field corresponding to the x-value for each record.
     * @param sigmaXField the field corresponding to the sigmaX-value for each record.
     * @param yField the field corresponding to the y-value for each record.
     * @param sigmaYField the field corresponding to the sigmaY-value for each record.
     * @param rhoField the field corresponding to the rho-value for each record.
     */
    public RecordToErrorEllipseConverter(Field<Number> xField, Field<Number> sigmaXField,
                                         Field<Number> yField, Field<Number> sigmaYField,
                                         Field<Number> rhoField) {
        this.xField = xField;
        this.sigmaXField = sigmaXField;
        this.yField = yField;
        this.sigmaYField = sigmaYField;
        this.rhoField = rhoField;
    }

    /**
     * Converts a <code>Data</code> object into a new ErrorEllipse. The conversion requires that the <code>Data</code>'s
     * extra value is a <code>Record</code> containing values for the fields given in the constructor. The
     * <code>Data</code>'s x and y values are ignored.
     *
     * @param data a <code>Data</code> object with a <code>Record</code> as its extra value
     * @return a new <code>ErrorEllipse</code> using the data attached to the argument
     */
    @Override
    public ErrorEllipse convert(Data data) {
        Record record = (Record) data.getExtraValue();

        return new ErrorEllipse() {

            @Override
            public double getX() {
                return record.getValue(xField).doubleValue();
            }

            @Override
            public double getY() {
                return record.getValue(yField).doubleValue();
            }

            @Override
            public double getSigmaX() {
                return record.getValue(sigmaXField).doubleValue();
            }

            @Override
            public double getSigmaY() {
                return record.getValue(sigmaYField).doubleValue();
            }

            @Override
            public double getRho() {
                return record.getValue(rhoField).doubleValue();
            }

            @Override
            public boolean getSelected() {
                return record.getSelected();
            }
        };
    }
}
