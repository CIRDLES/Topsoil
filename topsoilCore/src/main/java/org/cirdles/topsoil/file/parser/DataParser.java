package org.cirdles.topsoil.file.parser;

import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.DataRow;
import org.cirdles.topsoil.data.DataTable;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Provides methods for parsing value-separated data into a {@link DataTable}.
 *
 * @author marottajb
 */
public interface DataParser<T extends DataTable<C, R>, C extends DataColumn<?>, R extends DataRow> {

    T parseDataTable(Path path, String delimiter, String label) throws IOException;

    T parseDataTable(String content, String delimiter, String label);

    default boolean isParseableString(String content, String delimiter) {
        // TODO something more sophisticated
        return content.contains(delimiter);
    }

}
