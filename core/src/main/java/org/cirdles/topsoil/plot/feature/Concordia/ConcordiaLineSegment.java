package org.cirdles.topsoil.plot.feature.Concordia;

import Jama.Matrix;

public class ConcordiaLineSegment implements ParametricCurveSegment {

    // Class variables
    /**
     *
     */
    public static double lambda235;
    /**
     *
     */
    public static double lambda238;

    // these class variables store special end case data for plotting in the bounding box
    private static double minPlusOneSigmaT;
    private static double minLessOneSigmaT;
    private static double maxPlusOneSigmaT;
    private static double maxLessOneSigmaT;

    // Instance Variables
    private double minT;
    private double maxT;
    private ParametricCurveSegment leftSeg;
    private ParametricCurveSegment rightSeg;

    /**
     *
     */
    public ConcordiaLineSegment() {
    }

    /**
     * Public Constructor
     *
     * @param lambda235
     * @param lambda238
     * @param minT
     * @param maxT
     */
    public ConcordiaLineSegment(
            double lambda235,
            double lambda238,
            double minT,
            double maxT) {

        ConcordiaLineSegment.lambda235 = lambda235;
        ConcordiaLineSegment.lambda238 = lambda238;

        this.minT = minT;
        this.maxT = maxT;

        this.leftSeg = null;
        this.rightSeg = null;
    }

    /**
     * Private Constructor
     *
     * @param minT
     * @param maxT
     * @param leftSeg
     * @param rightSeg
     */
    public ConcordiaLineSegment(
            double minT,
            double maxT,
            ParametricCurveSegment leftSeg,
            ParametricCurveSegment rightSeg) {

        this.minT = minT;
        this.maxT = maxT;
        this.leftSeg = leftSeg;
        this.rightSeg = rightSeg;
    }

    /**
     *
     */
    public void SplitLeft() {

        ParametricCurveSegment myLeft =
                new ConcordiaLineSegment(minT, minT + ((maxT - minT) / 2.0), leftSeg, this);

        this.setLeftSeg(myLeft);
        if (myLeft.getLeftSeg() != null) {
            myLeft.getLeftSeg().setRightSeg(myLeft);
        }

        this.setMinT(minT + ((maxT - minT) / 2.0));
    }

    /**
     *
     * @param theT
     * @return
     */
    public static double theX(double theT) {
        if (lambda235 == 0) {
            return 0;
        } else {
            return Math.expm1(lambda235 * theT);
        }
    }

    /**
     *
     * @param theT
     * @return
     */
    public static double theY(double theT) {
        if (lambda238 == 0) {
            return 0;
        } else {
            return Math.expm1(lambda238 * theT);
        }
    }

    /**
     * returns an estimated slope of theconcordia in the vicinity of theT
     *
     * @param theT
     * @return
     */
    public double theSlope(double theT) {

        double x = theX(theT);
        double rLambda238_lambda235 = lambda238 / lambda235;

        return rLambda238_lambda235//
                * Math.pow((1.0 + x), (rLambda238_lambda235 - 1.0));
    }

    // START May 2010 methods added to implement Bezier curving
    /**
     *
     * @param theT
     * @return
     */
    public double theConcordiaSlope(double theT) {
        double x = theX(theT);
        double rLambda238_lambda235 = ConcordiaLineSegment.lambda238 / ConcordiaLineSegment.lambda235;

        return rLambda238_lambda235//
                * Math.pow((1.0 + x), (rLambda238_lambda235 - 1.0));
    }

    private double theUpperEnvelopeSlope(double theT) {

        double deltaY =//
                (theY(theT * 1.0001) - deltaUncertantyY(theT * 1.0001, false))//
                        -//
                        (theY(theT * 0.9999) - deltaUncertantyY(theT * 0.9999, false));

        double deltaX =//
                (theX(theT * 1.0001) - deltaUncertantyX(theT * 1.0001, false))//
                        -//
                        (theX(theT * 0.9999) - deltaUncertantyX(theT * 0.9999, false));

        return deltaY / deltaX;
    }

    private double theLowerEnvelopeSlope(double theT) {
        double deltaY =//
                (theY(theT * 1.0001) + deltaUncertantyY(theT * 1.0001, false))//
                        -//
                        (theY(theT * 0.9999) + deltaUncertantyY(theT * 0.9999, false));

        double deltaX =//
                (theX(theT * 1.0001) + deltaUncertantyX(theT * 1.0001, false))//
                        -//
                        (theX(theT * 0.9999) + deltaUncertantyX(theT * 0.9999, false));

        return deltaY / deltaX;
    }

    /**
     *
     * @return
     */
    public double controlX() {
        double m1 = theConcordiaSlope(minT);
        double m2 = theConcordiaSlope(maxT);

        return ((m1 * theX(minT))//
                - (m2 * theX(maxT))//
                - theY(minT) + theY(maxT))//
                / (m1 - m2);
    }

