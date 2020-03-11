package org.cirdles.topsoil.exception;

import java.util.StringJoiner;

public class MissingImplementationException extends UnsupportedOperationException {

    private static final long serialVersionUID = 4318514297209142719L;

    private static String joinArguments(Class[] arguments) {
        StringJoiner joiner = new StringJoiner(", ");
        for (Class argClass : arguments) {
            joiner.add(argClass.getSimpleName());
        }
        return joiner.toString();
    }

    private Class<?> targetClass;
    private Type type;
    private Class[] args;

    public MissingImplementationException(Throwable cause, Class<?> targetClass, Type type, Class... arguments) {
        super(
                "Expected public "
                + type.getName().toLowerCase()
                + " on instance of "
                + targetClass.getSimpleName()
                + " with arguments: "
                + joinArguments(arguments),
                cause
        );
        this.targetClass = targetClass;
        this.type = type;
        this.args = arguments;
    }

    public Type getType() {
        return type;
    }

    public Class[] getArguments() {
        return args;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public enum Type {
        CONSTRUCTOR("Constructor"),
        METHOD("Method");

        private String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
