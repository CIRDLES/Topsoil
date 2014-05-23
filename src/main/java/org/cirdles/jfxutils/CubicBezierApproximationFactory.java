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
package org.cirdles.jfxutils;

import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import org.cirdles.math.ParametricCurve;

/**
 *
 * @author zeringuej
 */
public class CubicBezierApproximationFactory implements CurveApproximationFactory {

    private final int numberOfPieces;

    public CubicBezierApproximationFactory() {
        this(1);
    }

    public CubicBezierApproximationFactory(int numberOfPieces) {
        if (numberOfPieces < 1) {
            throw new IllegalArgumentException("There must be at least one piece.");
        }

        this.numberOfPieces = numberOfPieces;
    }

    @Override
    public Path approximate(ParametricCurve curve, double minT, double maxT) {
        Path approximation = new Path(new MoveTo(curve.x(minT), curve.y(minT)));

        double deltaT = (maxT - minT) / numberOfPieces;
        for (int i = 0; i < numberOfPieces; i++) {
            approximation.getElements().add(new CubicBezierSegment(curve, minT + deltaT * i, minT + deltaT * (i + 1)));
        }

        return approximation;
    }

    private static class CubicBezierSegment extends CubicCurveTo {

        public CubicBezierSegment(ParametricCurve curve, double minT, double maxT) {
            
            double segmentLength = Math.sqrt(Math.pow(curve.x(maxT) - curve.x(minT), 2)
                    + Math.pow(curve.y(maxT) - curve.y(minT), 2));
            double angle1 = Math.atan(curve.dy_dx(minT));
            double angle2 = Math.atan(curve.dy_dx(maxT));
            
            boolean tIncreasesWithX = curve.x(minT) <= curve.x(maxT);
            
            if (tIncreasesWithX) {
                angle2 += Math.PI;
            } else {
                angle1 += Math.PI;
            }

            setControlX1(curve.x(minT) + segmentLength / 3 * Math.cos(angle1));
            setControlY1(curve.y(minT) + segmentLength / 3 * Math.sin(angle1));

            setControlX2(curve.x(maxT) + segmentLength / 3 * Math.cos(angle2));
            setControlY2(curve.y(maxT) + segmentLength / 3 * Math.sin(angle2));

            setX(curve.x(maxT));
            setY(curve.y(maxT));
        }
    }
}
