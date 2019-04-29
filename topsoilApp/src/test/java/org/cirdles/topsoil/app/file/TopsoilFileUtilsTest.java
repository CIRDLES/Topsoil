package org.cirdles.topsoil.app.file;

import org.junit.Test;

import static org.cirdles.topsoil.app.file.TopsoilFileUtils.readLines;
import static org.junit.Assert.*;

public class TopsoilFileUtilsTest {

    static String CSV_CONTENT = "Col1,Col2,Col3,Col4,Col5\n" +
            "0.0,one,2.0,three,4.0\n" +
            "0.1,two,2.1,four,4.1\n" +
            "0.2,three,2.2,five,4.2\n" +
            "0.3,four,2.3,six,4.3\n" +
            "0.4,five,2.4,seven,4.4\n";

    @Test
    public void guessDelimiter_test() {
        String[] lines = readLines(CSV_CONTENT);
        assertEquals(Delimiter.COMMA, TopsoilFileUtils.guessDelimiter(lines));
    }

    @Test
    public void isDelimiter_test() {
        String[] lines = readLines(CSV_CONTENT);
        assertTrue(TopsoilFileUtils.isDelimiter(lines, Delimiter.COMMA));
        assertFalse(TopsoilFileUtils.isDelimiter(lines, Delimiter.TAB));
        assertFalse(TopsoilFileUtils.isDelimiter(lines, Delimiter.SEMICOLON));
        assertFalse(TopsoilFileUtils.isDelimiter(lines, Delimiter.COLON));
    }

    @Test
    public void readLines_String_test() {
        String[] lines = TopsoilFileUtils.readLines(CSV_CONTENT);
        assertEquals("Col1,Col2,Col3,Col4,Col5", lines[0]);
    }

    @Test
    public void readCells_String_test() {
        String[][] cells = TopsoilFileUtils.readCells(TopsoilFileUtils.readLines(CSV_CONTENT), Delimiter.COMMA.asString());
        assertArrayEquals(new String[]{ "Col1", "Col2", "Col3", "Col4", "Col5" }, cells[0]);
    }


}
