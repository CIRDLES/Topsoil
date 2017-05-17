package org.cirdles.topsoil.app.util.issue;

import org.cirdles.topsoil.app.browse.WebBrowser;
import org.cirdles.topsoil.app.metadata.ApplicationMetadata;

import javax.inject.Inject;
import java.util.Properties;

/**
 * A class which opens the system's default browser to a GitHub issue creation page for Topsoil, automatically
 * inserting information about the user's system and their version of Topsoil.
 *
 * @author Benjamin Muldrow
 */
public class StandardGitHubIssueCreator extends GitHubIssueCreator {

    //***********************
    // Attributes
    //***********************

    /**
     * Topsoil metadata.
     */
    private final ApplicationMetadata metadata;

    /**
     * Properties of the host system, including operating system and Java version.
     */
    private final Properties systemProperties;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs an instance of StandardGitHubIssueCreator with the specified {@code ApplicationMetadata} (for getting
     * the version of Topsoil), {@code Properties} (for getting system properties e.g. Java version, operating system),
     * {@code WebBrowser} (for opening a GitHub "New Issue" page), and {@code StringBuilder} (for building the body of
     * the GitHub issue).
     *
     * @param metadata  an ApplicationMetadata containing Topsoil version information
     * @param systemProperties  Properties containing Java version and operating system
     * @param browser   a WebBrowser referencing the system's default browser
     * @param issueBody a StringBuilder for building the body of a GitHub issue
     */
    @Inject
    public StandardGitHubIssueCreator(ApplicationMetadata metadata, Properties systemProperties, WebBrowser browser,
                                      StringBuilder issueBody) {
        super(browser, issueBody);
        this.systemProperties = systemProperties;
        this.metadata = metadata;

        printHeader();
    }

    //***********************
    // Methods
    //***********************

    /**
     * Appends version and system information to the issue body, automatically creating a header for the GitHub issue
     * containing information useful to developers.
     */
    private void printHeader() {

        String topsoilVersion = metadata.getVersion();
        String javaVersion = systemProperties.getProperty("java.version");
        String operatingSystem = systemProperties.getProperty("os.name") + " " + systemProperties.getProperty("os.version");

        println("Topsoil Version: " + topsoilVersion);
        println("Java Version: " + javaVersion);
        println("Operating System: " + operatingSystem);
        println("***");

    }

}
