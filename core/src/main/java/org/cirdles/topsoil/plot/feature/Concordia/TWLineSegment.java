package org.cirdles.topsoil.plot.feature.Concordia;

import Jama.Matrix;

import java.math.BigDecimal;

public class TWLineSegment implements ParametricCurveSegment {

    private double minT;
    private double maxT;
    private ParametricCurveSegment leftSeg;
    private ParametricCurveSegment rightSeg;

    private static double minPlusOneSigmaT;
    private static double minLessOneSigmaT;
    private static double maxPlusOneSigmaT;
    private static double maxLessOneSigmaT;


    private static double lambda235 = 0;
    private static double lambda238 = 0;
    private static double r238_235s = 0;


    public TWLineSegment (){}

    public TWLineSegment (
            double lambda235,
            double lambda238,
            double r238_235s,
            double minT,
            double maxT ) {

        TWLineSegment.lambda235 = lambda235;
        TWLineSegment.lambda238 = lambda238;
        TWLineSegment.r238_235s = r238_235s;


        this.minT = minT;
        this.maxT = maxT;

        this.leftSeg = null;
        this.rightSeg = null;
    }

    /**
     * Private Constructor
     * @param minT
     * @param maxT
     * @param leftSeg
     * @param rightSeg
     */
    public TWLineSegment (
            double minT,
            double maxT,
            ParametricCurveSegment leftSeg,
            ParametricCurveSegment rightSeg ) {

        this.minT = minT;
        this.maxT = maxT;
        this.leftSeg = leftSeg;
        this.rightSeg = rightSeg;
    }

    /**
     *
     */
    public void SplitLeft () {

        ParametricCurveSegment myLeft =
                new TWLineSegment( minT, minT + ((maxT - minT) / 2.0), leftSeg, this );

        this.setLeftSeg( myLeft );
        if ( myLeft.getLeftSeg() != null ) {
            myLeft.getLeftSeg().setRightSeg( myLeft );
        }

        this.setMinT( minT + ((maxT - minT) / 2.0) );
    }

    /**
     *
     * @param theT
     * @return
     */
    public static double theX ( double theT ) {
        return 1.0 / Math.expm1( lambda238 * theT );
    }

    /**
     *
     * @param theT
     * @return
     */
    public static double theY ( double theT ) {
        return Math.expm1( lambda235 * theT )//
                / Math.expm1( lambda238 * theT )//
                / r238_235s;
    }

    /**
     * returns an estimated slope of theconcordia in the vicinity of theT
     * @param theT
     * @return
     */
    public double theSlope ( double theT ) {

        double x = theX( theT );
        double rLambda235_lambda238 =
                lambda235
                        / lambda238;

        return (1.0 / r238_235s)
                * ((
                (lambda238 * (1.0 + x)
                        - lambda235)
                        * Math.pow( 1.0 + (1.0 / x), rLambda235_lambda238 )
                        / lambda238 / (1 + x))
                - 1);
    }

    /**
     *
     * @param theT
     * @return
     */
    public double theConcordiaSlope ( double theT ) {

        double x = theX( theT );
        double rLambda235_lambda238 =
                TWLineSegment.lambda235
                        / TWLineSegment.lambda238;

        return (1.0 / TWLineSegment.r238_235s)
                * (
                (
                        (TWLineSegment.lambda238 * (1.0 + x)
                                - TWLineSegment.lambda235)
                                * Math.pow( 1.0 + (1.0 / x), rLambda235_lambda238 )
                                / TWLineSegment.lambda238 / (1.0 + x))
                        - 1.0);
    }

    private double theUpperEnvelopeSlope ( double theT ) {

        double deltaY =
                (theY( theT * 1.0001 ) - deltaUncertantyY( theT * 1.0001, false ))
                        -
                        (theY( theT * 0.9999 ) - deltaUncertantyY( theT * 0.9999, false ));

        double deltaX =
                (theX( theT * 1.0001 ) - deltaUncertantyX( theT * 1.0001, false ))
                        -
                        (theX( theT * 0.9999 ) - deltaUncertantyX( theT * 0.9999, false ));

        return deltaY / deltaX;
    }

