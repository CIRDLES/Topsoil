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

import com.johnzeringue.extendsfx.layout.CustomHBox;
import org.cirdles.topsoil.chart.setting.SettingScope;

/**
 *
 * @author John Zeringue
 */
public abstract class SettingControl<T> extends CustomHBox<SettingControl> {

    protected SettingScope settingScope;
    protected String settingName;

    public SettingControl(SettingScope settingScope, String settingName) {
        super(self -> {
            self.settingScope = settingScope;
            self.settingName = settingName;
        });
    }
    
    public String getSettingName() {
        return settingName;
    }
    
    public SettingScope getSettingScope() {
        return settingScope;
    }
    
    public abstract void update(T value);

}
