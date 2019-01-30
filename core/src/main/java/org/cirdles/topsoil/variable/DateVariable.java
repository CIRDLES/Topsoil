package org.cirdles.topsoil.variable;

import java.util.Date;

/**
 * @author marottajb
 */
public enum DateVariable implements Variable<Date> {

    TIMESTAMP("Timestamp", "Time");

    private String name;
    private String abbr;

    DateVariable(String name, String abbreviation) {
        this.name = name;
        this.abbr = abbreviation;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAbbreviation() {
        return abbr;
    }

}
