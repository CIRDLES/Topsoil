package org.cirdles.topsoil.app.progress;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static org.cirdles.topsoil.app.progress.isoSystem.UPb;
import static org.cirdles.topsoil.app.progress.isoSystem.UTh;

/**
 * Created by sbunce on 6/20/2016.
 */

//Allows for multiple Isotope Systems to be registered and processed
enum isoSystem {UPb, UTh};

public class IsotopeSystems {
    public isoSystem iso;

    public void setIsoSystem(String s){
        switch(s){
            case "UPb":
                iso = UPb;
                break;
            case "UTh":
                iso = UTh;
                break;
        }
    }

    public String getPrintedName(isoSystem iso){
        switch (iso){
            case UPb:
                return "Uranium Lead";
            case UTh:
                return "Uranium Thorium";
            default:
                return "";
        }
    }

    public int getMinColumns (isoSystem iso){
        switch (iso){
            case UPb:
                return 4;
            case UTh:
                return 4;
            default:
                return 0;
        }
    }

    //Will be updated after we figure out the headers for UTh
    public String[] getDefaultHeaders(isoSystem iso){
        switch(iso){
            case UPb:
                String[] upbHeaders = {"207Pb*/235U", "±2σ (%)", "206Pb*/238U", "±2σ (%)", "corr coef"};
                return upbHeaders;
            case UTh:
                String[] uthHeaders = {" "};
                return uthHeaders;
            default:
                return null;
        }
    }

    //Used in IsotopeSelectionWindow to create a menu
    public ObservableList<String> getList(){
        ObservableList<String> ret = FXCollections.observableArrayList(
                "UPb",
                "UTh"
        );
        return ret;
    }

}
