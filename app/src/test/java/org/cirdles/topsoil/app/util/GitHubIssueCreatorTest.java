package org.cirdles.topsoil.app.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.cirdles.topsoil.app.browse.WebBrowser;
import org.cirdles.topsoil.app.util.issue.GitHubIssueCreator;
import org.cirdles.topsoil.app.util.issue.IssueCreator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by Benjam on 1/29/2016.
 */
public class GitHubIssueCreatorTest {

    @Rule
    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private WebBrowser browser;

    private StringBuilder issueBody;
    private IssueCreator issueCreator;

    @Before
    public void setUp() {
        issueBody = new StringBuilder();
        issueCreator = new GitHubIssueCreator(browser, issueBody);
    }

    @Test
    public void testPrintln() {
        issueCreator.println("Hello");
        assertThat(issueBody).hasToString("Hello%0A");
    }

    @Test
    public void testPrintlnWithSpaces() {
        issueCreator.println("Hello World");
        assertThat(issueBody).hasToString("Hello+World%0A");
    }

    @Test
    public void testCreate() {
        issueCreator.println("Hello");
        issueCreator.create();

        verify(browser).browse("https://github.com/CIRDLES/Topsoil/issues/new?body=Hello%0A");
    }

}
