package org.cirdles.topsoil.app.data.column;

public class NumberColumn extends DataColumn<Number> {

    public NumberColumn(String label) {
        super(label, Number.class, new NumberColumnStringConverter());
    }

    @Override
    public NumberColumnStringConverter getStringConverter() {
        return (NumberColumnStringConverter) converter;
    }

}
