package org.cirdles.topsoil.plot;

import org.cirdles.commons.util.ResourceExtractor;

import java.net.URI;

public final class HTMLTemplate {

    private HTMLTemplate() {}

    public static String withRootDiv() {
        ResourceExtractor re = new ResourceExtractor(HTMLTemplate.class);
        URI topsoilURI = re.extractResourceAsPath("topsoil.js").toUri();
        return (""
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<style>\n"
                + "body {\n"
                + "  width: 100%;\n"
                + "  height: 100vh;\n"
                + "  overflow: hidden;\n"
                + "}\n"
                + "#root {\n"
                + "  width: 100%;\n"
                + "  height: 100%;\n"
                + "  overflow: hidden;\n"
                + "}\n"
                + "</style>\n"
                + "</head>\n"
                + "<body>\n"
                + "  <script src=\"" + topsoilURI + "\"></script>\n"
                + "  <div id=\"root\" />\n"
                + "</body>\n"
                + "</html>"
                + "").replaceAll("%20", "%%20");
    }

}
