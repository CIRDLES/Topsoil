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
 * A provider that can act differently across different platforms.
 */
public abstract class PlatformDependentProvider<T> implements Provider<T> {

    private final String osName;

    protected PlatformDependentProvider(String osName) {
        this.osName = osName;
    }

    public final T get() {
        if (osName.startsWith("Windows")) {
            return getOnWindows();
        } else if (osName.startsWith("Mac OS")) {
            return getOnMacOS();
        } else if (osName.equals("Linux")) {
            return getOnLinux();
        }

        throw new RuntimeException(
                format("Unrecognized platform %s.", osName));
    }

    protected abstract T getOnWindows();

    protected abstract T getOnMacOS();

    protected abstract T getOnLinux();

}
