package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.util.ExampleData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TopsoilProjectTest {

    private static TopsoilProject projectOne = new TopsoilProject(ExampleData.UPB.getDataTable(), ExampleData.UTH.getDataTable());
    private static TopsoilProject projectOneCopy = new TopsoilProject(ExampleData.UPB.getDataTable(), ExampleData.UTH.getDataTable());
    private static TopsoilProject projectTwo = new TopsoilProject(ExampleData.UPB.getDataTable(), ExampleData.SQUID_3.getDataTable());
    private static TopsoilProject projectThree = new TopsoilProject(ExampleData.UPB.getDataTable());

    @Test
    public void equals_test() {
        assertEquals(projectOne, projectOne);
        assertEquals(projectOne, projectOneCopy);
        assertNotEquals(projectOne, projectTwo);
        assertNotEquals(projectOne, projectThree);
        assertNotEquals(projectOne, "projectFour");
    }

}
