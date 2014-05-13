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

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import org.cirdles.topsoil.chart.Plotter;
import org.cirdles.topsoil.chart.StyleAccessor;
import org.cirdles.topsoil.chart.TickGenerator;

/**
 * A plotter for concordia lines. Takes the concordia line model and turns it into the appropriate JavaFX visual
 * representation.
 * 
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class ConcordiaLinePlotter extends Plotter<ConcordiaLine, ConcordiaChartStyleAccessor> {

    private final TickGenerator tickGenerator;

    /**
     * Returns a new ConcordiaLinePlotter that plots to the given chart. Note that it does not add its output to the
     * chart, but instead leaves the layout as a chart responsibility.
     * 
     * @param chart the chart to plot to
     */
    public ConcordiaLinePlotter(XYChart chart, ConcordiaChartStyleAccessor s) {
        super(chart, s);

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
    public Node plot(ConcordiaLine concordiaLine) {
        Group lineAndTicks = new Group();
        lineAndTicks.visibleProperty().bind(style.get().concordiaLineShownProperty());
        
        // Plot the line itself.
        Path line = new Path(new MoveTo(mapXToDisplay(concordiaLine.getXs()[0]),
                                        mapYToDisplay(concordiaLine.getYs()[0])));
        line.getStyleClass().add("concordia-line");

        for (int i = 1; i < concordiaLine.getXs().length && i < concordiaLine.getYs().length; i++) {
            line.getElements().add(new LineTo(mapXToDisplay(concordiaLine.getXs()[i]),
                                              mapYToDisplay(concordiaLine.getYs()[i])));
        }

        lineAndTicks.getChildren().add(line);

        // Plot the tick marks (circles) and labels.
        for (Number tick : tickGenerator.majorTicksForRange(concordiaLine.getStartT(),
                                                            concordiaLine.getEndT())) {
            Circle circle = new Circle(mapXToDisplay(ConcordiaLine.getX(tick.doubleValue())),
                                       mapYToDisplay(ConcordiaLine.getY(tick.doubleValue())),
                                       5);
            Text label = new Text(String.format("%.2f", tick.doubleValue() / 1000000));
            label.setX(circle.getCenterX() - label.getBoundsInLocal().getWidth() - 10);
            label.setY(circle.getCenterY());

            lineAndTicks.getChildren().addAll(circle, label);
        }

        return lineAndTicks;
    }
}
