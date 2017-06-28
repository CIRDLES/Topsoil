package org.cirdles.topsoil.app.util.listener;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * @author Jake Marotta
 */
public class ListChangeListenerHandle<E> implements ListenerHandle {

    private ObservableList<E> observable;
    private ListChangeListener<? super E> listener;

    ListChangeListenerHandle(ObservableList<E> observableValue, ListChangeListener<E> listener) {
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
    public ObservableList<E> getObservable() {
        return observable;
    }

}
