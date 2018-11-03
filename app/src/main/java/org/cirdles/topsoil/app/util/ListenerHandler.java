package org.cirdles.topsoil.app.util;

/**
 * A {@code ListenerHandler} facilitates the listening of a setValue of objects so that the same or similar action
 * must be performed upon any change, and so that objects can be added or removed from the setValue. For example, any time a
 * cell value is modified in a spreadsheet, that spreadsheet column has to be re-formatted. In addition, if a cell is
 * no longer in the spreadsheet, we don't want to waste the resources listening to it. The cells can be added via
 * {@code listen()} and removed via {@code forget()}.
 *
 * @author marottajb
 */
public interface ListenerHandler<T> {

    /**
     * Starts the listening on the provided item.
     *
     * @param   item
     *          T to listen to
     */
    void listen(T item);

    /**
     * Stops the listening on the provided item.
     *
     * @param   item
     *          T to no longer listen to
     */
    void forget(T item);

}
