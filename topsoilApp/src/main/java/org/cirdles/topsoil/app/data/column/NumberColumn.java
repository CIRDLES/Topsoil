package org.cirdles.topsoil.app.data.column;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class NumberColumn extends DataColumn<Number> {

    private static final long serialVersionUID = 7085133610326416507L;

    public NumberColumn(String label) {
        super(label, Number.class, new NumberColumnStringConverter());
    }

    @Override
    public NumberColumnStringConverter getStringConverter() {
        return (NumberColumnStringConverter) converter;
    }

}
