package org.cirdles.topsoil.app.file.writer;

import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.file.Delimiter;
import org.cirdles.topsoil.app.file.TableFileExtension;
import org.cirdles.topsoil.app.file.TopsoilFileUtils;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Defines behavior for writing the data in a {@link DataTable} to a file.
 *
 * @author marottajb
 */
public abstract class AbstractDataWriter implements DataWriter {

    /** {@inheritDoc} */
    @Override
    public final void writeTableToFile(Path path, DataTable table) throws IOException {
        TableFileExtension extension = TableFileExtension.getExtensionFromPath(path);
        if (extension == null) {
            throw new IllegalArgumentException("Unsupported file extension for path: " + path);
        }

        String[] lines = linesFromData(table, extension.getDelimiter());
        TopsoilFileUtils.writeLines(path, lines);
    }

    protected abstract String[] linesFromData(DataTable table, Delimiter delimiter);

}
