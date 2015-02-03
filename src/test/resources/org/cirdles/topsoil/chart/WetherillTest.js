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

describe("Wetherill", function() {
   before(function() {
        var LAMBDA_235 = 9.8485e-10,
            LAMBDA_238 = 1.55125e-10;
        this.calcX = function(t) { return Math.exp(LAMBDA_235 * t) - 1; },
        this.calcY = function(t) { return Math.exp(LAMBDA_238 * t) - 1; };
   });
   
   it("calculating it's value at a time t", function() {
        var t = 10,
            value = wetherill(t);
        buster.assert.equals(new Vector2D(this.calcX(t),this.calcY(t)), value);
   });
   it("calculating it's value at a time 0", function() {
        var t = 0,
            value = wetherill(t);
        buster.assert.equals(new Vector2D(this.calcX(t),this.calcY(t)), value);
   });
});

