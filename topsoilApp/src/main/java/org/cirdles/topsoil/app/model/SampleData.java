package org.cirdles.topsoil.app.model;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.app.util.file.DataParser;
import org.cirdles.topsoil.app.util.file.DefaultDataParser;
import org.cirdles.topsoil.app.util.file.Squid3DataParser;
import org.cirdles.topsoil.isotope.IsotopeSystem;

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
            dataParser = new Squid3DataParser(filePath);
        } else {
            dataParser = new DefaultDataParser(filePath);
        }
        DataTable table = dataParser.parseDataTable(fileName);
        table.setIsotopeSystem(isotopeSystem);
        table.setUnctFormat(unctFormat);
        return table;
    }

    public void printDataTable() {
        DataTable table = getDataTable();
        System.out.println("LABEL: " + table.getLabel());
        for (DataSegment seg : table.getChildren()) {
            System.out.println(("SEGMENT_LABEL: " + seg.getLabel()));
            for (DataRow row : seg.getChildren()) {
                System.out.println(row);
            }
        }
    }

}
