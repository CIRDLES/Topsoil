package org.cirdles.topsoil.app.util;

/**
 * @author Jake Marotta
 */
public class TopsoilException extends Exception {

    private TopsoilException() {
    }

    public TopsoilException(String message) {
        super(message);
    }

    public TopsoilException(Throwable cause) {
        super(cause);
    }

    public TopsoilException(String message, Throwable cause) {
        super(message, cause);
    }

    public TopsoilException(String message, Throwable cause,
                          boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
