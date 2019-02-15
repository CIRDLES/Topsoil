package org.cirdles.topsoil.app.util.serialization;

import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;

import java.io.Serializable;

import java.util.*;

import static org.cirdles.topsoil.app.util.serialization.SerializableTopsoilProject.ProjectKey.*;

/**
 * Represents a serializable state of Topsoil by storing the information in a {@link TopsoilProject} in a {@code
 * Serializable} format.
 *
 * @author marottajb
 *
 * @see ProjectSerializer
 */
public class SerializableTopsoilProject implements Serializable {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = -3402100385336874762L;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final Map<ProjectKey, Serializable> data = new HashMap<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public SerializableTopsoilProject(TopsoilProject project) {
//        data.put(LAMBDAS, extractLambdaSettings());
        data.put(DATA_TABLES, new ArrayList<>(project.getDataTableList()));
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public TopsoilProject getTopsoilProject() {
        return new TopsoilProject(((List<DataTable>) data.get(DATA_TABLES)).toArray(new DataTable[]{}));
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        in.defaultReadObject();
//    }
//
//    private void writeObject(ObjectOutputStream out) throws IOException {
//        out.defaultWriteObject();
//    }

    public enum ProjectKey {
        LAMBDAS,
        DATA_TABLES
    }

}
