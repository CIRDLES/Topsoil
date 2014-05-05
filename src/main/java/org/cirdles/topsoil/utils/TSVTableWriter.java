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

package org.cirdles.topsoil.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class TSVTableWriter implements TableWriter<Map> {
    
    private final boolean writeHeaders;

    public TSVTableWriter(boolean writeHeaders) {
        this.writeHeaders = writeHeaders;
    }

    @Override
    public void write(TableView<Map> src, Path dest) {
        StringBuilder stringBuilder = new StringBuilder();
        
        if (writeHeaders) {
            for (TableColumn<Map, ?> column : src.getColumns()) {
                stringBuilder.append(column.getText());
                stringBuilder.append('\t');
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append('\n');
        }
        
        for (int i = 0; i < src.getItems().size(); i++) {
            for (TableColumn<Map, ?> column : src.getColumns()) {
                stringBuilder.append(column.getCellData(i));
                stringBuilder.append('\t');
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append('\n');
        }
        
        try {
            Files.write(dest, stringBuilder.toString().getBytes("utf-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TSVTableWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TSVTableWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
