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

import Jama.Matrix;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * A class which represents an error ellipse. While this class is responsible for generating Bezier curve control points
 * and calculating its minimums and maximums, it is not to know anything about the framework using it, nor is it
 * intended to translate its properties into any other coordinate space.
 * 
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public abstract class ErrorEllipse {
    private static final double K = 4. / 3 * (sqrt(2) - 1);
    private static final Matrix CONTROL_POINTS_MATRIX = new Matrix(new double[][]{
        {1, 0},
        {1, K},
        {K, 1},
        {0, 1},
        {-K, 1},
        {-1, K},
        {-1, 0},
        {-1, -K},
        {-K, -1},
        {0, -1},
        {K, -1},
        {1, -K},
        {1, 0}
    });
    
    private Matrix controlPoints;
    
    public abstract double getX();
    public abstract double getSigmaX();
    public abstract double getY();
    public abstract double getSigmaY();
    public abstract double getRho();
    
    public double getConfidenceLevel() {
        return 1;
    }
    
    public boolean getSelected() {
        return false;
    }
    
    public double getMinX() {
        return min(getControlPoints().transpose().getArray()[0]);
    }
    
    public double getMaxX() {
        return max(getControlPoints().transpose().getArray()[0]);
    }
    
    public double getMinY() {
        return min(getControlPoints().transpose().getArray()[1]);
    }
    
    public double getMaxY() {
        return max(getControlPoints().transpose().getArray()[1]);
    }
    
    /**
     * Gets this error ellipse's Bezier control points.
     * @return the control points
     */
    public Matrix getControlPoints() {
        // lazy computation
        if (controlPoints == null) {
            controlPoints = calculateControlPoints();
        }
        
        return controlPoints;
    }

    private Matrix calculateControlPoints() {
        double covarianceX_Y = getSigmaX() * getSigmaY() * getRho();

        Matrix covMat = new Matrix(new double[][]{
            {pow(getSigmaX(), 2), covarianceX_Y},
            {covarianceX_Y, pow(getSigmaY(), 2)}
        });

        Matrix r = covMat.chol().getL().transpose();

        // [[1],              [[x, y],
        //  [1],               [x, y],
        //  [1],               [x, y],
        //  [1],               [x, y],
        //  [1],               [x, y],
        //  [1],               [x, y],
        //  [1], * [[x, y]] =  [x, y],
        //  [1],               [x, y],
        //  [1],               [x, y],
        //  [1],               [x, y],
        //  [1],               [x, y],
        //  [1],               [x, y],
        //  [1]]               [x, y]]
        Matrix xyMatrix = new Matrix(13, 1, 1).times(new Matrix(new double[]{getX(), getY()}, 1));
        
        return CONTROL_POINTS_MATRIX.times(getConfidenceLevel()).times(r).plus(xyMatrix);
    }
    
    private double min(double[] values) {
        double min = Double.MAX_VALUE;
        
        for (double value : values) {
            min = Math.min(min, value);
        }
        
        return min;
    }
    
    private double max(double[] values) {
        double max = Double.MIN_VALUE;
        
        for (double value : values) {
            max = Math.max(max, value);
        }
        
        return max;
    }
}
