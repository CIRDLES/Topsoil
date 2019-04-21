package org.cirdles.topsoil.constant;

/**
 * @author marottajb
 */
public interface Constant<T> {

    String getTitle();

    T getValue();

    void setValue(T value);

    void resetToDefault();

}
