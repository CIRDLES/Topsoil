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
package org.cirdles.topsoil.swingdemo;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class SwingDemo {

    // x, y, sigma x, sigma y, rho
    private static final double[][] DATA = new double[][]{
        {0.0722539075, 0.0110295656, 0.0002049758, 0.0000063126, 0.5365532874},
        {0.0721971452, 0.0110309854, 0.0001783027, 0.0000056173, 0.5325448483},
        {0.0721480905, 0.0110333887, 0.0001262722, 0.0000053814, 0.5693849155},
        {0.0720208987, 0.0110278685, 0.0001041118, 0.0000051695, 0.6034598793},
        {0.0722006985, 0.0110287224, 0.0001150679, 0.0000053550, 0.6488140173},
        {0.0721043666, 0.0110269651, 0.0001536438, 0.0000055438, 0.4514464090},
        {0.0721563039, 0.0110282194, 0.0001241486, 0.0000054189, 0.5407720667},
        {0.0721973299, 0.0110274879, 0.0001224165, 0.0000055660, 0.5557499444},
        {0.0721451656, 0.0110281849, 0.0001461117, 0.0000054048, 0.5309378161},
        {0.0720654237, 0.0110247729, 0.0001547497, 0.0000053235, 0.2337854029},
        {0.0721799174, 0.0110318201, 0.0001485404, 0.0000056511, 0.5177944463},
        {0.0721826355, 0.0110283902, 0.0001377158, 0.0000056126, 0.5953348385},
        {0.0720275042, 0.0110278402, 0.0001875497, 0.0000058909, 0.5274591815},
        {0.0721360819, 0.0110276418, 0.0001252055, 0.0000054561, 0.5760966585}
    };

    private static void initAndShowGUI() {
        // This method is invoked on the EDT thread
        JFrame frame = new JFrame("Swing and JavaFX");
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(800, 600);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Platform.runLater(() -> {
            initFX(fxPanel);
        });
    }

    private static void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }

    private static Scene createScene() {
//        NumberNumberChart<ErrorEllipsePlotter> chart = new ConcordiaChart();
//        VBox.setVgrow(chart, Priority.ALWAYS);
//        chart.setData(FXCollections.observableArrayList(new XYChart.Series<>()));
//
//        for (double[] ellipse : DATA) {
//            chart.getData().get(0).getData().add(
//                    new ErrorEllipsePlotter(ellipse[0], ellipse[1], ellipse[2], ellipse[3], ellipse[4]));
//        }
//
//        VBox root = new VBox(new AxisConfigurationToolBar((NumberAxis) chart.getXAxis()),
//                             new AxisConfigurationToolBar((NumberAxis) chart.getYAxis()),
//                             chart);

        return new Scene(null, 1200, 800, true, SceneAntialiasing.BALANCED);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            initAndShowGUI();
        });
    }

}
