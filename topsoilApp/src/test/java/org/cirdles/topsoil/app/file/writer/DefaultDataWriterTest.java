package org.cirdles.topsoil.app.file.writer;

import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.util.SampleData;
import org.cirdles.topsoil.app.file.parser.DataParser;
import org.cirdles.topsoil.app.file.parser.DefaultDataParser;
import org.cirdles.topsoil.app.file.parser.Delimiter;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.variable.DependentVariable;
import org.cirdles.topsoil.variable.IndependentVariable;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author marottajb
 */
public class DefaultDataWriterTest {

    DataTable table = SampleData.UPB.getDataTable();

    @Test
    public void writeTableToFile_test() {
        try {
            Path path = Files.createTempFile(null, ".csv");

            DataWriter dataWriter = new DefaultDataWriter();
            dataWriter.writeTableToFile(path, table);

            DataParser dataParser = new DefaultDataParser();
            DataTable after = dataParser.parseDataTable(path, Delimiter.COMMA.getValue(), "upb-sample.csv");
            after.setIsotopeSystem(IsotopeSystem.UPB);
            after.setUnctFormat(Uncertainty.TWO_SIGMA_PERCENT);
            List<DataColumn<?>> columns = after.getColumnTree().getLeafNodes();
            after.setColumnForVariable(IndependentVariable.X, columns.get(0));
            after.setColumnForVariable(DependentVariable.SIGMA_X, columns.get(1));
            after.setColumnForVariable(IndependentVariable.Y, columns.get(2));
            after.setColumnForVariable(DependentVariable.SIGMA_Y, columns.get(3));
            after.setColumnForVariable(IndependentVariable.RHO, columns.get(4));

            assertEquals(table, after);

        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}
