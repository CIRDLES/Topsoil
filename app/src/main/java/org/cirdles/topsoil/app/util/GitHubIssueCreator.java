package org.cirdles.topsoil.app.util;

import org.cirdles.topsoil.app.browse.WebBrowser;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Benjam on 11/20/2015.
 */
public class GitHubIssueCreator implements IssueCreator {

    private static final String BASE_URL = "https://github.com/CIRDLES/Topsoil/issues/new?body=";

    private final WebBrowser browser;
    private final StringBuilder issueBody;

    @Inject
    public GitHubIssueCreator(WebBrowser browser, StringBuilder issueBody) {
        this.browser = browser;
        this.issueBody = issueBody;
    }

    private String urlEncode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void println(String text) {
        issueBody.append(urlEncode(text + "\n"));
    }

    @Override
    public void create() {
        browser.browse(BASE_URL + issueBody);
    }

}
