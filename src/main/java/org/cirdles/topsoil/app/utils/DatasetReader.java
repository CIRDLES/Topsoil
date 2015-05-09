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
package org.cirdles.topsoil.app.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cirdles.topsoil.data.Dataset;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public interface DatasetReader {

    public Dataset read(InputStream source) throws IOException;

    public default Dataset read(String source) throws IOException {
        return read(new ByteArrayInputStream(source.getBytes(getCharset())));
    }

    public default Dataset read(File source)
            throws FileNotFoundException, IOException {
        try (InputStream inputStream = new FileInputStream(source)) {
            return read(inputStream);
        }
    }

    public default Dataset read(Path source) throws IOException {
        return read(Files.newInputStream(source));
    }

    public default Charset getCharset() {
        return Charset.forName("UTF-8");
    }

}
