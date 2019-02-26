package org.cirdles.topsoil.app.data.column;

public class StringColumn extends DataColumn<String> {

    private static final long serialVersionUID = -3725445157596494339L;

    public StringColumn(String label) {
        super(label, String.class, new SerializableStringConverter());
    }

}
