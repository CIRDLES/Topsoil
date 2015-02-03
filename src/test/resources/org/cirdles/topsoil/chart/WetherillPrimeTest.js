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

buster.spec.expose();

describe("WetherillPrime", function() {
    before(function() {
        var LAMBDA_235 = 9.8485e-10,
            LAMBDA_238 = 1.55125e-10;
            this.calcX = function(t) { return LAMBDA_235 * Math.exp(LAMBDA_235 * t); },
            this.calcY = function(t) { return LAMBDA_238 * Math.exp(LAMBDA_238 * t); };
    });
    
    it("calculating it's derivative at a time t", function() {
       var t = 10,
           derivative = wetherill.prime(t);
       buster.assert.equals(new Vector2D(this.calcX(t), this.calcY(t)), derivative);
    });
    
    it("calculating it's derivative at a time zero", function() {
        var t = 0,
           derivative = wetherill.prime(t);
       buster.assert.equals(new Vector2D(this.calcX(t), this.calcY(t)), derivative);
    });
});
