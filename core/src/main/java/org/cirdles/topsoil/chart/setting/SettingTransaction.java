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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author John Zeringue
 */
public final class SettingTransaction implements Iterable<SettingOperation> {

    private static final SettingOperationFactory operationFactory;
    static {
        operationFactory = new SettingScope.OperationFactory();
    }

    private final List<SettingOperation> operations;

    public SettingTransaction() {
        operations = new ArrayList<>();
    }

    private void add(SettingOperation operation) {
        operations.add(operation);
    }

    public void get(String settingName) {
        add(operationFactory.buildGet(settingName));
    }

    public void set(String settingName, Object value) {
        add(operationFactory.buildSet(settingName, value));
    }

    @Override
    public Iterator<SettingOperation> iterator() {
        return operations.iterator();
    }

}
