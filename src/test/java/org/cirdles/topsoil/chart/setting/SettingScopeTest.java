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

import java.util.Optional;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author John Zeringue
 */
public class SettingScopeTest {

    /**
     * Test of apply method, of class SettingScope.
     */
    @Test
    public void testApply() {
        String settingName = "Test Setting";
        String expectedValue = "test value";
        
        SettingTransaction transaction = new SettingTransaction();
        transaction.set(settingName, expectedValue);
        
        SettingScope instance = new SettingScope();
        instance.apply(transaction);

        // the setting should've been added
        Optional<String> actualValue = instance.get(settingName);
        assertTrue(actualValue.isPresent());
        assertEquals(expectedValue, actualValue.get());
    }

    /**
     * Test of transaction method, of class SettingScope.
     */
    @Test
    public void testTransaction() {
        String settingName = "Test Setting";
        String expectedValue = "test value";
        
        SettingScope instance = new SettingScope();
        instance.transaction(t -> {
            t.set(settingName, expectedValue);
        });

        // the setting should've been added
        Optional<String> actualValue = instance.get(settingName);
        assertTrue(actualValue.isPresent());
        assertEquals(expectedValue, actualValue.get());
    }

    /**
     * Test of get method, of class SettingScope.
     */
    @Test
    public void testGet() {
        String settingName = "Test Setting";
        String expectedValue = "test value";
        
        SettingScope instance = new SettingScope();
        instance.set(settingName, expectedValue);

        // a setting not set shouldn't exist
        assertEquals(Optional.empty(), instance.get("Nonexistant Setting"));

        // a setting set should
        Optional<String> actualValue = instance.get(settingName);
        assertTrue(actualValue.isPresent());
        assertEquals(expectedValue, actualValue.get());
    }

    /**
     * Test of getSettingNames method, of class SettingScope.
     */
    @Test
    public void testGetSettingNames() {
        SettingScope instance = new SettingScope();

        // the new scope should have no settings
        assertArrayEquals(new String[0], instance.getSettingNames());

        // add a setting
        instance.set("Test Setting", "test value");

        // the scope should have Test Setting
        assertArrayEquals(new String[]{"Test Setting"}, instance.getSettingNames());
    }

    /**
     * Test of set method, of class SettingScope.
     */
    @Test
    public void testSet() {
        String settingName = "Test Setting";
        String expectedValue1 = "test value 1";
        String expectedValue2 = "test value 2";
        
        SettingScope instance = new SettingScope();
        
        assertFalse(instance.get(settingName).isPresent());
        
        instance.set(settingName, expectedValue1);
        
        assertTrue(instance.get(settingName).isPresent());
        assertEquals(expectedValue1, instance.get(settingName).get());
        
        instance.set(settingName, expectedValue2);
        
        assertEquals(expectedValue2, instance.get(settingName).get());
    }
    
}
