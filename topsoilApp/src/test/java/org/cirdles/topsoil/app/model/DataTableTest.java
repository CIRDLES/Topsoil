package org.cirdles.topsoil.app.model;

import org.cirdles.topsoil.app.util.file.DefaultDataParser;
import org.cirdles.topsoil.app.util.file.Squid3DataParser;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataTableTest {

    private static String CONTENT = (
            ",Cat1,,,Cat2,,Cat3\n" +
                    ",,Col2,,,,\n" +
                    ",Col1,Col2,,,,\n" +
                    ",Col1,Col2,,Col4,,\n" +
                    ",Col1,Col2,Col3,Col4,Col5,\n" +
                    "Seg1,,,,,,\n" +
                    "Seg1:Row1,1.0,2.0,3.0,4.0,5.0,\n" +
                    "Seg2,,,,,,\n" +
                    "Seg2:Row1,1.0,2.0,3.0,4.0,5.0,\n"
    );

    private static DataTable oracle;

    @BeforeClass
    public static void setup() {
        oracle = new Squid3DataParser(CONTENT).parseDataTable("oracle");
    }

    @Test
    public void getRowByIndex_test() {
        assertEquals("Seg1:Row1", oracle.getRowByIndex(0).getLabel());
        assertEquals("Seg2:Row1", oracle.getRowByIndex(1).getLabel());
    }

}
