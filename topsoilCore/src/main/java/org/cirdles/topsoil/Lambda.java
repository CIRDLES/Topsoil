package org.cirdles.topsoil;

/**
 * @author marottajb
 */
public enum Lambda {

    U234("Lambda 234", "234", 2.82206e-6),
    U235("Lambda 235", "235", 9.8485e-10),
    U238("Lambda 238", "238", 1.55125e-10),

    Th230("Lambda 230", "230", 9.1705e-6);

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final String title;
    private final String abbreviation;
    private final Number defaultValue;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    Lambda(String title, String abbreviation, Number defaultValue) {
        this.title = title;
        this.abbreviation = abbreviation;
        this.defaultValue = defaultValue;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//


    public String getTitle() {
        return title;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public Number getDefaultValue() {
        return defaultValue;
    }

}