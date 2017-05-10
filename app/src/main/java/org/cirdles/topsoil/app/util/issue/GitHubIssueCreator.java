package org.cirdles.topsoil.app.util.issue;

import org.cirdles.topsoil.app.browse.WebBrowser;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * An {@link IssueCreator} class which opens the system's default browser to a GitHub issue creation page for Topsoil.
 *
 * @author Benjamin Muldrow
 */
public class GitHubIssueCreator implements IssueCreator {

    //***********************
    // Attributes
    //***********************

    /**
     * The URL to a "New Issue" form for Topsoil's GitHub repository.
     */
    private static final String BASE_URL = "https://github.com/CIRDLES/Topsoil/issues/new?body=";

    /**
     * A {@code WebBrowser} that accesses the system default browser to open the URL to report an issue.
     */
    private final WebBrowser browser;

    /**
     * Used to build part of the issue body.
     */
    private final StringBuilder issueBody;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs an instance of {@code GitHubIssueCreator} using a specified {@code WebBrowser} and
     * {@code StringBuilder}.
     *
     * @param browser   the WebBrowser to open issues in
     * @param issueBody the StringBuilder used to build an auto-generated issue body
     */
    @Inject
    public GitHubIssueCreator(WebBrowser browser, StringBuilder issueBody) {
        this.browser = browser;
        this.issueBody = issueBody;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Encodes a {@code String} of text into {@code UTF-8}.
     *
     * @param text  the String to encode
     * @return  the encoded String
     */
    private String urlEncode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Encodes a {@code String} of text into {@code UTF-8} and appends it to the issue body.
     *
     * @param text to add to issue body
     */
    @Override
    public void println(String text) {
        issueBody.append(urlEncode(text + "\n"));
    }

    /**
     * Navigates the {@link WebBrowser} to a GitHub "New Issue" page for Topsoil's repository.
     */
    @Override
    public void create() {
        browser.browse(BASE_URL + issueBody);
    }

}
