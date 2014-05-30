/*
 * Copyright 2014 zeringuej.
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
package org.cirdles.math;

import static java.lang.Math.*; // pow
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 *
 * @author zeringuej
 */
public class CubicBezierCurve extends ParametricCurve2D {
    
    private final Vector2D p0;
    private final Vector2D p1;
    private final Vector2D p2;
    private final Vector2D p3;
    
    public CubicBezierCurve(Vector2D p0, Vector2D p1, Vector2D p2, Vector2D p3) {
        this(p0, p1, p2, p3, 0, 1);
    }

    public CubicBezierCurve(Vector2D p0, Vector2D p1, Vector2D p2, Vector2D p3, double t0, double t1) {
        super(new Function() {

            @Override
            public double of(double t) {
                return CubicBezierCurve.of(p0, p1, p2, p3, t0, t1, t).x();
            }

            @Override
            public Function prime() throws UnsupportedOperationException {
                return new Function() {

                    @Override
                    public double of(double t) {
                        return CubicBezierCurve.primeOf(p0, p1, p2, p3, t0, t1, t).x();
                    }
                };
            }
        }, new Function() {

            @Override
            public double of(double t) {
                return CubicBezierCurve.of(p0, p1, p2, p3, t0, t1, t).y();
            }

            @Override
            public Function prime() throws UnsupportedOperationException {
                return new Function() {

                    @Override
                    public double of(double t) {
                        return CubicBezierCurve.primeOf(p0, p1, p2, p3, t0, t1, t).y();
                    }
                };
            }
        });
        
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    private static Vector2D of(Vector2D p0, Vector2D p1, Vector2D p2, Vector2D p3, double t0, double t1, double t) {
        double s = (t - t0) / (t1 - t0);
        return p0.times(pow(1 - s, 3))
                .plus(p1.times(3 * pow(1 - s, 2) * s))
                .plus(p2.times(3 * (1 - s) * pow(s, 2)))
                .plus(p3.times(pow(s, 3)));
    }

    private static Vector2D primeOf(Vector2D p0, Vector2D p1, Vector2D p2, Vector2D p3, double t0, double t1, double t) {
        return p1.minus(p0).times(3 * pow(t1 - t, 2))
                .plus(p2.minus(p1).times(6 * (t - t0) * (t - t1)))
                .plus(p3.minus(p2).times(3 * pow(t - t0, 2)))
                .dividedBy(pow(t1 - t0, 3));
    }
    
    public Path asPath() {
        return new Path(new MoveTo(p0.x(), p0.y()), asCubicCurveTo());
    }
    
    public CubicCurveTo asCubicCurveTo() {
        return new CubicCurveTo(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
    }
    
    public static CubicBezierCurve approximate(ParametricCurve2D curve, double t0, double t1) {
        Vector2D p0 = curve.of(t0);
        Vector2D p3 = curve.of(t1);
        
        Vector2D p1 = p0.plus(curve.prime().of(t0).times((t1 - t0) / 3));
        Vector2D p2 = p3.minus(curve.prime().of(t1).times((t1 - t0) / 3));
        
        return new CubicBezierCurve(p0, p1, p2, p3, t0, t1);
    }
}
