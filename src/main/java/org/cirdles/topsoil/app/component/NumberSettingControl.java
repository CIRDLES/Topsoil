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
package org.cirdles.topsoil.app.component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.cirdles.topsoil.chart.setting.SettingScope;

/**
 *
 * @author John Zeringue
 */
public class NumberSettingControl extends SettingControl<Number> {

    public NumberSettingControl(SettingScope settingScope, String settingName) {
        super(settingScope, settingName);
    }
    
    @Override
    public void update(Number value) {
        // both the slider and the value label should reflect the current value
        double doubleValue = ((Number) value).doubleValue();
        
        settingSlider.setValue(doubleValue);
        settingValue.setText(String.format("%.2f", doubleValue));
    }

    @FXML
    private Label settingLabel;
    @FXML
    private Slider settingSlider;
    @FXML
    private Label settingValue;

    @FXML
    private void initialize() {
        // setting label should show the setting's name
        settingLabel.setText(getSettingName());
        
        getSettingScope().get(getSettingName()).ifPresent(value -> {
            update((Number) value);
        });
        
        // on slider change
        settingSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // update the setting and label values
            settingScope.set(settingName, newValue);
        });
    }
    
}