    private double theLowerEnvelopeSlope ( double theT) {
        double deltaY =//
                (theY( theT * 1.0001 ) + deltaUncertantyY( theT * 1.0001, false ))
                        -
                        (theY( theT * 0.9999 ) + deltaUncertantyY( theT * 0.9999, false ));

        double deltaX =
                (theX( theT * 1.0001 ) + deltaUncertantyX( theT * 1.0001, false ))
                        -
                        (theX( theT * 0.9999 ) + deltaUncertantyX( theT * 0.9999, false ));

        return deltaY / deltaX;
    }

    /**
     *
     * @return
     */
    public double controlX () {
        double m1 = theConcordiaSlope( minT );
        double m2 = theConcordiaSlope( maxT );

        return ((m1 * theX( minT ))
                - (m2 * theX( maxT ))
                - theY( minT ) + theY( maxT ))
                / (m1 - m2);
    }

    /**
     *
     * @param
     * @return
     */
    public double controlUpperX () {

        double m1 = theUpperEnvelopeSlope( minT);
        double m2 = theUpperEnvelopeSlope( maxT);

        return ((m1 * minLessSigmaX())
                - (m2 * maxLessSigmaX())
                - minLessSigmaY() + maxLessSigmaY())
                / (m1 - m2);
    }

    /**
     *
     * @param
     * @return
     */
    public double controlLowerX () {

        double m1 = theLowerEnvelopeSlope( minT );
        double m2 = theLowerEnvelopeSlope( maxT );

        return ((m1 * minPlusSigmaX())
                - (m2 * maxPlusSigmaX())
                - minPlusSigmaY() + maxPlusSigmaY())
                / (m1 - m2);
    }

    /**
     *
     * @return
     */
    public double controlY () {
        double m1 = theConcordiaSlope( minT );
        double m2 = theConcordiaSlope( maxT );

        return -1 * (-1 * m1 * m2 * theX( minT ) + m1 * m2 * theX( maxT ) + m2 * theY( minT ) - m1 * theY( maxT ))
                / (m1 - m2);
    }

    /**
     *
     * @param
     * @return
     */
    public double controlUpperY () {
        double m1 = theUpperEnvelopeSlope( minT );
        double m2 = theUpperEnvelopeSlope( maxT );

        return -1 * (-1 * m1 * m2 * minLessSigmaX()
                + m1 * m2 * maxLessSigmaX()
                + m2 * minLessSigmaY()
                - m1 * maxLessSigmaY())
                / (m1 - m2);

    }

    /**
     *
     * @param
     * @return
     */
    public double controlLowerY () {
        double m1 = theLowerEnvelopeSlope( minT );//     theConcordiaSlope( minT );
        double m2 = theLowerEnvelopeSlope( maxT );//     theConcordiaSlope( maxT );

        return -1 * (-1 * m1 * m2 * minPlusSigmaX()
                + m1 * m2 * maxPlusSigmaX()
                + m2 * minPlusSigmaY()
                - m1 * maxPlusSigmaY())
                / (m1 - m2);
    }

    /**
     *
     * @param theT
     * @return
     */
    public static double theOrthogonalSlope ( double theT ) {
        return  - 1.0 / (new TWLineSegment()).theSlope( theT );
    }

    /**
     *
     * @return
     */
    public double minX () {
        return 1.0 / Math.expm1( lambda238 * getMinT() );
    }

    /**
     *
     * @return
     */
    public double minY () {
        return Math.expm1( lambda235 * getMinT() )//
                / Math.expm1( lambda238 * getMinT() )//
                / r238_235s;
    }

    /**
     *
     * @return
     */
    public double maxX () {
        return 1.0 / Math.expm1( lambda238 * getMaxT() );
    }

    /**
     *
     * @return
     */
    public double maxY () {
        return Math.expm1( lambda235 * getMaxT() )//
                / Math.expm1( lambda238 * getMaxT() )//
                / r238_235s;
    }

