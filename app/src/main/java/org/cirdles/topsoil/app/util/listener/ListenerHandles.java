package org.cirdles.topsoil.app.util.listener;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * @author Jake Marotta
 */
public class ListenerHandles {

    private ListenerHandles() {
        //
    }

    public static <T> ListenerHandle createAttached(ObservableValue<T> observable, ChangeListener<T> listener) {
        ListenerHandle handle = new ChangeListenerHandle<>(observable, listener);
        handle.attach();
        return handle;
    }

    public static <T> ListenerHandle createAttached(Observable observable, InvalidationListener listener) {
        ListenerHandle handle = new InvalidationListenerHandle(observable, listener);
        handle.attach();
        return handle;
    }

    public static <T> ListenerHandle createAttached(ObservableList<T> observable, ListChangeListener<T> listener) {
        ListenerHandle handle = new ListChangeListenerHandle<>(observable, listener);
        handle.attach();
        return handle;
    }

    public static <T> ListenerHandle createDetached(ObservableValue<T> observable, ChangeListener<T> listener) {
        return new ChangeListenerHandle<>(observable, listener);
    }
    public static <T> ListenerHandle createDetached(ObservableList<T> observable, ListChangeListener<T> listener) {
        return new ListChangeListenerHandle<>(observable, listener);
    }

    public static <T> ListenerHandle createDetached(Observable observable, InvalidationListener listener) {
        return new InvalidationListenerHandle(observable, listener);
    }

}
