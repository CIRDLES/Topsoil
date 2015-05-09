/* 
* Copyright 2015 CIRDLES.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

(function() {
    "use strict";
    
    window.newtonMethod = function (f, value) {
        // if value is provided then shift f by value
        if (typeof value !== "undefined") {
            var unshiftedF = f;

            f = function (x) {
                return unshiftedF(x) - value;
            };

            // the derivative is the same after the shift
            f.prime = unshiftedF.prime;
        }

        var x0, x1 = 1;

        // bounce around until the derivative at x1 is nonzero
        while (f.prime(x1) === 0)
            x1 += Math.random();

        for (var i = 0; i < 200; i++) {
            x0 = x1;
            if (Math.abs(f.prime(x0)) < Number.EPSILON) {
                break;
            }
            x1 -= f(x0) / f.prime(x0);
        }

        return x1;
    };
})();