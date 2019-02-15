package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.util.file.parser.DefaultFileParser;
import org.cirdles.topsoil.app.util.file.parser.Delimiter;
import org.cirdles.topsoil.app.util.file.parser.FileParser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author marottajb
 */
public class FileParserTest {

    static String CONTENT = "Col1,Col2,Col3,Col4,Col5\n" +
                            "0.0,one,2.0,three,4.0\n" +
                            "0.1,two,2.1,four,4.1\n" +
                            "0.2,three,2.2,five,4.2\n" +
                            "0.3,four,2.3,six,4.3\n" +
                            "0.4,five,2.4,seven,4.4\n";

    @Test
    public void getDataColumnType_test() {
        String[][] rows = FileParser.readCells(FileParser.readLines(CONTENT), ",");
        assertEquals(Double.class, FileParser.getColumnDataType(rows, 0, 1));
        assertEquals(String.class, FileParser.getColumnDataType(rows, 1, 1));
    }

    @Test
    public void guessDelimiter_test() {
        String[] lines = FileParser.readLines(CONTENT);
        assertEquals(Delimiter.COMMA, FileParser.guessDelimiter(lines));
    }

    @Test
    public void isDelimiter_test() {
        String[] lines = FileParser.readLines(CONTENT);
        assertTrue(FileParser.isDelimiter(lines, Delimiter.COMMA));
        assertFalse(FileParser.isDelimiter(lines, Delimiter.TAB));
        assertFalse(FileParser.isDelimiter(lines, Delimiter.SEMICOLON));
        assertFalse(FileParser.isDelimiter(lines, Delimiter.COLON));
    }

    @Test
    public void isDouble_test() {
        assertTrue(FileParser.isDouble("1.0"));
        assertTrue(FileParser.isDouble("1.0e-4"));
        assertFalse(FileParser.isDouble("abc"));
        assertFalse(FileParser.isDouble("1.0abc"));
    }

    @Test
    public void getValuesForRow_test() {
        String[][] rows = FileParser.readCells(FileParser.readLines(CONTENT), Delimiter.COMMA.getValue());
        List<DataColumn<?>> columns =
                new DefaultFileParser().parseColumnTree(CONTENT, Delimiter.COMMA.getValue()).getLeafNodes();
        String[] rowOne = rows[1];
        String[] rowTwo = rows[2];
    }

    @Test
    public void readLines_String_test() {
        String[] lines = FileParser.readLines(CONTENT);
        assertEquals("Col1,Col2,Col3,Col4,Col5", lines[0]);
    }

    @Test
    public void readCells_String_test() {
        String[][] cells = FileParser.readCells(FileParser.readLines(CONTENT), Delimiter.COMMA.getValue());
        assertArrayEquals(new String[]{ "Col1", "Col2", "Col3", "Col4", "Col5" }, cells[0]);
    }
}
