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
package org.cirdles.topsoil.app.sqlite;

import com.google.inject.Provides;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.cirdles.topsoil.app.dataset.DatasetMapper;
import org.cirdles.topsoil.app.dataset.RawDataHandler;
import org.mybatis.guice.MyBatisModule;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.inject.Named;
import javax.sql.DataSource;

/**
 * Created by johnzeringue on 8/27/15.
 */
public class SQLiteMyBatisModule extends MyBatisModule {

    private static final boolean SUPPRESS_CLOSE = true;

    @Override
    protected void initialize() {
        bindTransactionFactoryType(JdbcTransactionFactory.class);

        addMapperClass(DatasetMapper.class);
        addTypeHandlerClass(RawDataHandler.class);
    }

    @Provides
    DataSource provideDataSource(@Named("JDBC.url") String url) {
        return new SingleConnectionDataSource(url, SUPPRESS_CLOSE);
    }

    @Provides
    @Named("JDBC.driver")
    String provideJdbcDriver() {
        return "org.sqlite.JDBC";
    }

    @Provides
    @Named("JDBC.url")
    String provideJdbcUrl(@Named("JDBC.schema") String schema) {
        return "jdbc:sqlite:" + schema;
    }

    @Provides
    @Named("mybatis.environment.id")
    String provideMyBatisEnvironmentId() {
        return "test";
    }

}
