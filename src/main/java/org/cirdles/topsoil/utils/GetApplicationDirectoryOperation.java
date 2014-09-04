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

import java.nio.file.Path;
import java.nio.file.Paths;
import org.cirdles.utils.PlatformDependentOperation;

/**
 *
 * @author johnzeringue
 */
public class GetApplicationDirectoryOperation extends PlatformDependentOperation<String, Path> {
    
    private static PlatformDependentOperation<String, Path> instance;

    @Override
    protected Path performOnWindows(String... params) {
        if (params.length != 1 || params[0] == null || params[0].equals("")) {
            throw new IllegalArgumentException("Valid application name must be provided");
        }
        
        // getenv must be used because appdata is a Windows evironment variable
        return Paths.get(System.getenv("appdata"), params[0]);
    }

    @Override
    protected Path performOnMacOS(String... params) {
        if (params.length != 1 || params == null || params.equals("")) {
            throw new IllegalArgumentException("Valid application name must be provided");
        }
        
        return Paths.get(System.getProperty("user.home"), "Library/Application Support", params[0]);
    }

    @Override
    protected Path performOnLinux(String... params) {
        if (params.length != 1 || params == null || params.equals("")) {
            throw new IllegalArgumentException("Valid application name must be provided");
        }
        
        return Paths.get(System.getProperty("user.home"), "." + params[0].replaceAll(" *", "-").toLowerCase());
    }

}
