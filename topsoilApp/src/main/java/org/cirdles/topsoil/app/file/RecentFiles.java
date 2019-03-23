package org.cirdles.topsoil.app.file;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class RecentFiles {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String RECENT_FILES = "recent-files";
    private static final int MAX_SIZE = 10;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private static Preferences prefs = Preferences.userNodeForPackage(RecentFiles.class);

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private RecentFiles() { }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static Path[] getPaths() {
        return loadRecentFiles().toArray(new Path[]{});
    }

    public static void addPath(Path path) {
        List<Path> paths = loadRecentFiles();
        paths.remove(path);
        if (paths.size() == MAX_SIZE) {
            paths.remove(MAX_SIZE - 1);
        }
        paths.add(0, path);
        updateRecentFiles(paths);
    }

    public static void clear() {
        for (int i = 1; i <= MAX_SIZE; i++) {
            prefs.remove(RECENT_FILES + i);
        }
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private static List<Path> loadRecentFiles() {
        String str;
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

    private static void updateRecentFiles(List<Path> paths) {
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
