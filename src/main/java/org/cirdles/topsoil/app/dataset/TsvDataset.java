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
package org.cirdles.topsoil.app.dataset;

import org.cirdles.topsoil.app.dataset.reader.TsvRawDataReader;
import org.cirdles.topsoil.dataset.RawData;
import org.cirdles.topsoil.dataset.SimpleDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by johnzeringue on 9/3/15.
 */
public class TsvDataset extends SimpleDataset {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(TsvDataset.class);

    private static RawData safeRead(String rawData) {
        try {
            return new TsvRawDataReader().read(rawData);
        } catch (IOException ex) {
            LOGGER.error(null, ex);
            throw new RuntimeException(ex);
        }
    }

    public TsvDataset(String name, String rawData) {
        super(name, safeRead(rawData));
    }

}
