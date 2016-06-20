package org.cirdles.topsoil.app.progress;

/**
 * Created by sbunce on 6/20/2016.
 */

//Allows for multiple Isotope Systems to be registered and processed
public class IsotopeSystems {
    public enum isoSystem {UPb, UTh};

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

}
