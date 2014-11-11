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

import java.util.Optional;

/**
 * A partial implementation of {@link Chart} that stores and retrieves set data.
 * 
 * @author John Zeringue
 */
public abstract class BaseChart<T> implements Chart<T> {
    
    private Optional<T> data = Optional.empty();

    @Override
    public Optional<T> getData() {
        return data;
    }

    @Override
    public void setData(T data) {
        this.data = Optional.ofNullable(data);
    }
    
}
