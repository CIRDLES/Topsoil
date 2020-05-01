package org.cirdles.topsoil.app.control.plot.panel;

import javafx.event.Event;
import javafx.event.EventType;

class StyleImportEvent extends Event {

    public final static EventType<StyleImportEvent> STYLE_IMPORT = new EventType<>("STYLE_IMPORT");

    private String fileName;

    public String getFileName() {
        return fileName;
    }

    StyleImportEvent(String fileName) {
        super(STYLE_IMPORT);
        this.fileName = fileName;
    }

}
