package org.cirdles.topsoil.app.help;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.cirdles.topsoil.app.browse.WebBrowser;
import org.cirdles.topsoil.app.help.IssueCreator;
import org.cirdles.topsoil.app.help.StandardGitHubIssueCreator;
import org.cirdles.topsoil.app.metadata.ApplicationMetadata;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Properties;

/**
 * Created by Benjam on 2/1/2016.
 */
public class StandardGitHubIssueCreatorTest {

    @Rule
    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ApplicationMetadata metadata;

    @Mock
    private Properties systemProperties;

    @Mock
    private WebBrowser browser;

    private StringBuilder issueBody;
    private IssueCreator issueCreator;

    @Before
    public void setUp() {
        issueBody = new StringBuilder();

        when(systemProperties.getProperty("java.version")).thenReturn("1.8.0_60-b18");
        when(metadata.getVersion()).thenReturn("v1.0.0-alpha.7");
        when(systemProperties.getProperty("os.name")).thenReturn("Windows");
        when(systemProperties.getProperty("os.version")).thenReturn("10.1");
    }

    private IssueCreator createIssueCreator() {
        return new StandardGitHubIssueCreator(metadata, systemProperties, browser, issueBody);
    }

    @Test
    public void testPrintsJavaVersion() {
        issueCreator = createIssueCreator();
        assertThat(issueBody.toString()).contains("1.8.0_60-b18");
    }

    @Test
    public void testPrintTopsoilVersion() {
        issueCreator = createIssueCreator();
        assertThat(issueBody.toString()).contains("v1.0.0-alpha.7");
    }

    @Test
    public void testPrintOS() {
        issueCreator = createIssueCreator();
        assertThat(issueBody.toString()).contains("Windows+10.1");
    }

    @Test
    public void testPrintHeader() {
        String issueUrl = ""
                + "https://github.com/CIRDLES/Topsoil/issues/new?body="         // Base URL
                + "Topsoil+Version%3A+v1.0.0-alpha.7%0A"                        // Topsoil Version
                + "Java+Version%3A+1.8.0_60-b18%0A"                             // Java Version
                + "Operating+System%3A+Windows+10.1%0A"                         // OS Name
                + "***%0A";                                                     // Markup Line

        issueCreator = createIssueCreator();
        issueCreator.create();

        verify(browser).browse(issueUrl);
    }

}
