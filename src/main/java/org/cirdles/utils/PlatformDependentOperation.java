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

package org.cirdles.utils;

/**
 *
 * @author John Zeringue
 * @param <Params>
 * @param <Result>
 */
public abstract class PlatformDependentOperation<Params, Result> {
    
    private static final String OS_NAME = System.getProperty("os.name");
    
    public final Result perform(Params... params) {
        if (OS_NAME.startsWith("Windows")) {
            return performOnWindows(params);
        } else if (OS_NAME.startsWith("Mac OS")) {
            return performOnMacOS(params);
        } else if (OS_NAME.equals("Linux")) {
            return performOnLinux(params);
        }
        
        throw new RuntimeException("Unrecognized platform " + OS_NAME);
    }
    
    protected abstract Result performOnWindows(Params... params);
    
    protected abstract Result performOnMacOS(Params... params);
    
    protected abstract Result performOnLinux(Params... params);
    
}
