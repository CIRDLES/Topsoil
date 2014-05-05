/*
 * Copyright 2014 John.
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

import static java.lang.Math.*;
import java.math.BigDecimal;
import java.util.function.Function;
import javafx.scene.shape.Path;

/**
 * <p>This class is use to get a <code>Path</code> representing a <i>Concordia line</i>.</p>
 * <p>To get the <i>Concordia line</i>, use the method <code>getNode</code></p>
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 * @see Path
 */
public class ConcordiaLine {

    private final static BigDecimal E = new BigDecimal(Math.E);
    private final static BigDecimal LAMBDA_235 = new BigDecimal("0.00000000098485");
    private final static BigDecimal LAMBDA_238 = new BigDecimal("0.000000000155125");

    private final static int PIECES = 30;

    private final double[] xs;
    private final double[] ys;
    
    private final double startT;
    private final double endT;

    public ConcordiaLine(double startT, double stopT) {
        this.startT = startT;
        this.endT = stopT;
        
        double increment = (stopT - startT) / PIECES;
        xs = new double[PIECES + 1];
        ys = new double[PIECES + 1];

        for (int i = 0; i < PIECES + 1; i++) {
            xs[i] = getX(startT + increment * i);
            ys[i] = getY(startT + increment * i);
        }
    }

    public ConcordiaLine(double minX, double maxX, double minY, double maxY) {
        this(findStartT(minX, minY), findStopT(maxX, maxY));
    }

    private static double findStartT(double minX, double minY) {
        double result;

        // T can't be negative
        if (minX <= 0 && minY <= 0) {
            return 0;
        }

        result = max(findTIntercept(minX, ConcordiaLine::getX), findTIntercept(minY, ConcordiaLine::getY));

        return result;
    }

    private static double findStopT(double maxX, double maxY) {
        double result;

        // T can't be negative
        if (maxX <= 0 || maxY <= 0) {
            return 0;
        }

        result = min(findTIntercept(maxX, ConcordiaLine::getX), findTIntercept(maxY, ConcordiaLine::getY));

        return result;
    }
    
    private static double findTIntercept(double value, Function<Double, Double> function) {
        double startT = 0;
        double stopT = 1;
        
        // Find a range containing x.
        while (function.apply(stopT) < value) {
            startT = stopT;
            stopT *= 2;
        }
        
        // Perform a binary search and return the result
        double middleT = -1;
        for (int i = 0; i < 100; i++) {
            middleT = (startT + stopT) / 2;
            if (function.apply(middleT) < value) {
                startT = middleT;
            } else {
                stopT = middleT;
            }
        };
        
        return middleT;
    }

    public double[] getXs() {
        return xs;
    }

    public double[] getYs() {
        return ys;
    }

    public static double getX(double t) {
        return expm1(LAMBDA_235.doubleValue() * t);
    }

    public static double getY(double t) {
        return expm1(LAMBDA_238.doubleValue() * t);
    }
    
    public double getStartT() {
        return startT;
    }
    
    public double getEndT() {
        return endT;
    }
}
