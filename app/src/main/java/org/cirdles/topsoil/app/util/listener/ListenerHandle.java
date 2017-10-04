package org.cirdles.topsoil.app.util.listener;

import javafx.beans.Observable;

/**
 * @author Jake Marotta
 */
public interface ListenerHandle {

    void attach();

    void detach();

    Observable getObservable();

}
