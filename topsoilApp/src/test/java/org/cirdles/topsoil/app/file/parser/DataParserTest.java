package org.cirdles.topsoil.app.file.parser;

import org.cirdles.topsoil.app.data.column.DataColumn;
import org.junit.Test;

import java.util.List;

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
        assertEquals(Number.class, DataParser.getColumnDataType(rows, 0, 1));
        assertEquals(String.class, DataParser.getColumnDataType(rows, 1, 1));
    }

    @Test
    public void guessDelimiter_test() {
        String[] lines = DataParser.readLines(CONTENT);
        assertEquals(Delimiter.COMMA, DataParser.guessDelimiter(lines));
    }

    @Test
    public void isDelimiter_test() {
        String[] lines = DataParser.readLines(CONTENT);
        assertTrue(DataParser.isDelimiter(lines, Delimiter.COMMA));
        assertFalse(DataParser.isDelimiter(lines, Delimiter.TAB));
        assertFalse(DataParser.isDelimiter(lines, Delimiter.SEMICOLON));
        assertFalse(DataParser.isDelimiter(lines, Delimiter.COLON));
    }

    @Test
    public void isDouble_test() {
        assertTrue(DataParser.isDouble("1.0"));
        assertTrue(DataParser.isDouble("1.0e-4"));
        assertFalse(DataParser.isDouble("abc"));
        assertFalse(DataParser.isDouble("1.0abc"));
    }

    @Test
    public void getValuesForRow_test() {
        String[][] rows = DataParser.readCells(DataParser.readLines(CONTENT), Delimiter.COMMA.getValue());
        List<DataColumn<?>> columns =
                new DefaultDataParser().parseColumnTree(CONTENT, Delimiter.COMMA.getValue()).getLeafNodes();
        String[] rowOne = rows[1];
        String[] rowTwo = rows[2];
    }

    @Test
    public void readLines_String_test() {
        String[] lines = DataParser.readLines(CONTENT);
        assertEquals("Col1,Col2,Col3,Col4,Col5", lines[0]);
    }

    @Test
    public void readCells_String_test() {
        String[][] cells = DataParser.readCells(DataParser.readLines(CONTENT), Delimiter.COMMA.getValue());
        assertArrayEquals(new String[]{ "Col1", "Col2", "Col3", "Col4", "Col5" }, cells[0]);
    }
}
