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
* GetApplicationDirectoryOperation.java
* Purpose: This class gets the path to the given application directory depending 
* on the operating system (Windows, Linux, Mac)
* 
* @author johnzeringue
*/
public class GetApplicationDirectoryOperation extends PlatformDependentOperation<String, Path> {

    /**
    * This method returns the path on Windows. 
    * 
    * @param String... params
    * @return Path the path on Windows 
    **/
    @Override
    protected Path performOnWindows(String... params) {
        validateParams(params);
        
        // getenv must be used because appdata is a Windows environment variable
        return Paths.get(System.getenv("appdata"), params[0]);
    }

    /**
    * This method returns the path on MacOS. 
    * 
    * @param String... params
    * @return Path the path on MacOS 
    **/	
    @Override
    protected Path performOnMacOS(String... params) {
        validateParams(params);
		//Apps in Mac stored in the Application Support folder/directory in the users home directory
        return Paths.get(System.getProperty("user.home"), "Library/Application Support", params[0]);
    }

    /**
    * This method returns the path on Linux. 
    * 
    * @param String... params
    * @return Path the path on Linux 
    **/
    @Override
    protected Path performOnLinux(String... params) {
        validateParams(params);
		//Apps in Linux are stored in a hidden directory the user's home directory 
        return Paths.get(System.getProperty("user.home"), buildLinuxFolderName(params[0]));
    }

    /**
    * This method builds the Linux folder name for the given application. 
    * 
    * @param String the name of the application 
    * @return String the Linux folder name as a String
    **/	
    String buildLinuxFolderName(String applicationName) {
    	//Building folder names in Linux requires getting rid of white space and replacing with the '-' character 
    	//and making all it all lowercase. 
        return "." + applicationName.trim().replaceAll(" +", "-").toLowerCase();
    }

    /**
    * This method checks whether the application name provided is valid 
    * 
    * @param String[] params
    * @return void
    * @exception IllegalArgumentException throws an exception if the application name is not valid.
    **/    
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
