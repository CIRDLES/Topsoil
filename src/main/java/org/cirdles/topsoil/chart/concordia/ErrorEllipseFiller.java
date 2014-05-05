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
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import org.cirdles.topsoil.chart.Filler;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class ErrorEllipseFiller extends Filler<ErrorEllipse> {

    public ErrorEllipseFiller(XYChart chart) {
        super(chart);
    }

    @Override
    public Node fill(ErrorEllipse errorEllipse) {
        Path ellipse = new Path(new MoveTo(),
                                new CubicCurveTo(),
                                new CubicCurveTo(),
                                new CubicCurveTo(),
                                new CubicCurveTo());
        ellipse.setStroke(Color.TRANSPARENT);
        ellipse.setFill(Color.RED);
        ellipse.setOpacity(.3);
        
        Circle center = new Circle(mapXToDisplay(errorEllipse.getX()),
                                   mapYToDisplay(errorEllipse.getY()),
                                   3); // circle radius
        
        Group node = new Group(ellipse, center);
        node.getStyleClass().add("error-ellipse-fill");

        Matrix controlPoints = errorEllipse.getControlPoints();
        
        MoveTo moveTo = (MoveTo) ellipse.getElements().get(0);
        moveTo.setX(mapXToDisplay(controlPoints.get(0, 0)));
        moveTo.setY(mapYToDisplay(controlPoints.get(0, 1)));

        for (int i = 1; i <= 4; i++) {
            CubicCurveTo cubicCurveTo = (CubicCurveTo) ellipse.getElements().get(i);

            // set control points
            cubicCurveTo.setControlX1(mapXToDisplay(controlPoints.get(i * 3 - 2, 0)));
            cubicCurveTo.setControlY1(mapYToDisplay(controlPoints.get(i * 3 - 2, 1)));
            cubicCurveTo.setControlX2(mapXToDisplay(controlPoints.get(i * 3 - 1, 0)));
            cubicCurveTo.setControlY2(mapYToDisplay(controlPoints.get(i * 3 - 1, 1)));

            // set final point
            cubicCurveTo.setX(mapXToDisplay(controlPoints.get(i * 3, 0)));
            cubicCurveTo.setY(mapYToDisplay(controlPoints.get(i * 3, 1)));
        }

        return node;
    }
}
