/* 
 * Copyright 2014 CIRDLES.
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
        var t = 10,
            LAMBDA_235 = 9.8485e-10,
            LAMBDA_238 = 1.55125e-10,
            vX = LAMBDA_235 * Math.exp(LAMBDA_235 * t),
            vY = LAMBDA_238 * Math.exp(LAMBDA_238 * t);
        this.vector = new Vector2D(vX, vY);
    });
    
    it("instantiates an instance (prime)", function() {
       var curve = wetherill.prime(10);
       buster.assert.equals(this.vector, curve);
   });
});
