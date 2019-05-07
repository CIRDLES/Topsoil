package org.cirdles.topsoil.app.control.plot.panel;

import javafx.event.Event;
import javafx.event.EventType;
import org.cirdles.topsoil.plot.PlotProperties;

class PropertyChangeEvent extends Event {

    public final static EventType<PropertyChangeEvent> PROPERTY_CHANGED = new EventType<>("PROPERTY_CHANGED");

    private PlotProperties.Property<?> property;
    private Object oldValue;
    private Object newValue;

    PropertyChangeEvent(PlotProperties.Property<?> property, Object oldValue, Object newValue) {
        super(PROPERTY_CHANGED);

        this.property = property;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public PlotProperties.Property<?> getProperty() {
        return property;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}
