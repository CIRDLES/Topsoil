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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

/**
 * Created by johnzeringue on 9/9/15.
 */
@RunWith(Parameterized.class)
public class PlatformDependentProviderTest {

    @Parameters
    public static Collection<Object[]> getParameters() {
        return asList(new Object[][]{
                {"FreeBSD", OperatingSystem.OTHER},
                {"Linux", OperatingSystem.LINUX},
                {"Mac OS X", OperatingSystem.MAC_OS},
                {"SunOS", OperatingSystem.OTHER},
                {"Windows 98", OperatingSystem.WINDOWS},
                {"Windows 2003", OperatingSystem.WINDOWS},
                {"Windows XP", OperatingSystem.WINDOWS}
        });
    }

    @Rule
    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    public ExpectedException exception = ExpectedException.none();

    private final String osName;
    private final OperatingSystem expectedOs;

    private PlatformDependentProvider platformDependentProvider;

    public PlatformDependentProviderTest(
            String osName,
            OperatingSystem expectedOs) {
        this.osName = osName;
        this.expectedOs = expectedOs;
    }

    @Before
    public void setUp() {
        platformDependentProvider = spy(new PlatformDependentProvider(osName) {

            @Override
            protected Object getOnWindows() {
                return null;
            }

            @Override
            protected Object getOnMacOS() {
                return null;
            }

            @Override
            protected Object getOnLinux() {
                return null;
            }

        });
    }

    @Test
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public void testGet() throws Exception {
        if (expectedOs.equals(OperatingSystem.OTHER)) {
            exception.expect(RuntimeException.class);
            exception.expectMessage("Unrecognized platform");
        }

        platformDependentProvider.get();

        if (expectedOs.equals(OperatingSystem.LINUX)) {
            verify(platformDependentProvider).getOnLinux();
        } else if (expectedOs.equals(OperatingSystem.MAC_OS)) {
            verify(platformDependentProvider).getOnMacOS();
        } else if (expectedOs.equals(OperatingSystem.WINDOWS)) {
            verify(platformDependentProvider).getOnWindows();
        }

        verifyNoMoreInteractions(platformDependentProvider);
    }

    private enum OperatingSystem {
        LINUX, MAC_OS, WINDOWS, OTHER
    }

}
