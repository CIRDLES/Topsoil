package org.cirdles.topsoil.app.file;

import org.cirdles.topsoil.utils.TopsoilPersistentState;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Utility methods for setting/retrieving a list of most recently used files.
 * Some contexts support a list of MRU files, while others only support getting one.
 */
public final class RecentFiles {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String RECENT_PROJECTS = "recent-files";
    private static final String RECENT_EXPORTS = "recent-exports";
    private static final String RECENT_PLOT_STYLES = "recent-plot-styles";

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

    // PUBLIC PROJECT MRU METHODS
    /**
     * Returns an array containing the most recent project file paths.
     *
     * @return  Path[]
     */
    public static Path[] getProjectPaths() {
        return loadRecentProjectFiles().toArray(new Path[]{});
    }

    /**
     * Adds a path to the list of most recent project files.
     *
     * @param path  project file Path
     */
    public static void addProjectPath(Path path) {
        List<Path> paths = loadRecentProjectFiles();
        paths.remove(path);
        if (paths.size() == MAX_SIZE) {
            paths.remove(MAX_SIZE - 1);
        }
        paths.add(0, path);
        saveRecentProjectFiles(paths);
    }

    /**
     * Clears the list of most recent files.
     */
    public static void clearProjectPaths() {
        Preferences prefs = Preferences.userNodeForPackage(RecentFiles.class);
        for (int i = 1; i <= MAX_SIZE; i++) {
            prefs.remove(RECENT_PROJECTS + i);
        }
    }

    /**
     * Returns the path of the directory containing the most recently used project files.
     * @return Path
     */
    public static Path findMRUProjectFolder() {
        Path path;
        Path[] recentlyUsed = getProjectPaths();
        if (recentlyUsed.length == 0) {
            path = Paths.get(System.getProperty("user.home"));
        }
        else {
            path = Paths.get(recentlyUsed[0].toUri()).getParent();
        }
        return path;
    }


    // PUBLIC EXPORT MRU METHODS
    /**
     * Returns an array containing the most recently exported table paths.
     *
     * @return Path[]
     */
    public static Path[] getExportPaths() {
        return loadRecentExportFiles().toArray(new Path[]{});
    }

    /**
     * Adds a path to the list of most recently exported table paths.
     *
     * @param path table path
     */
    public static void addExportPath(Path path){
        List<Path> paths = loadRecentExportFiles();
        paths.remove(path);
        if (paths.size() == MAX_SIZE) {
            paths.remove(MAX_SIZE - 1);
        }
        paths.add(0, path);
        saveRecentExportFiles(paths);
    }

    /**
     * Clears the list of most recently exported table paths.
     */
    public static void clearExportPaths() {
        Preferences prefs = Preferences.userNodeForPackage(RecentFiles.class);
        for (int i = 1; i <= MAX_SIZE; i++) {
            prefs.remove(RECENT_EXPORTS + i);
        }
    }

    /**
     * Returns the path of the directory containing the most recently exported table.
     * @return Path
      */
    public static Path findMRUExportFolder() {
        //THIS IS NOT ACCURATE - We need to find a way of storing recently exported tables
        Path path;
        Path[] recentlyUsed = getExportPaths();
        if (recentlyUsed.length == 0) {
            path = Paths.get(System.getProperty("user.home"));
        }
        else {
            path = Paths.get(recentlyUsed[0].toUri()).getParent();
        }
        return path;
    }


    // PUBLIC PLOT STYLE MRU METHODS
    /**
     * Returns the most recently accessed plot style path.
     *
     * @return Path[]
     */
    public static Path getPlotStylePath() {
        Path path;
        if (loadRecentPlotStyleFiles().size() == 0){
            path = null;
        } else {
            path = loadRecentPlotStyleFiles().get(0);
        }
        return path;
    }

    /**
     * Replaces the most recently accessed plot style path.
     *
     * @param path table path
     */
    public static void addPlotStylePath(Path path){
        List<Path> paths = loadRecentPlotStyleFiles();
        if (loadRecentPlotStyleFiles().size() != 0) {
            paths.remove(0);
        }
        paths.add(0, path);
        saveRecentPlotStyleFiles(paths);
    }

