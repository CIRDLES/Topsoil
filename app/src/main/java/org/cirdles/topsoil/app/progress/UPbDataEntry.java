package org.cirdles.topsoil.app.progress;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Created by benjaminmuldrow on 6/20/16.
 */
public class UPbDataEntry {

    private final DoubleProperty leadUranium;
    private final DoubleProperty leadUraniumStD;
    private final DoubleProperty leadUranium2;
    private final DoubleProperty leadUraniumStD2;
    private final DoubleProperty corrCoef;

    public UPbDataEntry(double leadUranium,
                        double leadUraniumStD,
                        double leadUranium2,
                        double leadUraniumStD2) {
        this.leadUranium = new SimpleDoubleProperty(leadUranium);
        this.leadUraniumStD = new SimpleDoubleProperty(leadUraniumStD);
        this.leadUranium2 = new SimpleDoubleProperty(leadUranium2);
        this.leadUraniumStD2 = new SimpleDoubleProperty(leadUraniumStD2);
        this.corrCoef = new SimpleDoubleProperty(0);
    }

    public UPbDataEntry(double leadUranium,
                       double leadUraniumStD,
                       double leadUranium2,
                       double leadUraniumStD2,
                       double corrCoef) {
        this.leadUranium = new SimpleDoubleProperty(leadUranium);
        this.leadUraniumStD = new SimpleDoubleProperty(leadUraniumStD);
        this.leadUranium2 = new SimpleDoubleProperty(leadUranium2);
        this.leadUraniumStD2 = new SimpleDoubleProperty(leadUraniumStD2);
        this.corrCoef = new SimpleDoubleProperty(corrCoef);
    }

    public double getLeadUranium() {
        return leadUranium.get();
    }

    public DoubleProperty leadUraniumProperty() {
        return leadUranium;
    }

    public void setLeadUranium(double leadUranium) {
        this.leadUranium.set(leadUranium);
    }

    public double getLeadUraniumStD() {
        return leadUraniumStD.get();
    }

    public DoubleProperty leadUraniumStDProperty() {
        return leadUraniumStD;
    }

    public void setLeadUraniumStD(double leadUraniumStD) {
        this.leadUraniumStD.set(leadUraniumStD);
    }

    public double getLeadUranium2() {
        return leadUranium2.get();
    }

    public DoubleProperty leadUranium2Property() {
        return leadUranium2;
    }

    public void setLeadUranium2(double leadUranium2) {
        this.leadUranium2.set(leadUranium2);
    }

    public double getLeadUraniumStD2() {
        return leadUraniumStD2.get();
    }

    public DoubleProperty leadUraniumStD2Property() {
        return leadUraniumStD2;
    }

    public void setLeadUraniumStD2(double leadUraniumStD2) {
        this.leadUraniumStD2.set(leadUraniumStD2);
    }

    public double getCorrCoef() {
        return corrCoef.get();
    }

    public DoubleProperty corrCoefProperty() {
        return corrCoef;
    }

    public void setCorrCoef(double corrCoef) {
        this.corrCoef.set(corrCoef);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.leadUranium + ",");
        builder.append(this.leadUraniumStD + ",");
        builder.append(this.leadUranium2 + ",");
        builder.append(this.leadUraniumStD2 + ",");
        builder.append(this.corrCoef);
        return builder.toString();
    }
}
