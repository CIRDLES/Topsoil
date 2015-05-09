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
 * GetDocumentsDirectoryOperation.java
 *
 * @author John Zeringue
 */
public class GetDocumentsDirectoryOperation extends PlatformDependentOperation<String, Path> {

    /**
     *This method returns the path on Windows.
     *@return path on Windows
     */
    @Override
    protected Path performOnWindows(String... params) {

        return getPath(params);
    }

    /**
     *This method returns the path on MacOS.
     *@return path on MacOS
     */
    @Override
    protected Path performOnMacOS(String... params) {

        return getPath(params);
    }

    /**
     *This method returns the path on Linux.
     *@return path on Linux
     */
    @Override
    protected Path performOnLinux(String... params) {

        return getPath(params);
    }

    /**
     * This method returns the Path of the of documents directory of the user.
     * @param params  The folders within the users documents.
     * @return A path object representing the directory.
     */

    private Path getPath(String... params) {

        validateParams(params);

        if (params.length == 1 ) {
            return Paths.get(System.getProperty("user.home"), "Documents", params[0]);
        }
        else {
            return Paths.get(System.getProperty("user.home"), "Documents");
        }
    }

    /**
     *This method checks whether the directory name provided is valid
     *
     *@param String[] params
     *@exception IllegalArgumentException throws an exception if the directory name is not valid.
     **/
    void validateParams(String... params) {


        boolean stillValid1 = params.length ==0;
        boolean stillValid2 = params.length >=1;

        if(stillValid1) {

            stillValid1 &= params != null;

        }
        if (stillValid2) {

            String directoryName = params[0];

            stillValid2 &= directoryName != null;
            stillValid2 &= !"".equals(directoryName);

        }
        if(!stillValid1 && !stillValid2) {
            throw new IllegalArgumentException("Valid directory name must be provided");
        }
    }
}
