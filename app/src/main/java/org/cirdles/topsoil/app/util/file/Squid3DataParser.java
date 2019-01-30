package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.data.ColumnTree;
import org.cirdles.topsoil.app.data.DataSegment;

import java.nio.file.Path;

/**
 * @author marottajb
 */
public class Squid3DataParser extends DataParser {

    private String delim;

    public Squid3DataParser(Path path) {
        super(path);
        this.delim = getDelimiter();
    }

    public Squid3DataParser(String content) {
        super(content);
        this.delim = getDelimiter();
    }

    @Override
    public String getDelimiter() {
        // TODO see template project
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
