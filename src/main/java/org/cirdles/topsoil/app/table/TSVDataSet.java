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
package org.cirdles.topsoil.app.table;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TSVDataSet implements DataSet {
    
    private static final Logger LOGGER = Logger.getLogger(TSVDataSet.class.getName());

    private static final String FILE_NAME_REGEX
            = "(.+?)(?:__([a-z]+)__)?(?:__([a-z]+)__)?\\.tsv";

    private static final Pattern FILE_NAME_PATTERN
            = Pattern.compile(FILE_NAME_REGEX);

    private String name;
    private Path tsvPath;
    private Collection<String> flags;

    public TSVDataSet(Path tsvPath) {
        this.tsvPath = tsvPath;
        parseFileName(tsvPath.getFileName().toString());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Path getPath() {
        return tsvPath;
    }

    @Override
    public boolean hasHeaders() {
        return flags.contains("headers");
    }

    @Override
    public boolean isOpen() {
        return flags.contains("open");
    }

    Path tsvPathMarkedAsOpen() {
        if (isOpen()) { // nothing to do
            return tsvPath;
        }
        
        String fileName = tsvPath.getFileName().toString();
        
        String fileNameMarkedAsOpen
                = fileName.replaceFirst("\\.tsv$", "__open__.tsv");
        
        // handle the case where there is no parent
        if (tsvPath.getParent() == null) {
            return tsvPath.getFileSystem().getPath(fileNameMarkedAsOpen);
        }
        
        return tsvPath.getParent().resolve(fileNameMarkedAsOpen);
    }

    @Override
    public void open() {
        if (!isOpen()) {
            try {
                tsvPath = Files.move(getPath(), tsvPathMarkedAsOpen());
                flags.add("open");
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    Path tsvPathNotMarkedAsOpen() {
        String fileName = tsvPath.getFileName().toString();
        
        String fileNameNotMarkedAsOpen
                = fileName.replaceAll("__open__", "");
        
        // handle the case where there is no parent
        if (tsvPath.getParent() == null) {
            return tsvPath.getFileSystem().getPath(fileNameNotMarkedAsOpen);
        }
        
        return tsvPath.getParent().resolve(fileNameNotMarkedAsOpen);
    }

    @Override
    public void close() {
        if (isOpen()) {
            try {
                tsvPath = Files.move(tsvPath, tsvPathNotMarkedAsOpen());
                flags.remove("open");
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    private void parseFileName(String tsvFileName) {
        flags = new HashSet<>();

        Matcher fileNameMatcher = FILE_NAME_PATTERN.matcher(tsvFileName);
        if (fileNameMatcher.matches()) {
            name = fileNameMatcher.group(1);

            for (int i = 2; i <= fileNameMatcher.groupCount(); i++) {
                String flag = fileNameMatcher.group(i);

                if (flag != null) {
                    flags.add(flag.toLowerCase(Locale.US));
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid file name");
        }
    }

}
