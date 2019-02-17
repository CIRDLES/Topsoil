package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.util.SampleData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author marottajb
 */
public class TopsoilProjectTest {

    @Test
    public void equals_test() {
        TopsoilProject oracle = new TopsoilProject(
                SampleData.UPB.getDataTable(),
                SampleData.SQUID_3.getDataTable()
        );
        TopsoilProject other = new TopsoilProject(
                SampleData.UPB.getDataTable(),
                SampleData.SQUID_3.getDataTable()
        );

        assertEquals(oracle, other);
    }

}
