package org.cirdles.topsoil.app.util.listener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @author Jake Marotta
 */
public class ChangeListenerHandle<T> implements ListenerHandle {

    private ObservableValue observable;
    private ChangeListener<T> listener;

    ChangeListenerHandle(ObservableValue<T> observableValue, ChangeListener<T> listener) {
        this.observable = observableValue;
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
    public ObservableValue getObservable() {
        return observable;
    }

}
