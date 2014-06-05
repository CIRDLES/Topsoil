/*
 * Copyright 2014 pfif.
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
package org.cirdles.topsoil.chart.concordia.panels;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.cirdles.jfxutils.NumberField;
import org.cirdles.topsoil.chart.NumberAxis;

/**
 *
 * @author pfif
 */
public class AxisConfigurationSubpanel extends VBox implements Initializable {

    @FXML private CheckBox gridLinesCheckBox;

    @FXML private Label titleLabel;
    @FXML private ToggleButton tickingButton;
    @FXML private ToggleButton scaleButton;

    @FXML private Pane childPaneContainer;

    @FXML private GridPane tickingPanel;
    @FXML private CheckBox autoTickCheckBox;
    @FXML private NumberField ticknf;
    @FXML private NumberField tickUnitnf;
    @FXML private NumberField minTickUnitnf;

    @FXML private GridPane scalePanel;
    @FXML private NumberField lowerBoundnf;
    @FXML private NumberField upperBoundnf;

    private ObjectProperty<NumberAxis> axis = new SimpleObjectProperty();
    private ObjectProperty<String> title = new SimpleObjectProperty();

    public AxisConfigurationSubpanel() {
        FXMLLoader loader = new FXMLLoader(AxisConfigurationSubpanel.class.getResource("axisconfigurationsubpanel.fxml"),
                                           ResourceBundle.getBundle("org.cirdles.topsoil.Resources"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(AxisConfigurationSubpanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        titleLabel.textProperty().bind(title);

        axis.addListener((ObservableValue<? extends NumberAxis> observable, NumberAxis oldValue, NumberAxis newValue) -> {
            initializeAxis();
        });

        tickingButton.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                childPaneContainer.getChildren().add(tickingPanel);
            } else {
                childPaneContainer.getChildren().remove(tickingPanel);
            }
        });

        scaleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                childPaneContainer.getChildren().add(scalePanel);
            } else {
                childPaneContainer.getChildren().remove(scalePanel);
            }
        });

        scaleButton.setSelected(true);
    }

    public void initializeAxis() {
        if (axis.get() != null) {
            autoTickCheckBox.selectedProperty().bindBidirectional(axis.get().getTickGenerator().autoTickingProperty());

            ticknf.convertedProperty().bindBidirectional(axis.get().getTickGenerator().anchorTickProperty());
            ticknf.disableProperty().bind(autoTickCheckBox.selectedProperty());

            tickUnitnf.convertedProperty().bindBidirectional(axis.get().getTickGenerator().tickUnitProperty());
            tickUnitnf.disableProperty().bind(autoTickCheckBox.selectedProperty());

            minTickUnitnf.convertedProperty().bindBidirectional(axis.get().minorTickCountProperty());
            minTickUnitnf.disableProperty().bind(autoTickCheckBox.selectedProperty());

            lowerBoundnf.convertedProperty().bindBidirectional(axis.get().lowerBoundProperty());
            upperBoundnf.convertedProperty().bindBidirectional(axis.get().upperBoundProperty());

            XYChart chart = (XYChart) axis.get().getParent().getParent();
            
            lowerBoundnf.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    axis.get().setAutoRanging(false);
                }
            });
            
            lowerBoundnf.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    axis.get().setAutoRanging(false);
                }
            });

            if (axis.get().getSide().equals(Side.LEFT) || axis.get().getSide().equals(Side.RIGHT)) {
                gridLinesCheckBox.selectedProperty().bindBidirectional(chart.horizontalGridLinesVisibleProperty());
            } else {
                gridLinesCheckBox.selectedProperty().bindBidirectional(chart.verticalGridLinesVisibleProperty());
            }
        }
    }

    public ObjectProperty<NumberAxis> axisProperty() {
        return axis;
    }

    public ObjectProperty<String> titleProperty() {
        return title;
    }
}
