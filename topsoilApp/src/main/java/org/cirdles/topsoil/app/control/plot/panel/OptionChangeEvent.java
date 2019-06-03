package org.cirdles.topsoil.app.control.plot.panel;

import javafx.event.Event;
import javafx.event.EventType;
import org.cirdles.topsoil.plot.PlotOption;

class OptionChangeEvent<T> extends Event {

    public final static EventType<OptionChangeEvent> OPTION_CHANGED = new EventType<>("OPTION_CHANGED");

    private PlotOption<T> option;
    private T oldValue;
    private T newValue;

    OptionChangeEvent(PlotOption<T> option, T oldValue, T newValue) {
        super(OPTION_CHANGED);

        this.option = option;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public PlotOption<T> getOption() {
        return option;
    }

    public T getOldValue() {
        return oldValue;
    }

    public T getNewValue() {
        return newValue;
    }
}
