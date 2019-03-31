package org.cirdles.topsoil.app.file.writer;

import org.cirdles.topsoil.app.data.DataTable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * @author marottajb
 */
public interface DataWriter {

    boolean writeTableToFile(Path path, DataTable table) throws IOException;

    static boolean writeLines(Path path, String[] lines) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(path.toFile());
        OutputStreamWriter out = new OutputStreamWriter(fileOut, StandardCharsets.UTF_8);

        for (String line : lines) {
            out.write(line + System.lineSeparator());
        }

        out.close();
        fileOut.close();
        return true;
    }

}
