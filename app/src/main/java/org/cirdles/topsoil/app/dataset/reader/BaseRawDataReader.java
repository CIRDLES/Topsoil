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
package org.cirdles.topsoil.app.dataset.reader;

import org.cirdles.topsoil.app.dataset.RawData;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author John Zeringue
 */
public abstract class BaseRawDataReader implements RawDataReader {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    public RawData read(String source) throws IOException {
        return read(new ByteArrayInputStream(source.getBytes(UTF_8)));
    }

    @Override
    public RawData read(File source) throws IOException {
        try (InputStream inputStream = new FileInputStream(source)) {
            return read(inputStream);
        }
    }

    @Override
    public RawData read(Path source) throws IOException {
        return read(Files.newInputStream(source));
    }

}
