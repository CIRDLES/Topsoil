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

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by johnzeringue on 9/8/15.
 */
public class ApplicationDirectoryProvider
        extends PlatformDependentProvider<Path> {

    private final ApplicationMetadata metadata;

    @Inject
    public ApplicationDirectoryProvider(ApplicationMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    protected Path performOnWindows() {
        return Paths.get(System.getenv("appdata"), metadata.getName());
    }

    @Override
    protected Path performOnMacOS() {
        return Paths.get(
                System.getProperty("user.home"),
                "Library",
                "Application Support",
                metadata.getName());
    }

    private String buildLinuxPath() {
        return "." + metadata.getName().trim().replaceAll("\\s+", "-").toLowerCase();
    }

    @Override
    protected Path performOnLinux() {
        return Paths.get(buildLinuxPath());
    }

}