    /**
     *
     * @param aspectRatio
     * @return
     */
    public double controlUpperX(double aspectRatio) {
        double m1 = theUpperEnvelopeSlope(minT);
        double m2 = theUpperEnvelopeSlope(maxT);

        //     System.out.println("t = " + minT +   "SLOPES: upperenv= " + m1 + "  concordia= " + theConcordiaSlope( minT ) + "  aspect= " + aspectRatio);

        return ((m1 * minLessSigmaX())//
                - (m2 * maxLessSigmaX())//
                - minLessSigmaY() + maxLessSigmaY())//
                / (m1 - m2);
    }

    /**
     *
     * @param aspectRatio
     * @return
     */
    public double controlLowerX(double aspectRatio) {
        double m1 = theLowerEnvelopeSlope(minT);
        double m2 = theLowerEnvelopeSlope(maxT);

        return ((m1 * minPlusSigmaX())//
                - (m2 * maxPlusSigmaX())//
                - minPlusSigmaY() + maxPlusSigmaY())//
                / (m1 - m2);
    }

    /**
     *
     * @return
     */
    public double controlY() {
        double m1 = theConcordiaSlope(minT);
        double m2 = theConcordiaSlope(maxT);

        return -1 * (-1 * m1 * m2 * theX(minT) //
                + m1 * m2 * theX(maxT) //
                + m2 * theY(minT) //
                - m1 * theY(maxT))//
                / (m1 - m2);
    }

    /**
     *
     * @param theT
     * @return
     */
    public static double theOrthogonalSlope(double theT) {
        return -1.0 / (new ConcordiaLineSegment()).theSlope(theT);
    }

    /**
     *
     * @return
     */
    public double minX() {
        return Math.expm1(lambda235 * getMinT());
    }

    /**
     *
     * @return
     */
    public double minY() {
        return Math.expm1(lambda238 * getMinT());
    }

    /**
     *
     * @return
     */
    public double maxX() {
        return Math.expm1(lambda235 * getMaxT());
    }

    /**
     *
     * @return
     */
    public double maxY() {
        return Math.expm1(lambda238 * getMaxT());
    }

    // June 2010 new uncertainty envelope calcs

    //need to check into these sigma values and what is actually being calculated
    private double uncertaintyDeltaByAxis(String axis, double t, boolean verbose) {

        // decay constant covariance matrix
        double[][] covMatDecayConstantRaw = new double[2][2];

        //look into what is being calculated by the one sigma method here.
        //covMatDecayConstantRaw[0][0] = Math.pow(lambda235.getOneSigmaAbs().doubleValue(), 2);
        covMatDecayConstantRaw[0][1] = 0.0;
        covMatDecayConstantRaw[1][0] = covMatDecayConstantRaw[0][1];
        //covMatDecayConstantRaw[1][1] = Math.pow(lambda238.getOneSigmaAbs().doubleValue(), 2);

        Matrix covMatDecayConstant = new Matrix(covMatDecayConstantRaw);

        // Jacobian matrix x,y,lambda
        double[][] jacobianMatXYLambdaRaw = new double[2][2];
        jacobianMatXYLambdaRaw[0][0] = t * (Math.expm1(lambda235 * t) + 1.0);
        jacobianMatXYLambdaRaw[0][1] = 0.0;
        jacobianMatXYLambdaRaw[1][0] = 0.0;
        jacobianMatXYLambdaRaw[1][1] = t * (Math.expm1(lambda238 * t) + 1.0);

        Matrix jacobianMatXYLambda = new Matrix(jacobianMatXYLambdaRaw);



        // perpendicular vector
        double[][] perpVectorRaw = new double[1][2];

        double deltaXdeltaT = lambda235 * (Math.expm1(lambda235 * t) + 1.0);
        double deltaYdeltaT = lambda238 * (Math.expm1(lambda238 * t) + 1.0);

        perpVectorRaw[0][0] = -deltaYdeltaT;
        perpVectorRaw[0][1] = deltaXdeltaT;

        Matrix perpVector = new Matrix(perpVectorRaw);



        // perpendicular variance
        double perpVarNum = //
                (perpVector//
                        .times(jacobianMatXYLambda)//
                        .times(covMatDecayConstant)//
                        .times(jacobianMatXYLambda.transpose())//
                        .times(perpVector.transpose())).get(0, 0);
        double perpVarDen = (perpVector.times(perpVector.transpose())).get(0, 0);
        double perpVariance = perpVarNum / perpVarDen;


        // inverse tangent terms
        double arcTanTerm = Math.atan(perpVector.get(0, 1) / perpVector.get(0, 0));

        if (verbose) {
            System.out.println("TIME = " + t);
            System.out.println("     covL[0][0]= " + covMatDecayConstantRaw[0][0]);
            System.out.println("     covL[0][1]= " + covMatDecayConstantRaw[0][1]);
            System.out.println("     covL[1][0]= " + covMatDecayConstantRaw[1][0]);
            System.out.println("     covL[1][1]= " + covMatDecayConstantRaw[1][1]);

            System.out.println("     Jxyl[0][0]= " + jacobianMatXYLambdaRaw[0][0]);
            System.out.println("     Jxyl[1][1]= " + jacobianMatXYLambdaRaw[1][1]);
            System.out.println("  perpVec[0][0]= " + perpVectorRaw[0][0]);
            System.out.println("  perpVec[0][1]= " + perpVectorRaw[0][1]);
            System.out.println("     perpVarNum= " + perpVarNum);
            System.out.println("     perpVarDen= " + perpVarDen);
            System.out.println("        perpVar= " + perpVariance);
            System.out.println("         arcTan= " + arcTanTerm);
        }

        if (axis.equalsIgnoreCase("X")) {
            return 2.0 * Math.cos(arcTanTerm) * Math.sqrt(perpVariance);
        } else { // "Y"
            return 2.0 * Math.sin(arcTanTerm) * Math.sqrt(perpVariance);
        }
    }

