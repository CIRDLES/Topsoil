package org.cirdles.topsoil.app.util;

/**
 * Created by Benjam on 11/20/2015.
 */
public interface IssueCreator {

    /**
     * Appends a line of text to the issue body.
     * @param text
     */
    void println(String text);

    /**
     * Creates the issue.
     */
    void create();

}
