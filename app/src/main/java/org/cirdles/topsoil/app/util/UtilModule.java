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
package org.cirdles.topsoil.app.util;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Named;
import javax.inject.Singleton;
import java.awt.Desktop;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

import static com.google.inject.name.Names.named;

/**
 * Created by johnzeringue on 9/8/15.
 */
public class UtilModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Path.class)
                .annotatedWith(named("applicationDirectory"))
                .toProvider(ApplicationDirectoryProvider.class)
                .in(Singleton.class);

        bind(Alerter.class).to(ErrorAlerter.class);
        bind(IssueCreator.class).to(StandardGitHubIssueCreator.class);
    }

    @Provides
    Desktop provideDesktop() {
        Desktop desktop;

        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        } else {
            desktop = null;
        }

        return desktop;
    }

    @Provides
    FileSystem provideFileSystem() {
        return FileSystems.getDefault();
    }

    @Provides
    Properties provideSystemProperties() {
        return System.getProperties();
    }

    @Provides
    @Named("appdata")
    String provideAppData() {
        return System.getenv("appdata");
    }

    @Provides
    @Named("os.name")
    String provideOsName() {
        return System.getProperty("os.name");
    }

    @Provides
    @Named("user.home")
    String provideUserHome() {
        return System.getProperty("user.home");
    }

}
