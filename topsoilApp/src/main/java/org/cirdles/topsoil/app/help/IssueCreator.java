package org.cirdles.topsoil.app.help;

/**
 * An interface implemented by classes which create issues for external logs.
 *
 * @author Benjamin Muldrow
 */
public interface IssueCreator {

    /**
     * Appends a line of text to the issue body.
     *
     * @param text to add to issue body
     */
    void println(String text);

    /**
     * Creates the issue.
     */
    void create();

}
