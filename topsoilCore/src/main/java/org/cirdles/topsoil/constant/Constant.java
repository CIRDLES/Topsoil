package org.cirdles.topsoil.constant;

/**
 * @author marottajb
 */
public interface Constant<T> {

    String getTitle();

    String getAbbreviation();

    T getValue();

    void setValue(T value);

    void resetToDefault();

}
