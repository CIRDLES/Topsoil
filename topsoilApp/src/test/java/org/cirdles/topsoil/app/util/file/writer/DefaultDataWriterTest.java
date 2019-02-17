package org.cirdles.topsoil.app.util.file.writer;

import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.util.SampleData;
import org.cirdles.topsoil.app.util.file.parser.DataParser;
import org.cirdles.topsoil.app.util.file.parser.DefaultDataParser;
import org.cirdles.topsoil.app.util.file.parser.Delimiter;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

            assertEquals(table, after);

        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}
