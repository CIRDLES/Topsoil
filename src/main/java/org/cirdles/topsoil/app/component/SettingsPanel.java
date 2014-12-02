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

import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import org.cirdles.javafx.CustomVBox;
import org.cirdles.topsoil.chart.setting.SettingScope;

/**
 *
 * @author John Zeringue
 */
public class SettingsPanel extends CustomVBox<SettingsPanel> {

    private SettingScope settingScope;
    private Map<String, SettingControl> settingControls;

    public SettingsPanel(SettingScope settingScope) {
        super(self -> {
            self.settingScope = settingScope;
            self.settingControls = new HashMap<>();
            
            self.settingScope.addListener((settingName, value) -> {
                if (self.settingControls.containsKey(settingName)) {
                    self.settingControls.get(settingName).update(value);
                } else {
                    self.addControl(settingName);
                }
            });
        });
    }

    @FXML
    private void initialize() {
        for (String settingName : settingScope.getNames()) {
            addControl(settingName);
        }
    }

    private void addControl(String settingName) {
        SettingControl newControl = new SettingControl(settingScope, settingName);
        
        settingControls.put(settingName, newControl);
        getChildren().add(newControl);
    }

}
