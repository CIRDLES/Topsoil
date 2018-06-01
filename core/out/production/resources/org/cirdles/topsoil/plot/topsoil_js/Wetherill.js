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

    window.initializeWetherill = function(constants) {
        window.wetherill = function (t) {
            return new Vector2D(wetherill.x(t), wetherill.y(t));
        };

        wetherill.x = function (t) {
            return Math.exp(constants.LAMBDA_235 * t) - 1;
        };

        wetherill.y = function (t) {
            return Math.exp(constants.LAMBDA_238 * t) - 1;
        };

        wetherill.prime = function (t) {
            return new Vector2D(wetherill.x.prime(t), wetherill.y.prime(t));
        };

        wetherill.x.prime = function (t) {
            return constants.LAMBDA_235 * Math.exp(constants.LAMBDA_235 * t);
        };

        wetherill.y.prime = function (t) {
            return constants.LAMBDA_238 * Math.exp(constants.LAMBDA_238 * t);
        };

        // tools for envelopes
        var J_xyλ = function (t) {
            return [
                [t * Math.exp(constants.LAMBDA_235 * t), 0],
                [0, t * Math.exp(constants.LAMBDA_238 * t)]
            ];
        };

        var v = function (t) {
            return [-wetherill.y.prime(t), wetherill.x.prime(t)];
        };

        var Σ_λ = [
            [Math.pow(constants.LAMBDA_235 * 0.068031 / 100, 2), 0],
            [0, Math.pow(constants.LAMBDA_238 * 0.053505 / 100, 2)]
        ];

        // multiply any number of vectors/matrices
        var multiply = function () {
            // start with I_2
            var product = [
                [1, 0],
                [0, 1]
            ];

            for (var i = 0; i < arguments.length; i++) {
                product = numeric.dot(product, arguments[i]);
            }

            return product;
        };

        var variance = function (t) {
            var top = multiply(v(t), J_xyλ(t), Σ_λ, numeric.transpose(J_xyλ(t)), v(t));
            var bottom = numeric.dot(v(t), v(t));

            return top / bottom;
        };

        var deltaX = function (t) {
            return 2 * Math.cos(Math.atan(-wetherill.x.prime(t) / wetherill.y.prime(t)))
                    * Math.sqrt(variance(t));
        };

        var deltaY = function (t) {
            return 2 * Math.sin(Math.atan(-wetherill.x.prime(t) / wetherill.y.prime(t)))
                    * Math.sqrt(variance(t));
        };

        wetherill.upperEnvelope = function (t) {
            return new Vector2D(
                    wetherill.upperEnvelope.x(t),
                    wetherill.upperEnvelope.y(t));
        };

        wetherill.upperEnvelope.prime = wetherill.prime;

        wetherill.upperEnvelope.x = function (t) {
            return wetherill.x(t) - deltaX(t);
        };

        wetherill.upperEnvelope.x.prime = wetherill.x.prime;

        wetherill.upperEnvelope.y = function (t) {
            return wetherill.y(t) - deltaY(t);
        };

        wetherill.upperEnvelope.y.prime = wetherill.y.prime;

        wetherill.lowerEnvelope = function (t) {
            return new Vector2D(
                    wetherill.lowerEnvelope.x(t),
                    wetherill.lowerEnvelope.y(t));
        };

        wetherill.lowerEnvelope.prime = wetherill.prime;

        wetherill.lowerEnvelope.x = function (t) {
            return wetherill.x(t) + deltaX(t);
        };

        wetherill.lowerEnvelope.x.prime = wetherill.x.prime;

        wetherill.lowerEnvelope.y = function (t) {
            return wetherill.y(t) + deltaY(t);
        };

        wetherill.lowerEnvelope.y.prime = wetherill.y.prime;
    }
})();