    /**
     * Clears the most recent plot style paths.
     */
    public static void clearPlotStylePaths() {
        Preferences prefs = Preferences.userNodeForPackage(RecentFiles.class);
        prefs.remove(RECENT_PLOT_STYLES);
    }

    /**
     * Returns the path of the directory containing the most recently accessed plot style.
     * @return Path
     */
    public static Path findMRUPlotStyleFolder() {
        Path path;
        Path recentlyUsed = getPlotStylePath();
        if (recentlyUsed == null) {
            path = TopsoilPersistentState.getTopsoilUserData();
        }
        else if (recentlyUsed.getFileName().equals(TopsoilPersistentState.getTopsoilUserData().getFileName())) {
            path = Paths.get(recentlyUsed.toUri());
        }
        else {
            path = Paths.get(recentlyUsed.toUri()).getParent();
        }
        return path;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    // PRIVATE PROJECT MRU METHODS
    /**
     * Reads a list of most recent Project paths from the {@code Preferences} node.
     *
     * @return  List of Paths
     */
    private static List<Path> loadRecentProjectFiles() {
        String str;
        Preferences prefs = Preferences.userNodeForPackage(RecentFiles.class);
        List<Path> paths = new ArrayList<>(MAX_SIZE);
        for (int i = 1; i <= MAX_SIZE; i++) {
            str = prefs.get(RECENT_PROJECTS + i, null);
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
     * Updates the Project paths stored in the {@code Preferences} node with the provided list of paths.
     *
     * @param paths List of Paths
     */
    private static void saveRecentProjectFiles(List<Path> paths) {
        Preferences prefs = Preferences.userNodeForPackage(RecentFiles.class);
        String str;
        for (int i = 1; i <= MAX_SIZE; i++) {
            if (i > paths.size()) {
                prefs.remove(RECENT_PROJECTS + i);
            } else {
                str = paths.get(i - 1).toString();
                prefs.put(RECENT_PROJECTS + i, str);
            }
        }
    }


    // PRIVATE EXPORT MRU METHODS
    /**
     * Reads a list of most recent Export paths from the {@code Preferences} node.
     *
     * @return  List of Paths
     */
    private static List<Path> loadRecentExportFiles() {
        String str;
        Preferences prefs = Preferences.userNodeForPackage(RecentFiles.class);
        List<Path> paths = new ArrayList<>(MAX_SIZE);
        for (int i = 1; i <= MAX_SIZE; i++) {
            str = prefs.get(RECENT_EXPORTS + i, null);
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
     * Updates the Export paths stored in the {@code Preferences} node with the provided list of paths.
     *
     * @param paths List of Paths
     */
    private static void saveRecentExportFiles(List<Path> paths) {
        Preferences prefs = Preferences.userNodeForPackage(RecentFiles.class);
        String str;
        for (int i = 1; i <= MAX_SIZE; i++) {
            if (i > paths.size()) {
                prefs.remove(RECENT_EXPORTS + i);
            } else {
                str = paths.get(i - 1).toString();
                prefs.put(RECENT_EXPORTS + i, str);
            }
        }}


    // PRIVATE PLOT STYLE MRU METHODS
    /**
     * Reads a the most recent plot style paths from the {@code Preferences} node.
     *
     * @return  List containing one Path
     */
    private static List<Path> loadRecentPlotStyleFiles() {
        String str;
        Preferences prefs = Preferences.userNodeForPackage(RecentFiles.class);
        List<Path> paths = new ArrayList<>(1);
        str = prefs.get(RECENT_PLOT_STYLES, null);
        if (str != null) {
            Path path = Paths.get(str);
            if (path.toFile().exists()) {
                paths.add(path);
            }
        }
        return paths;
    }

    /**
     * Updates the plot style path stored in the {@code Preferences} node with the provided of path.
     *
     * @param paths List containing one Path
     */
    private static void saveRecentPlotStyleFiles(List<Path> paths) {
        Preferences prefs = Preferences.userNodeForPackage(RecentFiles.class);
        String str;
        prefs.remove(RECENT_PLOT_STYLES);
        str = paths.get(0).toString();
        prefs.put(RECENT_PLOT_STYLES, str);
    }
}
