package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.data.ColumnTree;
import org.cirdles.topsoil.app.data.DataSegment;

import java.nio.file.Path;

/**
 * @author marottajb
 */
public class DefaultDataParser extends DataParser {

    private String delim;

    public DefaultDataParser(Path path) {
        super(path);
        this.delim = super.getDelimiter();
    }

    public DefaultDataParser(String content) {
        super(content);
        this.delim = super.getDelimiter();
    }

    @Override
    public String getDelimiter() {
        return delim;
    }

    public ColumnTree parseColumnTree() {
        // TODO see template project
        return null;
    }

    public DataSegment[] parseData() {
        ColumnTree columnTree = parseColumnTree();
        // TODO see template project
        return null;
    }

}
