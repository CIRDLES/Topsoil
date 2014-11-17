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
import org.cirdles.javafx.CustomVBox;
import org.cirdles.topsoil.chart.Setting;
import org.cirdles.topsoil.chart.SettingsManager;
import org.cirdles.topsoil.chart.SettingsManagerListener;

/**
 *
 * @author John Zeringue
 */
public class SettingsPanel extends CustomVBox<SettingsPanel> implements SettingsManagerListener {

    private SettingsManager settingsManager;

    public SettingsPanel(SettingsManager settingsManager) {
        super(self -> {
            self.settingsManager = settingsManager;
            self.settingsManager.addListener(self);
        });
    }

    @FXML
    private void initialize() {
        if (settingsManager.getSettings() == null) {
            return;
        }

        for (Setting setting : settingsManager.getSettings()) {
            getChildren().add(new SettingControl(setting));
        }
    }

    @Override
    public void onSettingAdded(Setting setting) {
        getChildren().add(new SettingControl(setting));
    }

}
