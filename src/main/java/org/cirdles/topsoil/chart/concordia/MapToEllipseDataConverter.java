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

import java.util.Map;
import javafx.scene.chart.XYChart;
import org.cirdles.topsoil.chart.DataConverter;

/**
 * <p>Implementation of a <code>DataConverter</code> that generate an <code>ErrorEllipse</code> from a <code>map</code>.</p> 
 * <ul>
 *  <li>The fields used from the <code>map</code> to <code>get</code> the value to make the <code>ErrorEllipse</code> are passed to the constructor.</li>
 *  <li>The <code>map</code> must be passed to the property <code>extraValue</code> of the <code>XYChart.Data</code></li>
 * </ul>
 * @see DataConverter
 * @see ErrorEllipse
 */
public class MapToEllipseDataConverter implements DataConverter<ErrorEllipse>{

    private final Object key_x;
    private final Object key_y;
    private final Object key_sigmax;
    private final Object key_sigmay;
    private final Object key_rho;

    public MapToEllipseDataConverter(Object key_x, Object key_y, Object key_sigmax, Object key_sigmay, Object key_rho) {
        this.key_x = key_x;
        this.key_y = key_y;
        this.key_sigmax = key_sigmax;
        this.key_sigmay = key_sigmay;
        this.key_rho = key_rho;
    }

    @Override
    public ErrorEllipse convert(XYChart.Data data) {
        Map<Object, Double> ellipse_data = (Map<Object, Double>) data.getExtraValue(); 

        double x = ellipse_data.get(key_x);
        double y = ellipse_data.get(key_y);
        double sigmax = ellipse_data.get(key_sigmax);
        double sigmay = ellipse_data.get(key_sigmay);
        double rho = ellipse_data.get(key_rho);

        return new ErrorEllipse(x, y, sigmax, sigmay, rho);
    }

}