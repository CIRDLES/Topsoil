package org.cirdles.topsoil.app.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.cirdles.topsoil.app.util.NumberColumnStringConverter.countFractionDigits;

public class NumberColumnStringConverterTest {

    @Test
    public void countFractionDigits_test() {
        assertEquals(3, countFractionDigits(3.333));
    }

    @Test
    public void countFractionDigits_nullInput_test() {
        assertEquals(-1, countFractionDigits(null));
    }

}
