package org.cirdles.topsoil.app.file;

import org.cirdles.topsoil.app.data.SerializableProject;
import org.cirdles.topsoil.app.data.TopsoilProject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A class for reading and writing .topsoil project files.
 *
 * @author marottajb
 */
public class ProjectSerializer {

    /**
     * Writes a {@code TopsoilProject} to the given path.
     *
     * @param projectPath   Path to save project to
     * @param project       TopsoilProject to save
     *
     * @return              true, if successful
     *
     * @throws IOException  if file error
     */
    public static boolean serialize(Path projectPath, TopsoilProject project) throws IOException {
        if (projectPath == null) {
            throw new IllegalArgumentException("projectPath must not be null.");
        }
        if (project == null) {
            throw new IllegalArgumentException("project must not be null.");
        }
        OutputStream out = Files.newOutputStream(projectPath);
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(new SerializableProject(project));
        oos.close();
        out.close();
        return true;
    }

    /**
     * Reads a {@code TopsoilProject} from the provided .topsoil project path.
     *
     * @param projectPath   Path to project file
     *
     * @return              deserialized TopsoilProject
     *
     * @throws IOException  if file error
     */
    public static TopsoilProject deserialize(Path projectPath) throws IOException {
        try (InputStream in = Files.newInputStream(projectPath); ObjectInputStream ois = new ObjectInputStream(in)) {
            return ((SerializableProject) ois.readObject()).reconstruct();
        } catch (InvalidClassException | ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

}
