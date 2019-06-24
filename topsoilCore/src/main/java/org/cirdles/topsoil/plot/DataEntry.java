package org.cirdles.topsoil.plot;

import org.cirdles.topsoil.Variable;
import org.cirdles.topsoil.symbols.SimpleSymbolMap;

import java.util.Map;

public class DataEntry extends SimpleSymbolMap<Variable<?>> {

    public DataEntry() {
        super();
    }

    public DataEntry(Map<Variable<?>, Object> map) {
        super(map);
    }

}
