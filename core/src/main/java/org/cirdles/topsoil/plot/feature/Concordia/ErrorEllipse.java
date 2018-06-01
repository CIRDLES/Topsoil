package org.cirdles.topsoil.plot.feature.Concordia;


import Jama.CholeskyDecomposition;
import Jama.Matrix;

/**
 * Class adapted from original Topsoil Ellipse implementation and the work of James Bowring found
 * within the ET_Redux project.
 */
public class ErrorEllipse {

    private static final Matrix cntrlPointsMatrix = ControlPointsBase();


    private double xVal;
    private double yVal;
    private double rho;
    private double xSig;
    private double ySig;
    private Matrix ellipseControlPoints;

    private double uncertainty;

    public ErrorEllipse(double xVal, double yVal, double rho, double xSig, double ySig, double uncertainty){

        this.xVal = xVal;
        this.yVal = yVal;
        this.rho = rho;
        this.xSig = xSig;
        this.ySig = ySig;
        this.uncertainty = uncertainty;


        CalculateErrorEllipseIII();


    }


    private void CalculateErrorEllipseIII (){
        /**
         * xSig and ySig need to be in the form of one sigma abs.
         * This needs to be fixed or else the wrong values are going to be calculated.
         */
        double covarianceX_Y = rho * xSig * ySig;

        // ref http://math.nist.gov/javanumerics/jama/
        double[][] covMatRaw = new double[2][2];
        covMatRaw[0][0] = Math.pow( xSig, 2 );
        covMatRaw[0][1] = covarianceX_Y;
        covMatRaw[1][0] = covarianceX_Y;
        covMatRaw[1][1] = Math.pow( ySig, 2 );

        Matrix covMat = new Matrix( covMatRaw );

        CholeskyDecomposition cd = covMat.chol();
        Matrix R = cd.getL().transpose();

        Matrix scaledControlPointsMatrix = cntrlPointsMatrix.times( uncertainty );
        ellipseControlPoints = scaledControlPointsMatrix.times( R );

        double[][] xy = new double[13][2];

        for (int i = 0; i < 13; i ++) {
            xy[i][0] = xVal;
            xy[i][1] = yVal;
        }

        ellipseControlPoints.plusEquals( new Matrix( xy ) );

    }


    /**
     *
     * @return
     */
    public double getbezierMinX () {
        //return ellipseControlPoints.get(6, 0);
        return getExtreme( -1, 0 );
    }

    /**
     *
     * @return
     */
    public double getbezierMaxX () {
        //return ellipseControlPoints.get(1, 0);
        return getExtreme( 1, 0 );
    }

    /**
     *
     * @return
     */
    public double getbezierMinY () {
        //return ellipseControlPoints.get(6, 1);
        return getExtreme( -1, 1 );
    }

    /**
     *
     * @return
     */
    public double getbezierMaxY () {
        //return ellipseControlPoints.get(1, 1);
        return getExtreme( 1, 1 );
    }

    private double getExtreme ( int maxORmin, int xORy ) {
        // maxORmin = -1 for min, 1 for max
        // xORy = 0 for x, 1 for y
        double retval;

        if ( maxORmin == -1 ) {
            retval = 1e10;
            for (int i = 0; i < ellipseControlPoints.getRowDimension(); i ++) {
                if ( ellipseControlPoints.get( i, xORy ) < retval ) {
                    retval = ellipseControlPoints.get( i, xORy );
                }
            }
        } else {
            retval = 0.0;
            for (int i = 0; i < ellipseControlPoints.getRowDimension(); i ++) {
                if ( ellipseControlPoints.get( i, xORy ) > retval ) {
                    retval = ellipseControlPoints.get( i, xORy );
                }
            }
        }
        return retval;
    }

    /**
     *
     * @return
     */
    public Matrix getEllipseControlPoints(){

        return ellipseControlPoints;
    }

    //Static method to initalize the control points base matrix
    private static Matrix ControlPointsBase() {

        double k = 4 / 3 * (Math.sqrt(2) - 1);

        double[][] matrixCPB = new double[][]
                {{1, 0},
                        {1, k},
                        {k, 1},
                        {0, 1},
                        {-k, 1},
                        {-1, k},
                        {-1, 0},
                        {-1, -k},
                        {-k, -1},
                        {0, -1},
                        {k, -1},
                        {1, -k},
                        {1, 0}};

        return new Matrix(matrixCPB);
    }
}
