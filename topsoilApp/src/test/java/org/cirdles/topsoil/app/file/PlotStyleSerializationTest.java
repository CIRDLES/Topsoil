package org.cirdles.topsoil.app.file;

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
            fail();
        }
    }

    @Test
    public void serialization_test() {
        try {


        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
