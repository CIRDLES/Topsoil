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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.control.TableView;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public abstract class TableReader<T> {
    public abstract void read(String src, TableView<T> dest);
    
    public void read(Path src, TableView<T> dest) throws IOException {
        read(new String(Files.readAllBytes(src), StandardCharsets.UTF_8), dest);
    }
}
