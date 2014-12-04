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
package org.cirdles.topsoil.chart.setting;

/**
 *
 * @author John Zeringue
 */
public class SettingTransactor {
    
    private final SettingScope settingScope;
    
    private SettingTransaction currentTransaction;
    
    public SettingTransactor(SettingScope settingScope) {
        this.settingScope = settingScope;
        
        currentTransaction = new SettingTransaction();
    }
    
    public void set(String settingName, Object value) {
        currentTransaction.set(settingName, value);
    }
    
    public void apply() {
        settingScope.apply(currentTransaction);
        currentTransaction = new SettingTransaction();
    }
    
}
