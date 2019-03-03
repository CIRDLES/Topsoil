package org.cirdles.topsoil.app.util;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataComposite;
import org.cirdles.topsoil.app.file.parser.DataParser;
import org.cirdles.topsoil.app.file.parser.Squid3DataParser;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.app.file.parser.DefaultDataParser;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.variable.DependentVariable;
import org.cirdles.topsoil.variable.IndependentVariable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author marottajb
 */
public enum SampleData {

    UPB("upb-sample.csv", IsotopeSystem.UPB, Uncertainty.TWO_SIGMA_PERCENT),
    UTH("uth-sample.csv", IsotopeSystem.UTH, Uncertainty.TWO_SIGMA_ABSOLUTE),
    SQUID_3("squid3-sample.csv", IsotopeSystem.UPB, Uncertainty.ONE_SIGMA_PERCENT);

    private String fileName;
    private IsotopeSystem isotopeSystem;
    private Uncertainty unctFormat;

    SampleData(String fileName, IsotopeSystem isotopeSystem, Uncertainty unctFormat) {
        this.fileName = fileName;
        this.isotopeSystem = isotopeSystem;
        this.unctFormat = unctFormat;
    }

    public DataTable getDataTable() {
        final ResourceExtractor re = new ResourceExtractor(SampleData.class);
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
            table.setUnctFormat(unctFormat);
            if (this == UPB || this == UTH) {
                List<DataColumn<?>> columns = table.getColumnRoot().getLeafNodes();
                table.setColumnForVariable(IndependentVariable.X, columns.get(0));
                table.setColumnForVariable(DependentVariable.SIGMA_X, columns.get(1));
                table.setColumnForVariable(IndependentVariable.Y, columns.get(2));
                table.setColumnForVariable(DependentVariable.SIGMA_Y, columns.get(3));
                table.setColumnForVariable(IndependentVariable.RHO, columns.get(4));
            } else if (this == SQUID_3) {
                List<DataColumn<?>> columns =
                        ((DataComposite) table.getColumnRoot().find("204Pb-Corrected")).getLeafNodes();
                table.setColumnForVariable(IndependentVariable.X, columns.get(columns.size() - 5));
                table.setColumnForVariable(DependentVariable.SIGMA_X, columns.get(columns.size() - 4));
                table.setColumnForVariable(IndependentVariable.Y, columns.get(columns.size() - 3));
                table.setColumnForVariable(DependentVariable.SIGMA_Y, columns.get(columns.size() - 2));
                table.setColumnForVariable(IndependentVariable.RHO, columns.get(columns.size() - 1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }

}
