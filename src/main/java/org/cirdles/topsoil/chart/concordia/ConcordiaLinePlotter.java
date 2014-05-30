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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Text;
import org.cirdles.math.ConstantFunction;
import org.cirdles.math.CubicBezierCurve;
import org.cirdles.math.ParametricCurve2D;
import org.cirdles.topsoil.Tools;
import org.cirdles.topsoil.chart.Plotter;
import org.cirdles.topsoil.chart.TickGenerator;

/**
 * A plotter for concordia lines. Takes the concordia line model and turns it into the appropriate JavaFX visual
 * representation.
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class ConcordiaLinePlotter extends Plotter<ParametricCurve2D, ErrorEllipseChart> {

    private static final int NUMBER_OF_PIECES = 50;

    private final TickGenerator tickGenerator;

    /**
     * Returns a new ConcordiaLinePlotter that plots to the given chart. Note that it does not add its output to the
     * chart, but instead leaves the layout as a chart responsibility.
     *
     * @param chart the chart to plot to
     */
    public ConcordiaLinePlotter(ErrorEllipseChart chart) {
        super(chart);

        tickGenerator = new TickGenerator();
    }

    /**
     * Plots the concordia line on the chart associated with this plotter. The root node returned by this method is a
     * group containing the path approximated the line, circular tick marks, and tick mark labels.
     *
     * @param concordiaLine the concordia line segment to plot
     * @return the node to be used to show the concordia line in the associated chart
     */
    @Override
    public Node plot(ParametricCurve2D concordiaLine) {
        double minX = chart.getXAxis().getLowerBound();
        double minY = chart.getYAxis().getLowerBound();
        double maxX = chart.getXAxis().getUpperBound();
        double maxY = chart.getYAxis().getUpperBound();

        List<Double> xIntercepts
                = Arrays.asList(concordiaLine.x().minus(new ConstantFunction(minX)).zero(),
                                concordiaLine.x().minus(new ConstantFunction(maxX)).zero())
                .stream()
                .filter(t -> concordiaLine.of(t).y() >= minY && concordiaLine.of(t).y() <= maxY)
                .collect(Collectors.toList());

        List<Double> yIntercepts
                = Arrays.asList(concordiaLine.y().minus(new ConstantFunction(minY)).zero(),
                                concordiaLine.y().minus(new ConstantFunction(maxY)).zero())
                .stream()
                .filter(t -> concordiaLine.of(t).x() >= minX && concordiaLine.of(t).x() <= maxX)
                .collect(Collectors.toList());

        if (xIntercepts.size() + yIntercepts.size() <= 1) {
            return null;
        }

        List<Double> intercepts = new ArrayList<>(xIntercepts);
        intercepts.addAll(yIntercepts);

        Collections.sort(intercepts);

        double minT = intercepts.get(0);
        double maxT = intercepts.get(intercepts.size() - 1);

        Group lineAndTicks = new Group();

        double deltaT = (maxT - minT) / NUMBER_OF_PIECES;

        Path line = CubicBezierCurve.approximate(concordiaLine, minT, minT + deltaT).asPath();
        for (int i = 1; i < NUMBER_OF_PIECES; i++) {
            line.getElements().add(
                    CubicBezierCurve.approximate(concordiaLine, minT + i * deltaT, minT + (i + 1) * deltaT)
                    .asCubicCurveTo());
        }

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
            Circle circle = new Circle(mapXToDisplay(concordiaLine.x().of(tick.doubleValue())),
                                       mapYToDisplay(concordiaLine.y().of(tick.doubleValue())),
                                       5);
            Text label = new Text(Tools.DYNAMIC_STRING_CONVERTER.toString(tick.doubleValue() / 1000000));
            label.setX(circle.getCenterX() - label.getBoundsInLocal().getWidth() - 10);
            label.setY(circle.getCenterY());

            lineAndTicks.getChildren().addAll(circle, label);
        }

        return lineAndTicks;
    }
}
