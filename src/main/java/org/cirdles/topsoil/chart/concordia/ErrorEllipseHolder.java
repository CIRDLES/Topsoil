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
import org.cirdles.topsoil.chart.DataHolder;

/**
 * <p>Centralize the parsing of values used to create an ErrorElispe from a <code>Data</code> object.<p>
 * <p>The extra values of the <code>Data</code> object. must have the following field to be correctly red :</p>
 * <ul>
 *   <li>xValue</li>
 *   <li>yValue</li>
 *   <li>sigmaX</li>
 *   <li>sigmaY</li>
 *   <li>rho</li>
 * </ul>
 * @author John Zeringue
 * @see Data
 */
public class ErrorEllipseHolder extends DataHolder {
    public static String XVALUE = "xValue";
    public static String YVALUE = "yValue";
    public static String SIGMAXVALUE = "sigmaX";
    public static String SIGMAYVALUE = "sigmaY";
    public static String RHOVALUE = "rho";

    public ErrorEllipseHolder(Data<Number, Number> data) {
        super(data);
    }

    public double getX() {
        return this.<Number>getField(XVALUE).doubleValue();
    }

    public double getY() {
        return this.<Number>getField(YVALUE).doubleValue();
    }

    public double getSigmaX() {
        return this.<Number>getField(SIGMAXVALUE).doubleValue();
    }

    public double getSigmaY() {
        return this.<Number>getField(SIGMAYVALUE).doubleValue();
    }

    public double getRho() {
        return this.<Number>getField(RHOVALUE).doubleValue();
    }

}
