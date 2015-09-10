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

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides the application storage directory across multiple platforms.
 */
public class ApplicationDirectoryProvider
        extends PlatformDependentProvider<Path> {

    private final ApplicationMetadata metadata;
    private final String appData;
    private final String userHome;

    @Inject
    public ApplicationDirectoryProvider(
            ApplicationMetadata metadata,
            // nullable because appdata is set on Linux and Mac OS
            @Named("appdata") @Nullable String appData,
            @Named("os.name") String osName,
            @Named("user.home") String userHome) {
        super(osName);
        this.metadata = metadata;
        this.appData = appData;
        this.userHome = userHome;
    }

    @Override
    protected Path getOnWindows() {
        return Paths.get(appData, metadata.getName());
    }

    @Override
    protected Path getOnMacOS() {
        return Paths.get(
                userHome,
                "Library",
                "Application Support",
                metadata.getName());
    }

    private String buildLinuxDirectoryName() {
        return "." + metadata.getName().trim().replaceAll("\\s+", "-").toLowerCase();
    }

    @Override
    protected Path getOnLinux() {
        return Paths.get(userHome, buildLinuxDirectoryName());
    }

}
