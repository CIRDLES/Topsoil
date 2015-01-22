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
package org.cirdles.topsoil.app.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.cirdles.utils.PlatformDependentOperation;

/**
 *
 * @author johnzeringue
 */
public class GetApplicationDirectoryOperation extends PlatformDependentOperation<String, Path> {

    @Override
    protected Path performOnWindows(String... params) {
        validateParams(params);
        
        // getenv must be used because appdata is a Windows evironment variable
        return Paths.get(System.getenv("appdata"), params[0]);
    }

    @Override
    protected Path performOnMacOS(String... params) {
        validateParams(params);
        return Paths.get(System.getProperty("user.home"), "Library/Application Support", params[0]);
    }

    @Override
    protected Path performOnLinux(String... params) {
        validateParams(params);
        return Paths.get(System.getProperty("user.home"), buildLinuxFolderName(params[0]));
    }
    
    String buildLinuxFolderName(String applicationName) {
        return "." + applicationName.trim().replaceAll(" +", "-").toLowerCase();
    }
    
    void validateParams(String[] params) {
        boolean stillValid = params.length >= 1;
        
        if (stillValid) {
            String applicationName = params[0];
            
            stillValid &= applicationName != null;
            stillValid &= !"".equals(applicationName);
        }
        
        if (!stillValid) {
            throw new IllegalArgumentException("Valid application name must be provided");
        }
    }

}
