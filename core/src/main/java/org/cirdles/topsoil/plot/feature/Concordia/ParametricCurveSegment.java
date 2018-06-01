/*
 * ParametricCurveSegment.java
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cirdles.topsoil.plot.feature.Concordia;

/**
 *
 * @author James F. Bowring
 */
public interface ParametricCurveSegment {

    /**
     */
    void SplitLeft ();

    /**
     *
     * @return
     */
    ParametricCurveSegment getLeftSeg ();

    /**
     *
     * @return
     */
    double getMaxT ();

    /**
     *
     * @return
     */
    double getMinT ();

    /**
     *
     * @return
     */
    ParametricCurveSegment getRightSeg ();

    /**
     *
     * @return
     */
    public abstract double maxLessSigmaX ();

    /**
     *
     * @return
     */
    public abstract double maxLessSigmaY ();

    /**
     *
     * @return
     */
    public abstract double maxPlusSigmaX ();

    /**
     *
     * @return
     */
    public abstract double maxPlusSigmaY ();

    /**
     *
     * @return
     */
    double maxX ();

    /**
     *
     * @return
     */
    double maxY ();

    /**
     *
     * @return
     */
    double minX ();

    /**
     *
     * @return
     */
    double minY ();

    /**
     *
     * @param leftSeg
     */
    void setLeftSeg ( ParametricCurveSegment leftSeg );

    /**
     *
     * @param maxT
     */
    void setMaxT ( double maxT );

    /**
     *
     * @param minT
     */
    void setMinT ( double minT );

    /**
     *
     * @param rightSeg
     */
    void setRightSeg ( ParametricCurveSegment rightSeg );

    /**
     *
     * @return
     */
    double controlX ();

    /**
     *
     * @return
     */
    double controlY ();

    /**
     *
     * @param theT
     * @return
     */
    double theConcordiaSlope ( double theT );

    /**
     *
     * @param theT
     * @return
     */
    public double theSlope ( double theT );
}
