package org.cirdles.topsoil.app.util;

import org.cirdles.topsoil.app.browse.WebBrowser;
import org.cirdles.topsoil.app.metadata.ApplicationMetadata;

import javax.inject.Inject;
import java.util.Properties;

/**
 * Created by Benjam on 11/20/2015.
 */
public class StandardGitHubIssueCreator extends GitHubIssueCreator {

    private final ApplicationMetadata metadata;
    private final Properties systemProperties;

    @Inject
    public StandardGitHubIssueCreator(
            ApplicationMetadata metadata,
            Properties systemProperties,
            WebBrowser browser,
            StringBuilder issueBody) {
        super(browser, issueBody);
        this.systemProperties = systemProperties;
        this.metadata = metadata;

        printHeader();
    }

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
