package org.cirdles.topsoil.plot;

import org.cirdles.commons.util.ResourceExtractor;

import java.net.URI;
import java.nio.file.Path;

public final class HTMLTemplate {

    private static final String TEMPLATE;
    static {
        ResourceExtractor re = new ResourceExtractor(HTMLTemplate.class);

        URI d3URI = re.extractResourceAsPath("d3.min.js").toUri();
        URI numericURI = re.extractResourceAsPath("numeric.min.js").toUri();
        URI topsoilURI = re.extractResourceAsPath("topsoil.js").toUri();

        TEMPLATE = (""
                + "<!DOCTYPE html>\n"
                // <html>
                // <head>
                + "<style>\n"
                + "body {\n"
                + "  margin: 0; padding: 0;\n"
                + "  overflow: hidden;\n"
                + "}\n"
                + "</style>\n"
                // </head>
                + "<body>"
                + "<script src=\"" + d3URI + "\"></script>\n"
                + "<script src=\"" + numericURI + "\"></script>\n"
                + "<script src=\"" + topsoilURI + "\"></script>\n"
                + "<script src=\"%s\"></script>\n" // JS file for plot
                + "</body>"
                // </html>
                + "").replaceAll("%20", "%%20");
    }

    private HTMLTemplate() {}

    public static String forPlotType(PlotType plotType) {

        ResourceExtractor re = new ResourceExtractor(HTMLTemplate.class);

        // Append plotType-specific scripts to HTML
        StringBuilder resourceFiles = new StringBuilder();
        Path path;
        for (String resource : plotType.getResourceFiles()) {
            path = re.extractResourceAsPath(resource);
            if (path == null) {
                throw new RuntimeException(plotType.getName() + " resource not found: " + resource);
            }
            resourceFiles.append("<script src=\"");
            resourceFiles.append(re.extractResourceAsPath(resource).toUri().toString());
            resourceFiles.append("\"></script>\n");
        }

        String plotScript = re.extractResourceAsPath(plotType.getPlotFile()).toUri().toString();
        return String.format(TEMPLATE, plotScript)
                .concat(resourceFiles.toString());
    }

}