    // Jan 2011 new uncertainty envelope calcs ADAPTED from concordiaLineSegment and Noah's docs
    private double uncertaintyDeltaByAxis ( String axis, double t, boolean verbose ) {

        // decay constant covariance matrix
        double[][] covMatDecayConstantRaw = new double[2][2];

        //need to see what the oneSigABS value is for lambda235
        /**
         *
         */
        //covMatDecayConstantRaw[0][0] = Math.pow( lambda235.getOneSigmaAbs().doubleValue(), 2 );
        covMatDecayConstantRaw[0][0] = Math.pow( lambda235, 2 );

        covMatDecayConstantRaw[0][1] = 0.0;
        covMatDecayConstantRaw[1][0] = covMatDecayConstantRaw[0][1];
        //need to see what the oneSigABS value is for lambda238
        /**
         *
         */
        //covMatDecayConstantRaw[1][1] = Math.pow( lambda238.getOneSigmaAbs().doubleValue(), 2 );
        covMatDecayConstantRaw[1][1] = Math.pow( lambda238, 2 );

        Matrix covMatDecayConstant = new Matrix( covMatDecayConstantRaw );

        // Jacobian matrix x,y,lambda
        double[][] jacobianMatXYLambdaRaw = new double[2][2];
        jacobianMatXYLambdaRaw[0][0] = 0.0;
        jacobianMatXYLambdaRaw[0][1] =  - t * (Math.expm1( ( -lambda238 ) * t ) + 1.0);
        jacobianMatXYLambdaRaw[1][0] = (t * (Math.expm1( lambda235 * t ) + 1.0))
                / Math.expm1( lambda238 * t )
                / r238_235s;
        jacobianMatXYLambdaRaw[1][1] =  - t * (Math.expm1( lambda238 * t ) + 1.0)
                * Math.expm1( lambda235 * t )
                / Math.pow( Math.expm1( lambda238 * t ), 2 )
                / r238_235s;

        Matrix jacobianMatXYLambda = new Matrix( jacobianMatXYLambdaRaw );



        // perpendicular vector
        double[][] perpVectorRaw = new double[1][2];

        double deltaXdeltaT =  - lambda238 * (Math.expm1(  - lambda238 * t ) + 1.0);
        double deltaYdeltaT =
                ((lambda238
                        * (Math.expm1( lambda238 * t ) + 1.0))
                        - (lambda235
                        * (Math.expm1( lambda235 * t ) + 1.0))
                        + ((lambda235 - lambda238)
                        * (Math.expm1( lambda235 + ( lambda238 * t ) + 1.0)))
                        / Math.pow( Math.expm1( lambda238 * t ), 2 )
                        / r238_235s);

        perpVectorRaw[0][0] =  - deltaYdeltaT;
        perpVectorRaw[0][1] = deltaXdeltaT;

        Matrix perpVector = new Matrix( perpVectorRaw );


        // perpendicular variance
        double perpVarNum =
                (perpVector
                        .times( jacobianMatXYLambda )
                        .times( covMatDecayConstant )
                        .times( jacobianMatXYLambda.transpose() )
                        .times( perpVector.transpose() )).get( 0, 0 );
        double perpVarDen = (perpVector.times( perpVector.transpose() )).get( 0, 0 );
        double perpVariance = perpVarNum / perpVarDen;


        // inverse tangent terms
        double arcTanTerm =
                Math.atan( perpVector.get( 0, 1 ) / perpVector.get( 0, 0 ) );

        if ( verbose ) {
            System.out.println( "TW TIME = " + t );
            System.out.println( "     covL[0][0]= " + covMatDecayConstantRaw[0][0] );
            System.out.println( "     covL[0][1]= " + covMatDecayConstantRaw[0][1] );
            System.out.println( "     covL[1][0]= " + covMatDecayConstantRaw[1][0] );
            System.out.println( "     covL[1][1]= " + covMatDecayConstantRaw[1][1] );

            System.out.println( "     Jxyl[0][0]= " + jacobianMatXYLambdaRaw[0][0] );
            System.out.println( "     Jxyl[0][1]= " + jacobianMatXYLambdaRaw[0][1] );
            System.out.println( "     Jxyl[1][0]= " + jacobianMatXYLambdaRaw[1][0] );
            System.out.println( "     Jxyl[1][1]= " + jacobianMatXYLambdaRaw[1][1] );
            System.out.println( "  perpVec[0][0]= " + perpVectorRaw[0][0] );
            System.out.println( "  perpVec[0][1]= " + perpVectorRaw[0][1] );
            System.out.println( "     perpVarNum= " + perpVarNum );
            System.out.println( "     perpVarDen= " + perpVarDen );
            System.out.println( "        perpVar= " + perpVariance );
            System.out.println( "         arcTan= " + arcTanTerm );
        }

        if ( axis.equalsIgnoreCase( "X" ) ) {
            return 2.0 * Math.cos( arcTanTerm ) * Math.sqrt( perpVariance );
        } else { // "Y"
            return 2.0 * Math.sin( arcTanTerm ) * Math.sqrt( perpVariance );
        }
    }

