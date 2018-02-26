package org.cirdles.topsoil.plot.bridges;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.cirdles.mcLeanRegression.McLeanRegression;
import org.cirdles.mcLeanRegression.McLeanRegressionInterface;
import org.cirdles.mcLeanRegression.core.McLeanRegressionLineInterface;

public class Regression {

    McLeanRegressionLineInterface mcLeanRegressionLine;
    McLeanRegressionInterface mcLeanRegression = new McLeanRegression();

    //args come in as Strings because arrays can't be passed between JavaScript and Java
    public McLeanRegressionLineInterface fitLineToDataFor2D(String x, String y, String x1SigmaAbs, String y1SigmaAbs, String rhos) {
        double[] xDouble = toDouble(x);
        double[] yDouble = toDouble(y);
        double[] x1SigmaAbsDouble = toDouble(x1SigmaAbs);
        double[] y1SigmaAbsDouble = toDouble(y1SigmaAbs);
        double[] rhosDouble = toDouble(rhos);

        double[][] xy = new double[x.length()][2];

        for(int i = 0; i < xDouble.length; i++) {
            xy[i][0] = xDouble[i];
            xy[i][1] = yDouble[i];
        }

        try {
            mcLeanRegressionLine = mcLeanRegression.fitLineToDataFor2D(xDouble, yDouble, x1SigmaAbsDouble, y1SigmaAbsDouble, rhosDouble);
        } catch (Exception e) {
            // in the case that an uncertainty is not provided, the try block fails and we do an ordinary least squares (OLS)
            SimpleRegression regression = new SimpleRegression();
            regression.addData(xy);

            mcLeanRegressionLine = new McLeanOrdinaryLeastSquaresRegressionLine(regression);
        }

        return mcLeanRegressionLine;
    }

    public double getAX() { return mcLeanRegressionLine.getA()[0][0]; }
    public double getIntercept() { return mcLeanRegressionLine.getA()[1][0]; }

    public double getVectorX() { return mcLeanRegressionLine.getV()[0][0]; }
    public double getSlope() {
        return mcLeanRegressionLine.getV()[1][0];
    }

    public String getV() {
        return mcLeanRegressionLine.getV()[1].toString();
    }

    public String getSav() {
        StringBuilder savString = new StringBuilder();
        double[][] sav = mcLeanRegressionLine.getSav();
        for (double[] dubList : sav) {
            for (double dub : dubList) {
                savString.append(dub);
                savString.append(",");
            }
            savString.append(";");
        }

        return savString.toString();
    }

    //Convert the strings from the BasePlot.js into double[]
    private double[] toDouble(String str) {
        String[] stringList = str.split(",");

        double[] doubleList = new double[stringList.length];

        for(int i = 0; i < stringList.length; i++) {
            doubleList[i] = Double.parseDouble(stringList[i]);
        }

        return doubleList;
    }

    private class McLeanOrdinaryLeastSquaresRegressionLine
            implements McLeanRegressionLineInterface {

        private SimpleRegression regression;
        private double[][] a;
        private double[][] v;

        public McLeanOrdinaryLeastSquaresRegressionLine(SimpleRegression regression) {
            this.regression = regression;

            a = new double[2][1];
            a[1][0] = regression.getIntercept();

            v = new double[2][1];
            v[1][0] = regression.getSlope();
        }

        @Override
        public double[][] getA() {
            return a;
        }

        @Override
        public double[][] getV() {
            return v;
        }

        @Override
        public double[][] getSav() {
            return null;
        }

        @Override
        public double getMSWD() {
            return 0.0;
        }

        @Override
        public int getN() {
            return (int) regression.getN();
        }
    }

}
