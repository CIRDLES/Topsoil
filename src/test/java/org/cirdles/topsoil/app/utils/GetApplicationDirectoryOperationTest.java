/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.topsoil.app.utils;

import java.nio.file.Path;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author zeringue
 */
public class GetApplicationDirectoryOperationTest {

    private final GetApplicationDirectoryOperation instance
            = new GetApplicationDirectoryOperation();

    /**
     * Test of buildLinuxFolderName method, of class
     * GetApplicationDirectoryOperation.
     */
    @Test
    public void testBuildLinuxFolderName() {
        String applicationName;
        String expectedResult;
        String result;

        // multi-word application name
        applicationName = "Application Name";
        expectedResult = ".application-name";
        result = instance.buildLinuxFolderName(applicationName);
        assertEquals(expectedResult, result);

        // single-word application name
        applicationName = "Application";
        expectedResult = ".application";
        result = instance.buildLinuxFolderName(applicationName);
        assertEquals(expectedResult, result);
    }

    /**
     * Test of validateParams method, of class GetApplicationDirectoryOperation.
     */
    @Test
    public void testValidateParams() {
        String[] params;

        // valid params - just the application name
        params = new String[]{"Application"};
        instance.validateParams(params);

        // valid params - extra junk
        params = new String[]{"Application", "Other", "Other"};
        instance.validateParams(params);

        // invalid params - empty
        params = new String[0];
        try {
            instance.validateParams(params);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }
        
        // invalid params - null first param
        params = new String[]{null};
        try {
            instance.validateParams(params);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }
        
        // invalid params - empty string
        params = new String[]{""};
        try {
            instance.validateParams(params);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }
    }

}
