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
package org.cirdles.topsoil.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import netscape.javascript.JSObject;

/**
 *
 * @author John Zeringue
 */
public class SettingsManager implements SettingListener {

    private final Map<String, Setting> settings;
    private final List<SettingsManagerListener> settingsManagerListeners;
    private final List<SettingListener> settingListeners;

    public SettingsManager() {
        settings = new HashMap<>();
        settingsManagerListeners = new ArrayList<>();
        settingListeners = new ArrayList<>();
    }

    public SettingsManager addSetting(String name, double value) {
        // setting should not previously exist
        if (settings.containsKey(name)) {
            throw new IllegalArgumentException("Cannot add a setting for an existing name.");
        }

        Setting setting = new Setting(name, value);
        setting.addListener(this);
        settings.put(name, setting);
        
        // trigger listeners for add
        settingsManagerListeners.stream().forEach(listener -> listener.onSettingAdded(setting));

        // so that this method can be chained
        return this;
    }
    
    public Setting getSetting(String name) {
        return settings.get(name);
    }

    public Setting[] getSettings() {
        return settings.values().toArray(new Setting[0]);
    }
    
    public void addListener(SettingsManagerListener listener) {
        settingsManagerListeners.add(listener);
    }
    
    public void addListener(SettingListener listener) {
        settingListeners.add(listener);
    }
    
    public void addJSListener(JSObject jsObject) {
        settingListeners.add(setting -> jsObject.call("onUpdate", setting));
    }

    @Override
    public void onUpdate(Setting setting) {
        settingListeners.stream().forEach(listener -> listener.onUpdate(setting));
    }

}
