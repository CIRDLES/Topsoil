package org.cirdles.topsoil.app.spreadsheet;

import org.cirdles.topsoil.app.util.ListenerHandler;

/**
 * @author marottajb
 */
public abstract class ChangeHandlerBase<T> implements ListenerHandler<T> {

    /*
        @TODO Do this better, probably
     */

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    abstract public void listen(T item);

    abstract public void forget(T item);

}
