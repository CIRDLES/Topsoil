package org.cirdles.topsoil.app.util;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.util.file.parser.DataParser;
import org.cirdles.topsoil.app.util.file.parser.Squid3DataParser;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.app.util.file.parser.DefaultDataParser;
import org.cirdles.topsoil.isotope.IsotopeSystem;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author marottajb
 */
public enum SampleData {

    UPB("upb-sample.csv", IsotopeSystem.UPB, Uncertainty.TWO_SIGMA_PERCENT),
    UTH("uth-sample.csv", IsotopeSystem.UTH, Uncertainty.TWO_SIGMA_ABSOLUTE),
    SQUID_3("squid3-sample.csv", IsotopeSystem.GENERIC, Uncertainty.ONE_SIGMA_ABSOLUTE);

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }

}
