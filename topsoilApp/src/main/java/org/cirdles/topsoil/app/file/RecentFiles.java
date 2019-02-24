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

    static final String RECENT_FILES = "recent-files";
    static final int MAX_SIZE = 10;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private Preferences prefs;
    private List<Path> paths;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public RecentFiles() {
        this.paths = new ArrayList<>(MAX_SIZE);
        this.prefs = Preferences.userNodeForPackage(RecentFiles.class);
        loadRecentFiles();
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public Path[] getRecentFiles() {
        return paths.toArray(new Path[]{});
    }

    public void addRecentFile(Path path) {
        paths.remove(path);
        if (paths.size() == MAX_SIZE) {
            paths.remove(MAX_SIZE - 1);
        }
        paths.add(0, path);
        updateRecentFiles();
    }

    public void clearRecentFiles() {
        paths.clear();
        updateRecentFiles();
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void loadRecentFiles() {
        String str;
        for (int i = 1; i <= MAX_SIZE; i++) {
            str = prefs.get(RECENT_FILES + i, null);
            if (str != null) {
                paths.add(Paths.get(str));
            }
        }
    }

    private void updateRecentFiles() {
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