    /**
     *
     * @param t
     * @param verbose
     * @return
     */
    public double deltaUncertantyX(double t, boolean verbose) {
        return uncertaintyDeltaByAxis("X", t, verbose);
    }

    /**
     *
     * @param t
     * @param verbose
     * @return
     */
    public double deltaUncertantyY(double t, boolean verbose) {
        return uncertaintyDeltaByAxis("Y", t, verbose);
    }

    /**
     *
     * @return
     */
    public double minPlusSigmaX() {
        return minX() + deltaUncertantyX(minT, false);
    }

    /**
     *
     * @return
     */
    public double minPlusSigmaY() {
        return minY() + deltaUncertantyY(minT, false);
    }

    /**
     *
     * @return
     */
    public double maxPlusSigmaX() {
        return maxX() + deltaUncertantyX(maxT, false);
    }

    /**
     *
     * @return
     */
    public double maxPlusSigmaY() {
        return maxY() + deltaUncertantyY(maxT, false);
    }

    /**
     *
     * @return
     */
    public double minLessSigmaX() {
        return minX() - deltaUncertantyX(minT, false);
    }

    /**
     *
     * @return
     */
    public double minLessSigmaY() {
        return minY() - deltaUncertantyY(minT, false);
    }

    /**
     *
     * @return
     */
    public double maxLessSigmaX() {
        return maxX() - deltaUncertantyX(maxT, false);
    }

    /**
     *
     * @return
     */
    public double maxLessSigmaY() {
        return maxY() - deltaUncertantyY(maxT, false);
    }

    /**
     *
     * @return
     */
    public static double getMinPlusOneSigmaT() {
        return minPlusOneSigmaT;
    }

    /**
     *
     * @param aMinPlusOneSigmaT
     */
    public static void setMinPlusOneSigmaT(double aMinPlusOneSigmaT) {
        minPlusOneSigmaT = aMinPlusOneSigmaT;
    }

    /**
     *
     * @return
     */
    public static double getMinLessOneSigmaT() {
        return minLessOneSigmaT;
    }

    /**
     *
     * @param aMinLessOneSigmaT
     */
    public static void setMinLessOneSigmaT(double aMinLessOneSigmaT) {
        minLessOneSigmaT = aMinLessOneSigmaT;
    }

    /**
     *
     * @return
     */
    public static double getMaxPlusOneSigmaT() {
        return maxPlusOneSigmaT;
    }

    /**
     *
     * @param aMaxPlusOneSigmaT
     */
    public static void setMaxPlusOneSigmaT(double aMaxPlusOneSigmaT) {
        maxPlusOneSigmaT = aMaxPlusOneSigmaT;
    }

    /**
     *
     * @return
     */
    public static double getMaxLessOneSigmaT() {
        return maxLessOneSigmaT;
    }

    /**
     *
     * @param aMaxLessOneSigmaT
     */
    public static void setMaxLessOneSigmaT(double aMaxLessOneSigmaT) {
        maxLessOneSigmaT = aMaxLessOneSigmaT;
    }

    /**
     *
     * @return
     */
    public double getMinT() {
        return minT;
    }

    /**
     *
     * @param minT
     */
    public void setMinT(double minT) {
        this.minT = minT;
    }

    /**
     *
     * @return
     */
    public double getMaxT() {
        return maxT;
    }

    /**
     *
     * @param maxT
     */
    public void setMaxT(double maxT) {
        this.maxT = maxT;
    }

    /**
     *
     * @return
     */
    public ParametricCurveSegment getLeftSeg() {
        return leftSeg;
    }

    /**
     *
     * @param leftSeg
     */
    public void setLeftSeg(ParametricCurveSegment leftSeg) {
        this.leftSeg = leftSeg;
    }

    /**
     *
     * @return
     */
    public ParametricCurveSegment getRightSeg() {
        return rightSeg;
    }

    /**
     *
     * @param rightSeg
     */
    public void setRightSeg(ParametricCurveSegment rightSeg) {
        this.rightSeg = rightSeg;
    }
}

