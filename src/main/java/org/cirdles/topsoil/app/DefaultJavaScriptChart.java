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
package org.cirdles.topsoil.app;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import static java.nio.file.FileSystems.newFileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.cirdles.topsoil.chart.JavaScriptChart;

/**
 *
 * @author John Zeringue
 */
public class DefaultJavaScriptChart extends JavaScriptChart {

    private static final String JAR_SCHEME = "jar";

    private static final Map<String, FileSystem> FILE_SYSTEMS
            = new HashMap<>();

    public DefaultJavaScriptChart(String resourceName, Class clazz) {
        super(resourceToPath(resourceName, clazz));
    }

    private static URI resourceToURI(String name, Class clazz)
            throws URISyntaxException {
        return clazz.getResource(name).toURI();
    }

    private static Path uriToPath(URI uri) {
        Path path;

        if (uri.getScheme().equals(JAR_SCHEME)) {
            String[] uriParts = uri.toString().split("!");

            // retrieve or build file system
            Map<String, ?> env = new HashMap<>();
            FileSystem fileSystem = FILE_SYSTEMS.computeIfAbsent(uriParts[0],
                    jarPath -> {
                        try {
                            return newFileSystem(URI.create(jarPath), env);
                        } catch (IOException ex) {
                            return null;
                        }
                    });
            
            path = fileSystem.getPath(uriParts[1]);
        } else { // assume file scheme
            path = Paths.get(uri);
        }

        return path;
    }

    private static Path resourceToPath(String name, Class clazz) {
        try {
            return uriToPath(resourceToURI(name, clazz));
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

}
