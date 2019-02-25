package org.cirdles.topsoil.app.style;

import org.cirdles.commons.util.ResourceExtractor;

import java.net.MalformedURLException;
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

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(StyleLoader.class);
    private final List<String> stylesheets = new ArrayList<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public StyleLoader() {
        Path mainCSS = mainCSS();
        if (mainCSS != null) {
            stylesheets.add(getStylesheet(mainCSS));
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

    private String getStylesheet(Path path) {
        String stylesheet = null;
        try {
            if (path != null) {
                stylesheet = path.toUri().toURL().toExternalForm();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return stylesheet;
    }
}
