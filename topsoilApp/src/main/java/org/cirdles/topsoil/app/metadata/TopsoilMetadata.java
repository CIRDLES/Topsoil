/*
 * Copyright 2015 CIRDLES.
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
package org.cirdles.topsoil.app.metadata;

import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;

/**
 * @author John Zeringue
 */
public class TopsoilMetadata implements ApplicationMetadata {

    private ResourceBundle resourceBundle;

    public TopsoilMetadata() {
        resourceBundle = getBundle(TopsoilMetadata.class.getName());
    }

    @Override
    public String getName() {
        return "Topsoil";
    }

    @Override
    public String getVersion() {
        return resourceBundle.getString("version");
    }

}
