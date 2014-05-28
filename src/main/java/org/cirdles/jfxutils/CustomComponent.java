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
package org.cirdles.jfxutils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Region;

/**
 *
 * @author zeringuej
 */
public abstract class CustomComponent extends Region implements Initializable {

    public CustomComponent() {
        FXMLLoader fxmlLoader = new FXMLLoader();

        // configure the FXML loader
        fxmlLoader.setLocation(getClass().getResource(getClass().getSimpleName() + ".fxml"));
        fxmlLoader.setResources(getResources(getClass()));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(CustomComponent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static ResourceBundle getResources(Class clazz) {
        ResourceBundle bundle = ResourceBundle.getBundle(clazz.getCanonicalName());

        if (!clazz.equals(CustomComponent.class)) {
            try {
                Method setParent = ResourceBundle.class.getDeclaredMethod("setParent");
                setParent.setAccessible(true);
                setParent.invoke(bundle, getResources(clazz.getSuperclass()));
            } catch (Exception ex) {
                Logger.getLogger(CustomComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return bundle;
    }
}
