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

import com.johnzeringue.extendsfx.layout.CustomVBox;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
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

            self.settingScope.addListener(settingNames -> {
                for (String settingName : settingNames) {
                    if (self.settingControls.containsKey(settingName)) {
                        Object value = settingScope.get(settingName).get();
                        self.settingControls.get(settingName).update(value);
                    } else {
                        self.addControl(settingName);
                    }
                }
            });
        });
    }

    @FXML
    private void initialize() {
        for (String settingName : settingScope.getSettingNames()) {
            addControl(settingName);
        }
    }

    private void addControl(String settingName) {
        SettingControl newControl = null;
        Object value = settingScope.get(settingName).get();

        if (value instanceof Number) {
            newControl = new NumberSettingControl(settingScope, settingName);
        } else if (value instanceof String) {
            newControl = new StringSettingControl(settingScope, settingName);
        } else {
            Logger.getGlobal().log(Level.WARNING, value.getClass() + " not handled");
        }

        settingControls.put(settingName, newControl);
        getChildren().add(newControl);
    }

}
