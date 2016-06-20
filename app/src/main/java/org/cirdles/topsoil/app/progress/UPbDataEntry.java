package org.cirdles.topsoil.app.progress;

import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;

/**
 * Created by benjaminmuldrow on 6/20/16.
 */
public class UPbDataEntry {

    double leadUranium;
    double leadUraniumStD;
    double leadUranium2;
    double leadUraniumStD2;
    double corrCoef = 0;

    public UPbDataEntry(double leadUranium,
                        double leadUraniumStD,
                        double leadUranium2,
                        double leadUraniumStD2) {

        setLeadUranium(leadUranium);
        setLeadUraniumStD(leadUraniumStD);
        setLeadUranium2(leadUranium2);
        setLeadUraniumStD2(leadUraniumStD2);
    }

    public UPbDataEntry(double leadUranium,
                       double leadUraniumStD,
                       double leadUranium2,
                       double leadUraniumStD2,
                       double corrCoef) {
        setLeadUranium(leadUranium);
        setLeadUraniumStD(leadUraniumStD);
        setLeadUranium2(leadUranium2);
        setLeadUraniumStD2(leadUraniumStD2);
        setCorrCoef(corrCoef);
    }

    public double getLeadUranium() {
        return leadUranium;
    }

    public void setLeadUranium(double leadUranium) {
        this.leadUranium = leadUranium;
    }

    public double getLeadUraniumStD() {
        return leadUraniumStD;
    }

    public void setLeadUraniumStD(double leadUraniumStD) {
        this.leadUraniumStD = leadUraniumStD;
    }

    public double getLeadUranium2() {
        return leadUranium2;
    }

    public void setLeadUranium2(double leadUranium2) {
        this.leadUranium2 = leadUranium2;
    }

    public double getLeadUraniumStD2() {
        return leadUraniumStD2;
    }

    public void setLeadUraniumStD2(double leadUraniumStD2) {
        this.leadUraniumStD2 = leadUraniumStD2;
    }

    public double getCorrCoef() {
        return corrCoef;
    }

    public void setCorrCoef(double corrCoef) {
        this.corrCoef = corrCoef;
    }

}
