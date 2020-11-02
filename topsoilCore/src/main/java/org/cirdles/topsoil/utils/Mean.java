package org.cirdles.topsoil.utils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Mean {

    /**
     * Returns the arithmetic mean of the available values
     *
     * @return double of the arithmetic mean
     */
    public static double getMean(double[] numList) {
        DescriptiveStatistics ds = new DescriptiveStatistics(numList);
        return ds.getMean();
    }
}
