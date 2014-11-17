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
package org.cirdles.topsoil.app.components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.cirdles.javafx.CustomHBox;
import org.cirdles.topsoil.chart.Setting;
import org.cirdles.topsoil.chart.SettingListener;

/**
 *
 * @author John Zeringue
 */
public class SettingControl extends CustomHBox<SettingControl> implements SettingListener {

    private Setting setting;

    public SettingControl(Setting setting) {
        super(self -> {
            self.setting = setting;
            self.setting.addListener(self);
        });
    }

    @Override
    public void onUpdate(Setting setting) {
        settingSlider.setValue(setting.getValue());
        settingValue.setText(String.format("%.2f", setting.getValue()));
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
        settingLabel.setText(setting.getName());
        
        // both the slider and the value label should reflect the current value
        settingSlider.setValue(setting.getValue());
        settingValue.setText(String.format("%.2f", setting.getValue()));
        
        // on slider change
        settingSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // update the setting and label values
            setting.setValue(newValue.doubleValue());
            settingValue.setText(String.format("%.2f", newValue));
        });
    }
    
    private Setting getSetting() {
        return setting;
    }
    
    private void setSetting(Setting setting) {
        this.setting = setting;
    }

}
