/*
 * TicGeneratorForAxes.java
 *
 * Created Aug 3, 2011
 *
 * Copyright 2006 James F. Bowring and www.Earth-Time.org
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
package org.cirdles.topsoil.plot;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author James F. Bowring
 */
public class TicGeneratorForAxes {

    /**
     * 
     */
    private static int indexOfFirstMajorTic = 0;

    /**
     * 
     * @param axisMin
     * @param axisMax
     * @param numberTics
     * @return
     */
    public static BigDecimal[] generateTics ( double axisMin, double axisMax, int numberTics ) {
        /* Adapted from
         * Nice Numbers for Graph Labels
         * by Paul Heckbert
         * from "Graphics Gems", Academic Press, 1990
         */

        int nfrac;
        double d;
        double ticMin;
        double ticMax;
        double ticRange;

        ticRange = niceNum( axisMax - axisMin, true );
        d = niceNum( ticRange / (numberTics - 1), false );
        ticMin = Math.floor( axisMin / d ) * d;
        ticMax = Math.ceil( axisMax / d ) * d;

        nfrac = (int) Math.max(  - Math.floor( Math.log10( d ) ), 0 );

        BigDecimal[] tics = new BigDecimal[0];

        try {
            tics = new BigDecimal[(int) ((ticMax + 0.5 * d - ticMin) / d) + 1];
            int index = 0;
            for (double x = ticMin; x < ticMax + 0.5 * d; x += d) {
                tics[index] = new BigDecimal( Double.toString( x) ).setScale( nfrac, RoundingMode.HALF_UP );
                index ++;
            }

            // find index of tic ending with most zeroes (3 2 1 or 0)
            indexOfFirstMajorTic = 0;
            for (int i = 0; i < tics.length; i ++) {
                String val = tics[i].toPlainString();
                if ( (val.length() > 2) && val.endsWith( "000" ) ) {
                    indexOfFirstMajorTic = i;
                    break;
                }
            }
            if ( indexOfFirstMajorTic == 0 ) {
                for (int i = 0; i < tics.length; i ++) {
                    String val = tics[i].toPlainString();
                    if ( (val.length() > 1) && val.endsWith( "00" ) ) {
                        indexOfFirstMajorTic = i;
                        break;
                    }
                }
            }
            if ( indexOfFirstMajorTic == 0 ) {
                for (int i = 0; i < tics.length; i ++) {
                    String val = tics[i].toPlainString();
                    if ( (val.length() > 1) && val.endsWith( "0" ) ) {
                        indexOfFirstMajorTic = i;
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }




        return tics;
    }

    /**
     * 
     * @param min
     * @param max
     * @param marginStretchFactor
     * @return
     */
    public static double generateMarginAdjustment ( double min, double max, double marginStretchFactor ) {

        return marginStretchFactor * (max - min);

    }

    private static double niceNum ( double x, boolean round ) {
        /* Adapted from
         * Nice Numbers for Graph Labels
         * by Paul Heckbert
         * from "Graphics Gems", Academic Press, 1990
         */

        int expv;				/* exponent of x */
        double f;				/* fractional part of x */
        double nf;				/* nice, rounded fraction */

        expv = (int) Math.floor( Math.log10( x ) );
        f = x / Math.pow( 10.0, expv );		/* between 1 and 10 */
        if ( round ) {
            if ( f < 1.5 ) {
                nf = 1.;
            } else if ( f < 3. ) {
                nf = 2.;
            } else if ( f < 7. ) {
                nf = 5.;
            } else {
                nf = 10.;
            }
        } else {
            if ( f <= 1. ) {
                nf = 1.;
            } else if ( f <= 2. ) {
                nf = 2.;
            } else if ( f <= 5. ) {
                nf = 5.;
            } else {
                nf = 10.;
            }

        }

        return nf * Math.pow( 10.0, expv );
    }
}
