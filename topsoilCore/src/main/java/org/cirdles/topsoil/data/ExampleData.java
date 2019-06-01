package org.cirdles.topsoil.data;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.Uncertainty;
import org.cirdles.topsoil.file.parser.DataParser;
import org.cirdles.topsoil.file.parser.DefaultDataParser;
import org.cirdles.topsoil.file.parser.Squid3DataParser;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author marottajb
 */
public enum ExampleData {

    UPB("upb-example.csv", IsotopeSystem.UPB, Uncertainty.TWO_SIGMA_PERCENT),
    UTH("uth-example.csv", IsotopeSystem.UTH, Uncertainty.TWO_SIGMA_ABSOLUTE),
    SQUID_3("squid3-example.csv", IsotopeSystem.UPB, Uncertainty.ONE_SIGMA_PERCENT);

    private String fileName;
    private IsotopeSystem isotopeSystem;
    private Uncertainty unctFormat;

    ExampleData(String fileName, IsotopeSystem isotopeSystem, Uncertainty unctFormat) {
        this.fileName = fileName;
        this.isotopeSystem = isotopeSystem;
        this.unctFormat = unctFormat;
    }

    /**
     * Returns a new {@code DataTable} representing the example data.
     *
     * @return  DataTable
     */
    public DataTable getDataTable() {
        final ResourceExtractor re = new ResourceExtractor(ExampleData.class);
        Path filePath = re.extractResourceAsPath(fileName);
        DataParser dataParser;
        if (this == SQUID_3) {
            dataParser = new Squid3DataParser();
        } else {
            dataParser = new DefaultDataParser();
        }
        DataTable table = null;
        try {
            table = dataParser.parseDataTable(filePath, ",", fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }

}
