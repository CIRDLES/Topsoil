package org.cirdles.topsoil;

/**
 * Defines lambda constants.
 *
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

    /**
     * Returns the readable title of this {@code Lambda} value.
     *
     * @return  String title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the abbreviation of this {@code Lambda} value.
     *
     * @return  String abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Returns the default value of this {@code Lambda} value.
     *
     * @return  default Number value
     */
    public Number getDefaultValue() {
        return defaultValue;
    }

}