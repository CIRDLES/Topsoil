package org.cirdles.topsoil.plot.bridges;

/**
 * Intended to be added as a member to a JavaScriptPlot, so that the JavaScript may make upcalls to JavaFX.
 *
 * @author Emily Coleman
 */
public class JavaScriptBridge {

    /**
     * Prints a string to System.out.
     *
     * @param s String
     */
    public void println(String s) {
            System.out.println(s);
        }

}
