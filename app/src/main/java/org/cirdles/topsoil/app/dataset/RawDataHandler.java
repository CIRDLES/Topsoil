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

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.cirdles.topsoil.app.dataset.reader.RawDataReader;
import org.cirdles.topsoil.app.dataset.reader.TsvRawDataReader;
import org.cirdles.topsoil.app.dataset.writer.RawDataWriter;
import org.cirdles.topsoil.app.dataset.writer.TsvRawDataWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by johnzeringue on 9/3/15.
 */
public class RawDataHandler extends BaseTypeHandler<RawData> {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(RawDataHandler.class);

    private final RawDataReader rawDataReader;
    private final RawDataWriter rawDataWriter;

    public RawDataHandler() {
        rawDataReader = new TsvRawDataReader();
        rawDataWriter = new TsvRawDataWriter();
    }

    @Override
    public void setNonNullParameter(
            PreparedStatement preparedStatement,
            int index,
            RawData rawData,
            JdbcType jdbcType) throws SQLException {
        try {
            preparedStatement.setString(index, rawDataWriter.write(rawData));
        } catch (IOException ex) {
            LOGGER.error(null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public RawData getNullableResult(
            ResultSet resultSet,
            String columnName) throws SQLException {
        try {
            return rawDataReader.read(resultSet.getString(columnName));
        } catch (IOException ex) {
            LOGGER.error(null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public RawData getNullableResult(
            ResultSet resultSet,
            int columnIndex) throws SQLException {
        try {
            return rawDataReader.read(resultSet.getString(columnIndex));
        } catch (IOException ex) {
            LOGGER.error(null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public RawData getNullableResult(
            CallableStatement callableStatement,
            int columnIndex) throws SQLException {
        try {
            return rawDataReader.read(callableStatement.getString(columnIndex));
        } catch (IOException ex) {
            LOGGER.error(null, ex);
            throw new RuntimeException(ex);
        }
    }

}
