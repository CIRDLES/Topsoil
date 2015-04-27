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
package org.cirdles.topsoil.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author John Zeringue
 */
public class PaddableDataset implements Dataset {
    
    private final Dataset baseDataset;
    private final List<Field> paddingFields = new ArrayList<>();
    
    private int numberOfPads = 0;

    public PaddableDataset(Dataset baseDataset) {
        this.baseDataset = baseDataset;
    }

    @Override
    public Optional<String> getName() {
        return baseDataset.getName();
    }

    @Override
    public List<Field> getFields() {
        List<Field> paddedFields = new ArrayList<>(baseDataset.getFields());
        paddedFields.addAll(paddingFields);
        
        return paddedFields;
    }

    @Override
    public List<Entry> getEntries() {
        return baseDataset.getEntries();
    }
    
    public <T> void padWith(Field<? super T> field, T fillValue) {
        paddingFields.add(field);
        getEntries().forEach(entry -> entry.set(field, fillValue));
        
        numberOfPads++;
    }
    
    public void padWithZeros() {
        padWith(new NumberField("fill-" + numberOfPads), 0);
    }
    
}
