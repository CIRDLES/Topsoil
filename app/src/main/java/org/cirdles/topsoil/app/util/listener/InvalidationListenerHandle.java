package org.cirdles.topsoil.app.util.listener;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 * @author Jake Marotta
 */
public class InvalidationListenerHandle implements ListenerHandle {

    private Observable observable;
    private InvalidationListener listener;

    InvalidationListenerHandle(Observable observable, InvalidationListener listener) {
        this.observable = observable;
        this.listener = listener;
    }

    @Override
    public void attach() {
        observable.addListener(listener);
    }

    @Override
    public void detach() {
        observable.removeListener(listener);
    }

    @Override
    public Observable getObservable() {
        return observable;
    }

}
