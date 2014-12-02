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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author John Zeringue
 */
public class SettingScope {

    private static final Logger logger
            = Logger.getLogger(SettingScope.class.getName());

    private final Optional<SettingScope> parentScope;
    private final Map<String, Object> settings = new HashMap<>();
    private final Collection<BiConsumer<String, Object>> listeners = new HashSet<>();

    public SettingScope() {
        this.parentScope = Optional.empty();
    }

    public SettingScope(SettingScope parentScope) {
        this.parentScope = Optional.ofNullable(parentScope);
    }

    public Optional get(String settingName) {
        Optional value = Optional.ofNullable(settings.get(settingName));

        if (!value.isPresent()) {
            // return the parent's value if it exists
            value = parentScope.flatMap(parentScope -> {
                return parentScope.get(settingName);
            });
        }

        return value;
    }

    public String[] getNames() {
        // copy the key set of settings
        Collection<String> localNames = new HashSet<>(settings.keySet());

        // add this scope's parent's names if it has one
        parentScope.map(SettingScope::getNames).ifPresent(names -> {
            Collections.addAll(localNames, names);
        });

        return localNames.toArray(new String[localNames.size()]);
    }

    public void set(String settingName, Object value) {
        Object currentValue = settings.get(settingName);
        
        logger.info(currentValue + " --> " + value);
        
        if (currentValue == null || !currentValue.equals(value)) {
            logger.log(Level.INFO, "{0} set to value {1} of {2}",
                       new Object[]{settingName, value, value.getClass()});

            settings.put(settingName, value);
            listeners.forEach(listener -> listener.accept(settingName, value));
        } else {
            logger.log(Level.INFO, "{0} is unchanged", new Object[]{settingName});
        }
    }

    public void addListener(BiConsumer<String, Object> listener) {
        listeners.add(listener);
    }

}
