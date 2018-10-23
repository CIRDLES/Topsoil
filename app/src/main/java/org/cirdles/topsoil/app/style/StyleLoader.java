package org.cirdles.topsoil.app.style;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.util.TopsoilException;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A utility class for providing a {@code Node} with Topsoil's custom styles.
 *
 * @author marottajb
 */
public class StyleLoader {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String MAIN_CSS = "topsoil.css";

    private static final String SPREADSHEET_CSS = "spreadsheet.css";
    private static final String TEMP_SPREADSHEET_CSS = "temp-spreadsheet-css.tmp";

    private static final String ON_PICKER = "on-picker2.png";
    private static final String OFF_PICKER = "off-picker2.png";
    private static final String X_PICKER = "x-picker.png";
    private static final String SIGMA_X_PICKER = "sigma-x-picker.png";
    private static final String Y_PICKER = "y-picker.png";
    private static final String SIGMA_Y_PICKER = "sigma-y-picker.png";
    private static final String RHO_PICKER = "rho-picker.png";
    private static final String NO_VAR_PICKER = "no-var-picker.png";

    private final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(StyleLoader.class);

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final List<String> stylesheets = new ArrayList<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public StyleLoader() {
        Path mainCSS = mainCSS();
        if (mainCSS != null) {
            stylesheets.add(getStylesheet(mainCSS));
        }
        String spreadsheetCSS = getStylesheet(spreadsheetCSS());
        if (spreadsheetCSS != null) {
            stylesheets.add(spreadsheetCSS);
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public List<String> getStylesheets() {
        return stylesheets;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private Path mainCSS() {
        return RESOURCE_EXTRACTOR.extractResourceAsPath(MAIN_CSS);
    }

    private Path spreadsheetCSS() {
        try {
            Path cssPath = RESOURCE_EXTRACTOR.extractResourceAsPath(SPREADSHEET_CSS);
            Path newPath = cssPath.getParent().resolve(TEMP_SPREADSHEET_CSS);

            Path onPath = RESOURCE_EXTRACTOR.extractResourceAsPath(ON_PICKER);
            Path offPath = RESOURCE_EXTRACTOR.extractResourceAsPath(OFF_PICKER);
            Path xPath = RESOURCE_EXTRACTOR.extractResourceAsPath(X_PICKER);
            Path sigmaXPath = RESOURCE_EXTRACTOR.extractResourceAsPath(SIGMA_X_PICKER);
            Path yPath = RESOURCE_EXTRACTOR.extractResourceAsPath(Y_PICKER);
            Path sigmaYPath = RESOURCE_EXTRACTOR.extractResourceAsPath(SIGMA_Y_PICKER);
            Path rhoPath = RESOURCE_EXTRACTOR.extractResourceAsPath(RHO_PICKER);
            Path noVarPath = RESOURCE_EXTRACTOR.extractResourceAsPath(NO_VAR_PICKER);

            StringJoiner joiner = new StringJoiner("\n");
            BufferedReader reader = Files.newBufferedReader(cssPath);
            BufferedWriter writer = Files.newBufferedWriter(newPath);

            String line = reader.readLine();
            while (line != null) {
                joiner.add(line);
                line = reader.readLine();
            }
            reader.close();

            String content = joiner.toString()
                                   .replaceAll(ON_PICKER, onPath.toFile().getName())
                                   .replaceAll(OFF_PICKER, offPath.toFile().getName())
                                   .replaceAll(SIGMA_X_PICKER, sigmaXPath.toFile().getName())
                                   .replaceAll(X_PICKER, xPath.toFile().getName())
                                   .replaceAll(SIGMA_Y_PICKER, sigmaYPath.toFile().getName())
                                   .replaceAll(Y_PICKER, yPath.toFile().getName())
                                   .replaceAll(RHO_PICKER, rhoPath.toFile().getName())
                                   .replaceAll(NO_VAR_PICKER, noVarPath.toFile().getName());
            writer.write(content);
            writer.close();

            return newPath;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getStylesheet(Path path) {
        String stylesheet = null;
        try {
            if (path != null) {
                stylesheet = path.toUri().toURL().toExternalForm();
            }
        } catch (MalformedURLException e) {
            new TopsoilException("Unable to load: " + path.toAbsolutePath().toString(), e).printStackTrace();
        }
        return stylesheet;
    }
}
