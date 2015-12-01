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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.cirdles.topsoil.app.browse.BrowseModule;
import org.cirdles.topsoil.app.builder.BuilderModule;
import org.cirdles.topsoil.app.flyway.FlywayModule;
import org.cirdles.topsoil.app.metadata.MetadataModule;
import org.cirdles.topsoil.app.sqlite.SQLiteMyBatisModule;
import org.cirdles.topsoil.app.util.UtilModule;

import javax.inject.Named;
import java.nio.file.Path;

/**
 *
 * @author John Zeringue
 */
public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new BrowseModule());
        install(new BuilderModule());
        install(new FlywayModule());
        install(new MetadataModule());
        install(new SQLiteMyBatisModule());
        install(new UtilModule());

        requestStaticInjection(Tools.class);
    }

    @Provides
    @Named("JDBC.schema")
    String provideJdbcSchema(
            @Named("applicationDirectory") Path applicationDirectory) {
        return applicationDirectory.resolve("data.db").toString();
    }

}
