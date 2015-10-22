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

import org.cirdles.topsoil.app.metadata.ApplicationMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Provides the application storage directory across multiple platforms.
 */
public class ApplicationDirectoryProvider
        extends PlatformDependentProvider<Path> {

    private static Logger LOGGER
            = LoggerFactory.getLogger(ApplicationDirectoryProvider.class);

    private final ApplicationMetadata metadata;
    private final FileSystem fileSystem;
    private final String appData;
    private final String userHome;

    @Inject
    public ApplicationDirectoryProvider(
            ApplicationMetadata metadata,
            FileSystem fileSystem,
            // nullable because appdata is not set on Linux and Mac OS
            @Named("appdata") @Nullable String appData,
            @Named("os.name") String osName,
            @Named("user.home") String userHome) {

        super(osName);
        this.metadata = metadata;
        this.fileSystem = fileSystem;
        this.appData = appData;
        this.userHome = userHome;
    }

    private void createIfNecessary(Path path) {
        try {
            Files.createDirectories(path);
        } catch (Exception ex) {
            LOGGER.error(null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Path getOnWindows() {
        Path applicationDirectory = fileSystem.getPath(appData, metadata.getName());
        createIfNecessary(applicationDirectory);
        return applicationDirectory;
    }

    @Override
    protected Path getOnMacOS() {
        Path applicationDirectory = fileSystem.getPath(
                userHome,
                "Library",
                "Application Support",
                metadata.getName());

        createIfNecessary(applicationDirectory);
        return applicationDirectory;
    }

    private String buildLinuxDirectoryName() {
        return "." + metadata.getName().trim().replaceAll("\\s+", "-").toLowerCase();
    }

    @Override
    protected Path getOnLinux() {
        Path applicationDirectory = fileSystem.getPath(
                userHome,
                buildLinuxDirectoryName());

        createIfNecessary(applicationDirectory);
        return applicationDirectory;
    }

}
