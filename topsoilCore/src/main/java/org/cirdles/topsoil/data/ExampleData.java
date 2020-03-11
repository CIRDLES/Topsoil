package org.cirdles.topsoil.data;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.file.parser.DataParser;
import org.cirdles.topsoil.file.parser.DefaultDataParser;
import org.cirdles.topsoil.file.parser.Squid3DataParser;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author marottajb
 */
public enum ExampleData {

    UPB("upb-example.csv", DataTemplate.DEFAULT, Uncertainty.TWO_SIGMA_PERCENT),
    UTH("uth-example.csv", DataTemplate.DEFAULT, Uncertainty.TWO_SIGMA_ABSOLUTE),
    SQUID_3("squid3-example.csv", DataTemplate.SQUID_3, Uncertainty.ONE_SIGMA_PERCENT);

    private String fileName;
    private DataTemplate template;
    private Uncertainty unctFormat;

    ExampleData(String fileName, DataTemplate template, Uncertainty unctFormat) {
        this.fileName = fileName;
        this.template = template;
        this.unctFormat = unctFormat;
    }

    /**
     * Returns a new {@code DataTable} representing the example data.
     *
     * @param tableClass    Class of a concrete implementor of DataTable
     *
     * @return  DataTable
     *
     * @throws IOException  if unable to find the data file
     */
    public <T extends DataTable<C, R>, C extends DataColumn<?>, R extends DataRow> T getDataTable(Class<T> tableClass) throws IOException {
        final ResourceExtractor re = new ResourceExtractor(ExampleData.class);
        Path filePath = re.extractResourceAsPath(fileName);
        DataParser<T, C, R> dataParser = template.getParser(tableClass);
        T table = dataParser.parseDataTable(filePath, ",", fileName);
        table.setUncertainty(unctFormat);
        return table;
    }

}
