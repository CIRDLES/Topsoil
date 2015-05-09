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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 *
 * @author John Zeringue
 */
public class SettingScope {

    private final Map<String, Object> settings;
    private final Collection<Consumer<String[]>> listeners;

    private final Collection<String> changedSettings;

    public SettingScope() {
        settings = new HashMap<>();
        listeners = new HashSet<>();

        changedSettings = new HashSet<>();
    }

    public void apply(SettingTransaction transaction) {
        transaction.forEach(operation -> operation.applyTo(this));

        if (!changedSettings.isEmpty()) {
            String[] changedSettingsArray = changedSettings.toArray(new String[0]);
            listeners.forEach(listener -> listener.accept(changedSettingsArray));
            
            changedSettings.clear();
        }
    }
    
    public void transaction(Consumer<SettingTransaction> transactionConsumer) {
        SettingTransaction transaction = new SettingTransaction();
        transactionConsumer.accept(transaction);
        this.apply(transaction);
    }

    public Optional get(String settingName) {
        return Optional.ofNullable(settings.get(settingName));
    }

    public String[] getSettingNames() {
        return settings.keySet().toArray(new String[0]);
    }

    public void set(String settingName, Object value) {
        Object currentValue = settings.get(settingName);

        if (currentValue == null || !currentValue.equals(value)) {
            settings.put(settingName, value);
            changedSettings.add(settingName);
            listeners.forEach(listener -> listener.accept(changedSettings.toArray(new String[0])));
        }
    }

    public void addListener(Consumer<String[]> listener) {
        listeners.add(listener);
    }
    
    public SettingTransactor buildTransactor() {
        return new SettingTransactor(this);
    }

    public static final class OperationFactory implements SettingOperationFactory {

        @Override
        public SettingOperation buildGet(String settingName) {
            return settingScope -> {
                return settingScope.settings.keySet().toArray(new String[0]);
            };
        }

        @Override
        public SettingOperation<Void> buildSet(String settingName, Object value) {
            return settingScope -> {
                settingScope.settings.merge(settingName, value, (oldValue, newValue) -> {
                    // if the values are different let the setting scope know
                    if (!oldValue.equals(newValue)) {
                        settingScope.changedSettings.add(settingName);
                    }

                    return newValue;
                });

                return null;
            };
        }

    }

}
