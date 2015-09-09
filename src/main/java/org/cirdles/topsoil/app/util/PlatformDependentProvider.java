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

import javax.inject.Provider;

import static java.lang.String.format;

/**
 * Created by johnzeringue on 9/8/15.
 */
public abstract class PlatformDependentProvider<T> implements Provider<T> {

    private static final String OS_NAME = System.getProperty("os.name");

    public final T get() {
        if (OS_NAME.startsWith("Windows")) {
            return performOnWindows();
        } else if (OS_NAME.startsWith("Mac OS")) {
            return performOnMacOS();
        } else if (OS_NAME.equals("Linux")) {
            return performOnLinux();
        }

        throw new RuntimeException(
                format("Unrecognized platform %s.", OS_NAME));
    }

    protected abstract T performOnWindows();

    protected abstract T performOnMacOS();

    protected abstract T performOnLinux();

}