    /**
     *
     * @param t
     * @param verbose
     * @return
     */
    public double deltaUncertantyX ( double t, boolean verbose ) {
        return uncertaintyDeltaByAxis( "X", t, verbose );
    }

    /**
     *
     * @param t
     * @param verbose
     * @return
     */
    public double deltaUncertantyY ( double t, boolean verbose ) {
        return uncertaintyDeltaByAxis( "Y", t, verbose );
    }

    /**
     *
     * @param
     * @return
     */
    public double minPlusSigmaX () {
        return minX() + deltaUncertantyX( minT, false );
    }

    /**
     *
     * @param
     * @return
     */
    public double minPlusSigmaY () {
        return minY() + deltaUncertantyY( minT, false );
    }

    /**
     *
     * @param
     * @return
     */
    public double maxPlusSigmaX () {
        return maxX() + deltaUncertantyX( maxT, false );
    }

    /**
     *
     * @param
     * @return
     */
    public double maxPlusSigmaY () {
        return maxY() + deltaUncertantyY( maxT, false );
    }

    /**
     *
     * @param
     * @return
     */
    public double minLessSigmaX () {
        return minX() - deltaUncertantyX( minT, false );
    }

    /**
     *
     * @param
     * @return
     */
    public double minLessSigmaY () {
        return minY() - deltaUncertantyY( minT, false );
    }

    /**
     *
     * @param
     * @return
     */
    public double maxLessSigmaX () {
        return maxX() - deltaUncertantyX( maxT, false );
    }

    /**
     *
     * @param
     * @return
     */
    public double maxLessSigmaY () {
        return maxY() - deltaUncertantyY( maxT, false );
    }

    /**
     *
     * @return
     */
    public static double getMinPlusOneSigmaT () {
        return minPlusOneSigmaT;
    }

    /**
     *
     * @param aMinPlusOneSigmaT
     */
    public static void setMinPlusOneSigmaT ( double aMinPlusOneSigmaT ) {
        minPlusOneSigmaT = aMinPlusOneSigmaT;
    }

    /**
     *
     * @return
     */
    public static double getMinLessOneSigmaT () {
        return minLessOneSigmaT;
    }

    /**
     *
     * @param aMinLessOneSigmaT
     */
    public static void setMinLessOneSigmaT ( double aMinLessOneSigmaT ) {
        minLessOneSigmaT = aMinLessOneSigmaT;
    }

    /**
     *
     * @return
     */
    public static double getMaxPlusOneSigmaT () {
        return maxPlusOneSigmaT;
    }

    /**
     *
     * @param aMaxPlusOneSigmaT
     */
    public static void setMaxPlusOneSigmaT ( double aMaxPlusOneSigmaT ) {
        maxPlusOneSigmaT = aMaxPlusOneSigmaT;
    }

    /**
     *
     * @return
     */
    public static double getMaxLessOneSigmaT () {
        return maxLessOneSigmaT;
    }

    /**
     *
     * @param aMaxLessOneSigmaT
     */
    public static void setMaxLessOneSigmaT ( double aMaxLessOneSigmaT ) {
        maxLessOneSigmaT = aMaxLessOneSigmaT;
    }

    /**
     *
     * @return
     */
    public double getMinT () {
        return minT;
    }

    /**
     *
     * @param minT
     */
    public void setMinT ( double minT ) {
        this.minT = minT;
    }

    /**
     *
     * @return
     */
    public double getMaxT () {
        return maxT;
    }

    /**
     *
     * @param maxT
     */
    public void setMaxT ( double maxT ) {
        this.maxT = maxT;
    }

    /**
     *
     * @return
     */
    public ParametricCurveSegment getLeftSeg () {
        return leftSeg;
    }

    /**
     *
     * @param leftSeg
     */
    public void setLeftSeg ( ParametricCurveSegment leftSeg ) {
        this.leftSeg = leftSeg;
    }

    /**
     *
     * @return
     */
    public ParametricCurveSegment getRightSeg () {
        return rightSeg;
    }

    /**
     *
     * @param rightSeg
     */
    public void setRightSeg ( ParametricCurveSegment rightSeg ) {
        this.rightSeg = rightSeg;
    }
}
