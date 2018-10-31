package org.cirdles.topsoil.app.util;

/**
 * @author marottajb
 */
public abstract class ListenerHandlerBase<T> implements ListenerHandler<T> {

    /*
        @TODO Do this better, probably
     */

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    abstract public void listen(T item);

    abstract public void forget(T item);

}
