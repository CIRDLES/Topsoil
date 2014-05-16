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
 *//*
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
package org.cirdles.topsoil.chart;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import org.cirdles.jfxutils.NumberField;

/**
 * A simple Toolbar that allow the configuration of a <code>NumberAxis</code>. The field that are configurable are :
 * <ul>
 * <li>Autotick</li>
 * <li>Anchor Tick</li>
 * <li>Tick unit</li>
 * </ul>
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class AxisConfigurationToolBar extends ToolBar {

    private final NumberAxis axis;

    public AxisConfigurationToolBar(NumberAxis axis) {
        super();
        this.axis = axis;

        ToggleGroup autoTickingToggle = new ToggleGroup();

        HBox layout = new HBox();
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setSpacing(5);
        layout.setPadding(new Insets(0, 10, 0, 0));

        RadioButton autoTickButton = new RadioButton("Autotick");

        RadioButton manualTickButton = new RadioButton("Anchor tick:");
        
        ObservableValue<Number> range = axis.upperBoundProperty().subtract(axis.lowerBoundProperty());

        TextField AnchorTickField = new NumberField(axis.getTickGenerator().anchorTickProperty(), range);
        AnchorTickField.setAlignment(Pos.BASELINE_RIGHT);
        AnchorTickField.setEditable(false);

        Label TickUnitLabel = new Label("Tick unit:");

        TextField TickUnitField = new NumberField(axis.getTickGenerator().tickUnitProperty(), range);
        TickUnitField.setAlignment(Pos.BASELINE_RIGHT);
        TickUnitField.setEditable(false);

        autoTickingToggle.getToggles().addAll(autoTickButton,
                                              manualTickButton);
        autoTickingToggle.selectToggle(autoTickButton);
        autoTickingToggle.selectedToggleProperty().addListener(
                (ObservableValue<? extends Toggle> ov, Toggle oldSelection, Toggle newSelection) -> {
                    if (oldSelection.equals(manualTickButton)) {
                        AnchorTickField.setEditable(false);
                        TickUnitField.setEditable(false);
                        axis.getTickGenerator().setAutoTicking(true);
                    } else if (newSelection.equals(manualTickButton)) {
                        AnchorTickField.setEditable(true);
                        TickUnitField.setEditable(true);
                        axis.getTickGenerator().setAutoTicking(false);
                    }
                });

        layout.getChildren().addAll(autoTickButton,
                                    manualTickButton,
                                    AnchorTickField,
                                    TickUnitLabel,
                                    TickUnitField);

        getItems().add(layout);
    }
}
