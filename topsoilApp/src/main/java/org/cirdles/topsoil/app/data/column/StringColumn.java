package org.cirdles.topsoil.app.data.column;

import javafx.util.converter.DefaultStringConverter;

public class StringColumn extends DataColumn<String> {

    public StringColumn(String label) {
        super(label, String.class, new DefaultStringConverter());
    }

}
