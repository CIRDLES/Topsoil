package org.cirdles.topsoil.symbols;

import java.io.Serializable;
import java.util.function.Function;

public class SimpleSymbolKey<T> implements SymbolKey<T>, Serializable {

    private String title;
    private String fieldName;
    private T defaultValue;
    private Class<T> valueType;
    private Function<T, Object> valueConverter;

    public SimpleSymbolKey(String prettyTitle, String fieldName, T defaultValue, Class<T> valueType) {
        this(prettyTitle, fieldName, defaultValue, valueType, null);
    }

    public SimpleSymbolKey(
            String prettyTitle,
            String fieldName,
            T defaultValue,
            Class<T> valueType,
            Function<T, Object> valueToJSONConverter) {
        this.title = prettyTitle;
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
        this.valueType = valueType;
        this.valueConverter = valueToJSONConverter;
    }

    @Override
    public Object getJSONCompatibleValue(Object obj) {
        if (match(obj) && valueConverter != null) {
            return valueConverter.apply((T) obj);
        }
        return SymbolKey.super.getJSONCompatibleValue(obj);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Class<T> getType() {
        return valueType;
    }

    @Override
    public String toJSONString() {
        return fieldName;
    }
}
