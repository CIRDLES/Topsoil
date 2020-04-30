package org.cirdles.topsoil.app.file;

import org.cirdles.topsoil.Lambda;
import org.cirdles.topsoil.app.file.serialization.PlotStyleSerializer;
import org.cirdles.topsoil.javafx.PlotView;
import org.cirdles.topsoil.plot.PlotOption;
import org.cirdles.topsoil.symbols.SimpleSymbolKey;
import org.cirdles.topsoil.symbols.SimpleSymbolMap;
import org.cirdles.topsoil.symbols.SymbolKey;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PlotStyleSerializationTest {

    @Test
    public void plot_option_test() {
        try {
            SimpleSymbolMap<PlotOption<Number>> ssm = new SimpleSymbolMap<>();

            String objectString = "object";
            Number l = Lambda.U234.getDefaultValue();
            ssm.put(PlotOption.LAMBDA_U234,l);
            System.out.println(Arrays.toString(ssm.entrySet().toArray()));

            System.out.println("original: " + objectString);
            System.out.println("get(): " + ssm.getAndCast(PlotOption.LAMBDA_U234));
            assertEquals(l,ssm.getAndCast(PlotOption.LAMBDA_U234));

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
