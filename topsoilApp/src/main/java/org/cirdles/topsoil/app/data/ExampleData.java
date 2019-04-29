package org.cirdles.topsoil.app.data;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.file.parser.DataParser;
import org.cirdles.topsoil.app.file.parser.Squid3DataParser;
import org.cirdles.topsoil.Uncertainty;
import org.cirdles.topsoil.app.file.parser.DefaultDataParser;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.variable.Variables;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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
            table.setIsotopeSystem(isotopeSystem);
            table.setUncertainty(unctFormat);
            if (this == UPB || this == UTH) {
                List<DataColumn<?>> columns = table.getDataColumns();
                table.setColumnForVariable(Variables.X, columns.get(0));
                table.setColumnForVariable(Variables.SIGMA_X, columns.get(1));
                table.setColumnForVariable(Variables.Y, columns.get(2));
                table.setColumnForVariable(Variables.SIGMA_Y, columns.get(3));
                table.setColumnForVariable(Variables.RHO, columns.get(4));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }

}
