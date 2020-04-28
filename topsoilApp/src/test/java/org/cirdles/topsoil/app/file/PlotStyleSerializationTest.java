package org.cirdles.topsoil.app.file;

import org.cirdles.topsoil.app.file.serialization.PlotStyleSerializer;
import org.cirdles.topsoil.javafx.PlotView;
import org.cirdles.topsoil.symbols.SimpleSymbolKey;
import org.cirdles.topsoil.symbols.SimpleSymbolMap;
import org.cirdles.topsoil.symbols.SymbolKey;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PlotStyleSerializationTest {

    @Test
    public void symbol_map_test() {
        try {
            SimpleSymbolMap<SimpleSymbolKey<String>> ssm = new SimpleSymbolMap<>();

            String keyString = "key";
            SimpleSymbolKey<String> ssk = new SimpleSymbolKey<String>("title", "fieldName", keyString, (Class<String>) keyString.getClass());
            String value = "value";

            String objectString = "object";

            ssm.put(ssk,objectString);

            System.out.println("original: " + objectString);
            System.out.println("get(): " + ssm.get(ssk));
            assertEquals(objectString,ssm.get(ssk));

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void serialization_test() {
        try {
            SimpleSymbolMap<SimpleSymbolKey<String>> ssm = new SimpleSymbolMap<>();

            String keyString = "key";
            SimpleSymbolKey<String> ssk = new SimpleSymbolKey<String>("title", "fieldName", keyString, (Class<String>) keyString.getClass());

            String objectString = "object";

            ssm.put(ssk,objectString);

            String fileName = "fileNameTest";
            PlotStyleSerializer.serializeObjectToFile(ssm, fileName);

            SimpleSymbolMap<SimpleSymbolKey<String>> ssmTwo = (SimpleSymbolMap<SimpleSymbolKey<String>>) PlotStyleSerializer.getSerializedObjectFromFile(fileName, true);

            assertEquals(ssm.get(keyString), ssmTwo.get(keyString));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
