package org.cirdles.topsoil.app.file.parser;

import org.cirdles.topsoil.app.data.DataTable;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Provides methods for parsing value-separated data into a {@link DataTable}.
 *
 * @author marottajb
 */
public interface DataParser {

    DataTable parseDataTable(Path path, String delimiter, String label) throws IOException;

    DataTable parseDataTable(String content, String delimiter, String label);

}
