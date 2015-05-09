/*
 * To Change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cirdles.topsoil.app.utils;

import java.nio.file.Path;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kyser
 */
public class GetDocumentsDirectoryOperationTest {

    private final GetDocumentsDirectoryOperation instance
    = new GetDocumentsDirectoryOperation();

    /**
     * Test of the validateParams method of class GetDirectoryOperation.
     */
    @Test
    public void testValidateParams() {

        String[] params;

        // valid params - just the directory name
        params = new String[] {"Directory"};
        instance.validateParams(params);

        // valid params - extra junk
        params = new String[] {"Directory", "Other", "Other"};
        instance.validateParams(params);

        // valid  params - empty
        params = new String[0];
        instance.validateParams(params);

        // invalid params - null para
        params = new String[] {null};
        try {
            instance.validateParams(params);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
        }

        // invalid params - empty string
        params = new String[] {""};
        try {
            instance.validateParams(params);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
        }
    }
}

