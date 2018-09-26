package org.cirdles.topsoil.app.style;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.util.TopsoilException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A utility class for providing a {@code Node} with Topsoil's custom stylesheets.
 *
 * @author marottajb
 */
public class CSSLoader {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String MAIN_CSS = "topsoil.css";

    private static final String SPREADSHEET_CSS = "spreadsheet.css";
    private static final String TEMP_SPREADSHEET_CSS = "temp-spreadsheet-css.tmp";
    private static final String ON_PICKER = "on-picker2.png";
    private static final String OFF_PICKER = "off-picker2.png";

    private final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(CSSLoader.class);

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final Map<String, String> stylesheets = new HashMap<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public CSSLoader() {
        read();
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public List<String> getStylesheets() {
        return new ArrayList<>(stylesheets.values());
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void read() {
        if (! stylesheets.containsKey(MAIN_CSS)) {
            String mcss = mainCSS();
            if (mcss != null) {
                stylesheets.put(MAIN_CSS, mcss);
            }
        }
        if (! stylesheets.containsKey(SPREADSHEET_CSS)) {
            String scss = spreadsheetCSS();
            if (scss != null) {
                stylesheets.put(SPREADSHEET_CSS, scss);
            }
        }
    }

    private String mainCSS() {
        String result = null;
        try {
            Path path = RESOURCE_EXTRACTOR.extractResourceAsPath(MAIN_CSS);
            result = path.toUri().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            new TopsoilException("Could not find file: " + MAIN_CSS, e).printStackTrace();
        }
        return result;
    }

    private String spreadsheetCSS() {
        String result = null;
        try {
            Path cssPath = RESOURCE_EXTRACTOR.extractResourceAsPath(SPREADSHEET_CSS);
            Path newPath = cssPath.getParent().resolve(TEMP_SPREADSHEET_CSS);
            Path onPath = RESOURCE_EXTRACTOR.extractResourceAsPath(ON_PICKER);
            Path offPath = RESOURCE_EXTRACTOR.extractResourceAsPath(OFF_PICKER);

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
                                   .replaceAll(ON_PICKER, onPath.toUri().toURL().toString())
                                   .replaceAll(OFF_PICKER, offPath.toUri().toURL().toString());
            writer.write(content);
            writer.close();

            result = newPath.toUri().toURL().toExternalForm();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
