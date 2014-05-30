/*
 * Copyright 2014 pfif.
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

package org.cirdles.topsoil;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;

    /**
     * A Label whose min width is always set to use its pref width.
     */
    public class LabelUsePrefSize extends Label{

        public LabelUsePrefSize() {
            this("");
            
        }
        
        public LabelUsePrefSize(String text){
            super(text);
            setMinWidth(Region.USE_PREF_SIZE);
        }
        
    }
