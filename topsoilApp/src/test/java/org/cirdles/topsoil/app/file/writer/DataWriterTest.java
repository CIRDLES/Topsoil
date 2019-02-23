package org.cirdles.topsoil.app.file.writer;

import org.cirdles.topsoil.app.file.parser.DataParser;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * @author marottajb
 */
public class DataWriterTest {

    @Test
    public void writeLines_test() {
        try {
            String[] lines = new String[]{
                    "TestLine1",
                    "TestLine2",
                    "TestLine3"
            };
            Path path = Files.createTempFile(null, ".csv");
            DataWriter.writeLines(path, lines);

            String[] newLines = DataParser.readLines(path);
            for (int i = 0; i < lines.length; i++) {
                assertEquals(lines[i], newLines[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

}
