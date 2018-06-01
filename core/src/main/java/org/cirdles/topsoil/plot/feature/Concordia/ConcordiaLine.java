package org.cirdles.topsoil.plot.feature.Concordia;

public class ConcordiaLine {

    private ParametricCurveSegment startingSeg;
    private ParametricCurveSegment endingSeg;


    public ConcordiaLine(ParametricCurveSegment startingSeg){

        this.startingSeg = startingSeg;
        this.endingSeg = startingSeg;
    }

    /**
     *
     * @param n
     */
    public void refineLineByRecursiveHalving(int n) {

        for (int i = 0; i < n; i++) {
            ParametricCurveSegment mySeg = startingSeg;

            while (mySeg != null) {
                mySeg.SplitLeft();

                mySeg = mySeg.getRightSeg();
            }

            startingSeg = startingSeg.getLeftSeg();
        }
    }

    /**
     *
     * @return
     */
    public ParametricCurveSegment getStartSeg() {
        return startingSeg;
    }

    /**
     *
     * @param startSeg
     */
    public void setStartSeg(ParametricCurveSegment startSeg) {
        this.startingSeg = startSeg;
    }

    /**
     *
     * @return
     */
    public ParametricCurveSegment getEndSeg() {
        return endingSeg;
    }

    /**
     *
     * @param endSeg
     */
    public void setEndSeg(ParametricCurveSegment endSeg) {
        this.endingSeg = endSeg;
    }
}
