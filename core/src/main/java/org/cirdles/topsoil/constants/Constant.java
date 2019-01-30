package org.cirdles.topsoil.constants;

/**
 * @author marottajb
 */
public interface Constant<T> {

    String getTitle();

    String getAbbreviation();

    T getValue();

    void resetToDefault();

}
