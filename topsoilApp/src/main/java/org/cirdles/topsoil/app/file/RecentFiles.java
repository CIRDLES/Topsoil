package org.cirdles.topsoil.app.file;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Utility methods for setting/retrieving a list of most recently used files.
 */
public final class RecentFiles {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String RECENT_FILES = "recent-files";
    private static final int MAX_SIZE = 10;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private RecentFiles() {
        // Prevents instantiation by default constructor
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Returns an array containing the most recent project file paths.
     *
     * @return  Path[]
     */
    public static Path[] getPaths() {
        return loadRecentFiles().toArray(new Path[]{});
    }

    /**
     * Adds a path to the list of most recent project files.
     *
     * @param path  project file Path
     */
    public static void addPath(Path path) {
        List<Path> paths = loadRecentFiles();
        paths.remove(path);
        if (paths.size() == MAX_SIZE) {
            paths.remove(MAX_SIZE - 1);
        }
        paths.add(0, path);
        saveRecentFiles(paths);
    }

    /**
     * Clears the list of most recent files.
     */
    public static void clear() {
        Preferences prefs = Preferences.userNodeForPackage(RecentFiles.class);
        for (int i = 1; i <= MAX_SIZE; i++) {
            prefs.remove(RECENT_FILES + i);
        }
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /**
     * Reads a list of most recent paths from the {@code Preferences} node.
     *
     * @return  List of Paths
     */
    private static List<Path> loadRecentFiles() {
        String str;
        Preferences prefs = Preferences.userNodeForPackage(RecentFiles.class);
        List<Path> paths = new ArrayList<>(MAX_SIZE);
        for (int i = 1; i <= MAX_SIZE; i++) {
            str = prefs.get(RECENT_FILES + i, null);
            if (str != null) {
                Path path = Paths.get(str);
                if (path.toFile().exists()) {
                    paths.add(path);
                }
            }
        }
        return paths;
    }

    /**
     * Updates the paths stored in the {@code Preferences} node with the provided list of paths.
     *
     * @param paths List of Paths
     */
    private static void saveRecentFiles(List<Path> paths) {
        Preferences prefs = Preferences.userNodeForPackage(RecentFiles.class);
        String str;
        for (int i = 1; i <= MAX_SIZE; i++) {
            if (i > paths.size()) {
                prefs.remove(RECENT_FILES + i);
            } else {
                str = paths.get(i - 1).toString();
                prefs.put(RECENT_FILES + i, str);
            }
        }
    }
}
