package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.model.*;

import java.nio.file.Path;
import java.util.*;

/**
 * @author marottajb
 */
public class DefaultDataParser extends DataParser {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DefaultDataParser(Path path) {
        super(path);
    }

    public DefaultDataParser(String content) {
        super(content);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    ColumnTree parseColumnTree() {
        return new ColumnTree(parseHeaders(countHeaderRows()));
    }

    List<DataSegment> parseData() {
        ColumnTree columnTree = parseColumnTree();
        List<DataColumn<?>> columns = columnTree.getLeafNodes();
        int startIndex = countHeaderRows();
        List<DataRow> rows = new ArrayList<>();
        for (int rowIndex = startIndex; rowIndex < cells.length; rowIndex++) {
            rows.add(new DataRow("row" + (rowIndex - startIndex + 1), getValuesForRow(cells[rowIndex], columns)));
        }

        List<DataSegment> segments = new ArrayList<>();
        segments.add(new DataSegment("model", rows.toArray(new DataRow[]{})));
        return segments;
    }

}
