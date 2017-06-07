package org.cirdles.topsoil.plot;

/**
 * Intended to be added as a member to a JavaScriptPlot, so that the JavaScript may make upcalls to JavaFX.
 *
 * @author Emily Coleman
 */
public class JavaScriptBridge {

    public void println(String s) {
            System.out.println(s);
        }

}
