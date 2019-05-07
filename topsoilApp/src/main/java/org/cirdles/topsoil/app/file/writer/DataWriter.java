package org.cirdles.topsoil.app.file.writer;

import org.cirdles.topsoil.app.data.DataTable;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Provides methods for writing the data in a {@link DataTable} to a file.
 *
 * @author marottajb
 */
public interface DataWriter {

    /**
     * Writes the data in the provided {@code DataTable} to the specified {@code Path}.
     *
     * @param path  file Path
     * @param table DataTable
     *
     * @throws IOException  for file errors
     */
    void writeTableToFile(Path path, DataTable table) throws IOException;

}
