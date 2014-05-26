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

import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.Arrays;
import java.util.function.Function;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Text;
import org.cirdles.jfxutils.CubicBezierApproximationFactory;
import org.cirdles.jfxutils.CurveApproximationFactory;
import org.cirdles.math.ParametricCurve;
import org.cirdles.topsoil.chart.Plotter;
import org.cirdles.topsoil.chart.TickGenerator;

/**
 * A plotter for concordia lines. Takes the concordia line model and turns it into the appropriate JavaFX visual
 * representation.
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class ConcordiaLinePlotter extends Plotter<ParametricCurve, ErrorEllipseChart> {

    private final TickGenerator tickGenerator;
    private final CurveApproximationFactory curveFactory;

    /**
     * Returns a new ConcordiaLinePlotter that plots to the given chart. Note that it does not add its output to the
     * chart, but instead leaves the layout as a chart responsibility.
     *
     * @param chart the chart to plot to
     */
    public ConcordiaLinePlotter(ErrorEllipseChart chart) {
        super(chart);

        tickGenerator = new TickGenerator();
        curveFactory = new CubicBezierApproximationFactory(10);
    }

    /**
     * Plots the concordia line on the chart associated with this plotter. The root node returned by this method is a
     * group containing the path approximated the line, circular tick marks, and tick mark labels.
     *
     * @param concordiaLine the concordia line segment to plot
     * @return the node to be used to show the concordia line in the associated chart
     */
    @Override
    public Node plot(ParametricCurve concordiaLine) {
        double minT = findMinT(concordiaLine,
                               chart.getXAxis().getLowerBound(), chart.getYAxis().getLowerBound(),
                               chart.getXAxis().getUpperBound(), chart.getYAxis().getUpperBound());
        double maxT = findMaxT(concordiaLine,
                               chart.getXAxis().getLowerBound(), chart.getYAxis().getLowerBound(),
                               chart.getXAxis().getUpperBound(), chart.getYAxis().getUpperBound());

        Group lineAndTicks = new Group();
        lineAndTicks.visibleProperty().bind(chart.concordiaLineShownProperty());

        Path line = curveFactory.approximate(concordiaLine, minT, maxT);
        line.getStyleClass().add("concordia-line");

        for (PathElement element : line.getElements()) {
            if (element instanceof MoveTo) {
                MoveTo moveTo = (MoveTo) element;
                moveTo.setX(mapXToDisplay(moveTo.getX()));
                moveTo.setY(mapYToDisplay(moveTo.getY()));
            } else if (element instanceof CubicCurveTo) {
                CubicCurveTo curveTo = (CubicCurveTo) element;
                curveTo.setControlX1(mapXToDisplay(curveTo.getControlX1()));
                curveTo.setControlY1(mapYToDisplay(curveTo.getControlY1()));
                curveTo.setControlX2(mapXToDisplay(curveTo.getControlX2()));
                curveTo.setControlY2(mapYToDisplay(curveTo.getControlY2()));
                curveTo.setX(mapXToDisplay(curveTo.getX()));
                curveTo.setY(mapYToDisplay(curveTo.getY()));
            }
        }

        lineAndTicks.getChildren().add(line);

        // Plot the tick marks (circles) and labels.
        for (Number tick : tickGenerator.majorTicksForRange(minT, maxT)) {
            Circle circle = new Circle(mapXToDisplay(concordiaLine.x(tick.doubleValue())),
                                       mapYToDisplay(concordiaLine.y(tick.doubleValue())),
                                       5);
            Text label = new Text(String.format("%.2f", tick.doubleValue() / 1000000));
            label.setX(circle.getCenterX() - label.getBoundsInLocal().getWidth() - 10);
            label.setY(circle.getCenterY());

            lineAndTicks.getChildren().addAll(circle, label);
        }

        return lineAndTicks;
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

    private static double findMinT(ParametricCurve curve, double minX, double minY, double maxX, double maxY) {
        return Arrays.asList(findTInterceptForX(curve, minX),
                             findTInterceptForY(curve, minY),
                             findTInterceptForX(curve, maxX),
                             findTInterceptForY(curve, maxY))
                .stream()
                .filter(value -> {
                    return curve.x(value) > minX - 1e-2 && curve.x(value) < maxX + 1e-2
                    && curve.y(value) > minY - 1e-2 && curve.y(value) < maxY + 1e-2;
                })
                .reduce(Double.MAX_VALUE, Math::min);
    }

    private static double findMaxT(ParametricCurve curve, double minX, double minY, double maxX, double maxY) {
        return Arrays.asList(findTInterceptForX(curve, minX),
                             findTInterceptForY(curve, minY),
                             findTInterceptForX(curve, maxX),
                             findTInterceptForY(curve, maxY))
                .stream()
                .filter(value -> {
                    return curve.x(value) > minX - 1e-2 && curve.x(value) < maxX + 1e-2
                    && curve.y(value) > minY - 1e-2 && curve.y(value) < maxY + 1e-2;
                })
                .reduce(Double.MIN_VALUE, Math::max);
    }

    private static double findTInterceptForX(ParametricCurve curve, double value) {
        double startT = 0;
        double stopT = 1;

        // Find a range containing x.
        while (curve.x(stopT) < value) {
            startT = stopT;
            stopT *= 2;
        }

        // Perform a binary search and return the result
        double middleT = -1;
        for (int i = 0; i < 100; i++) {
            middleT = (startT + stopT) / 2;
            if (curve.x(middleT) < value) {
                startT = middleT;
            } else {
                stopT = middleT;
            }
        };

        return middleT;
    }

    private static double findTInterceptForY(ParametricCurve curve, double value) {
        double startT = 0;
        double stopT = 1;

        // Find a range containing x.
        while (curve.y(stopT) < value) {
            startT = stopT;
            stopT *= 2;
        }

        // Perform a binary search and return the result
        double middleT = -1;
        for (int i = 0; i < 100; i++) {
            middleT = (startT + stopT) / 2;
            if (curve.y(middleT) < value) {
                startT = middleT;
            } else {
                stopT = middleT;
            }
        };

        return middleT;
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
}
