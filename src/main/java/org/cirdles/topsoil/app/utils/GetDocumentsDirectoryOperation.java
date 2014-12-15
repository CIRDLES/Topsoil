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
 * @author John Zeringue
 */
public class GetDocumentsDirectoryOperation extends PlatformDependentOperation<String, Path>{

    @Override
    protected Path performOnWindows(String... params) {
        if (params.length == 1 && !"".equals(params)) {
            return Paths.get(System.getProperty("user.home"), "Documents", params[0]);
        } else {
            return Paths.get(System.getProperty("user.home"), "Documents");
        }
    }

    @Override
    protected Path performOnMacOS(String... params) {
        if (params.length == 1 && !"".equals(params)) {
            return Paths.get(System.getProperty("user.home"), "Documents", params[0]);
        } else {
            return Paths.get(System.getProperty("user.home"), "Documents");
        }
    }

    @Override
    protected Path performOnLinux(String... params) {
        if (params.length == 1 && !"".equals(params)) {
            return Paths.get(System.getProperty("user.home"), "Documents", params[0]);
        } else {
            return Paths.get(System.getProperty("user.home"), "Documents");
        }
    }
   
}
