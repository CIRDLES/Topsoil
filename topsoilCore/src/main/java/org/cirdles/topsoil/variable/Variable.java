package org.cirdles.topsoil.variable;

import org.cirdles.topsoil.constant.ParameterizedConstant;

public class Variable<T> extends ParameterizedConstant<T> {

    private String abbreviation;

    Variable(String name, String abbreviation, String key, T defaultValue) {
        super(name, key, defaultValue);
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
