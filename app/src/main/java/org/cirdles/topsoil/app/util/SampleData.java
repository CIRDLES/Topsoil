package org.cirdles.topsoil.app.util;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.data.*;
import org.cirdles.topsoil.app.data.node.DataNode;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.app.util.file.DataParser;
import org.cirdles.topsoil.app.util.file.DefaultDataParser;
import org.cirdles.topsoil.app.util.file.Squid3DataParser;
import org.cirdles.topsoil.isotope.IsotopeSystem;

import java.nio.file.Path;
import java.util.*;

/**
 * @author marottajb
 */
public enum SampleData {

    UPB("upb-sample.csv", IsotopeSystem.UPB, UncertaintyFormat.TWO_SIGMA_PERCENT),
    UTH("uth-sample.csv", IsotopeSystem.UTH, UncertaintyFormat.TWO_SIGMA_ABSOLUTE),
    GENERIC("", IsotopeSystem.GENERIC, UncertaintyFormat.ONE_SIGMA_ABSOLUTE),
    SQUID_3("squid3-sample.csv", IsotopeSystem.GENERIC, UncertaintyFormat.ONE_SIGMA_ABSOLUTE);

    private String fileName;
    private IsotopeSystem isotopeSystem;
    private UncertaintyFormat unctFormat;

    SampleData(String fileName, IsotopeSystem isotopeSystem, UncertaintyFormat unctFormat) {
        this.fileName = fileName;
        this.isotopeSystem = isotopeSystem;
        this.unctFormat = unctFormat;
    }

    public DataTable parseDataTable() {
        final ResourceExtractor re = new ResourceExtractor(SampleData.class);
        Path filePath = re.extractResourceAsPath(fileName);
        DataParser dataParser;
        if (this == SQUID_3) {
            dataParser = new Squid3DataParser(filePath);
        } else {
            dataParser = new DefaultDataParser(filePath);
        }
        return new DataTable("Sample_UPb_Data",
                             isotopeSystem,
                             unctFormat,
                             dataParser.parseColumnTree(),
                             Arrays.asList(dataParser.parseData()));
    }

    public DataTable getDataTable() {
        DataColumn noCatOne = new DataColumn("noCatOne");
        DataColumn noCatTwo = new DataColumn("noCatTwo");
        DataColumn firstCatOne = new DataColumn("firstCatOne");
        DataColumn firstCatTwo = new DataColumn("firstCatTwo");
        DataColumn secondCatOne = new DataColumn("secondCatOne");

        DataCategory firstCat = new DataCategory("firstCat", firstCatOne, firstCatTwo);
        DataCategory secondCat = new DataCategory("secondCat", secondCatOne);
        DataCategory emptyCat = new DataCategory("emptyCat");

        List<DataNode> categories = new ArrayList<>(Arrays.asList(noCatOne, noCatTwo, firstCat, secondCat, emptyCat));

        List<DataSegment> dataSegments = new ArrayList<>();
        DataSegment segment;
        DataRow row;
        Map<DataColumn, Object> values;
        int size = 3;
        for (int i = 0; i < size; i++) {
            segment = new DataSegment("DataSegment" + (i + 1));
            for (int j = 0; j < size; j++) {
                values = new HashMap<>();
                values.put(noCatOne, (double) i + j);
                values.put(noCatTwo, (double) i + j + 1);
                values.put(firstCatOne, (double) i + j + 2);
                values.put(firstCatTwo, (double) i + j + 3);
                values.put(secondCatOne, (double) i + j + 4);
                row = new DataRow("DataRow" + (i + 1), values);
                segment.getChildren().add(row);
            }
            dataSegments.add(segment);
        }

        IsotopeSystem isotopeSystem = IsotopeSystem.GENERIC;
        UncertaintyFormat unctFormat = UncertaintyFormat.ONE_SIGMA_ABSOLUTE;

        return new DataTable("TestDataTable", isotopeSystem, unctFormat, categories, dataSegments);
    }

}
