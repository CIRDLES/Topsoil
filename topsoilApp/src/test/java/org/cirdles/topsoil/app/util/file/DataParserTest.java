package org.cirdles.topsoil.app.util.file;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author marottajb
 */
public class DataParserTest {

    static String CONTENT = "Col1,Col2,Col3,Col4,Col5\n" +
                            "0.0,one,2.0,three,4.0\n" +
                            "0.1,two,2.1,four,4.1\n" +
                            "0.2,three,2.2,five,4.2\n" +
                            "0.3,four,2.3,six,4.3\n" +
                            "0.4,five,2.4,seven,4.4\n";

    @Test
    public void getDataColumnType_test() {
        String[][] rows = DataParser.readCells(DataParser.readLines(CONTENT), ",");
        assertEquals(Double.class, DataParser.getColumnDataType(rows,0, 1));
        assertEquals(String.class, DataParser.getColumnDataType(rows,1, 1));
    }
}
