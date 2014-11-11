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
package org.cirdles.topsoil.lib.chart;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author John Zeringue
 */
public class Setting {
    private List<SettingListener> listeners;
    
    private final String name;
    private double value;
    
    public Setting(String name, double value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setValue(double value) {
        this.value = value;
        
        listeners.stream().forEach(listener -> listener.onUpdate(this));
    }
    
    public void addListener(SettingListener listener) {
        // lazily initialize listeners list
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        
        listeners.add(listener);
    }
}
