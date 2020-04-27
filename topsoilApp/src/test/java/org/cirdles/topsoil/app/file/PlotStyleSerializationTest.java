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
