package org.cirdles.topsoil.constant;

/**
 * @author marottajb
 */
public interface Constant<T> {

    String getTitle();

    String getAbbreviation();

    T getValue();

    void resetToDefault();

}
