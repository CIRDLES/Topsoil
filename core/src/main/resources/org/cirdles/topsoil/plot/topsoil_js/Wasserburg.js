
(function() {
    "use strict";

    window.initializeWasserburg = function(constants) {
        window.wasserburg = function (t) {
            return new Vector2D(wasserburg.x(t), wasserburg.y(t));
        };

        // X value for a given t(time in years)
        wasserburg.x = function (t) {
            return 1.0 / (Math.exp(constants.LAMBDA_238 * t) - 1.0);
        };

        // Y value for a given t(time in years)
        wasserburg.y = function (t) {
            return (1.0 / constants.R238_235S) * ((Math.exp(constants.LAMBDA_235 * t) - 1.0) /
                (Math.exp(constants.LAMBDA_238 * t) - 1.0));
        };

        // Vector containing derivative of x and derivative of y for given t(time in years)
        wasserburg.prime = function (t) {
            return new Vector2D(wasserburg.x.prime(t), wasserburg.y.prime(t));
        };

        // derivative(with respect to t) for x given t(time in years)
        wasserburg.x.prime = function (t) {
            return (-constants.LAMBDA_238) * Math.exp((-constants.LAMBDA_238) * t);
        };

        //derivative(with respect to t) for y given t(time in years)
        wasserburg.y.prime = function (t) {
            return ((constants.LAMBDA_238 * Math.exp(constants.LAMBDA_238 * t)) -
                (constants.LAMBDA_235 * Math.exp(constants.LAMBDA_235 * t)) +
                ((constants.LAMBDA_235 - constants.LAMBDA_238) *
                    Math.exp((constants.LAMBDA_235 + constants.LAMBDA_238) * t))) /
                (Math.pow(Math.exp(constants.LAMBDA_238 * t) - 1, 2) * constants.R238_235S);
        };

        /** tools for uncertainty envelopes
         *
         * ****************
         * PLEASE SEE THE DOCUMENT "Uncertainty Envelopes for Lines and Curves"
         * by Noah on the Topsoil Google Drive
         * ****************
         */

        /* jacobian matrix for derivatives with respect to the lambdas
            Format of matrix:
            Row:0 Col:0 value is derivative of x with respect to lambda235
            Row:0 Col:1 value is derivative of x with respect to lambda238
            Row:1 Col:0 value is derivative of y with respect to lambda235
            Row:1 Col:1 value is derivative of y with respect to lambda238
         */
        var J_xyλ = function (t) {
            return [
                [0, -t * Math.exp(-constants.LAMBDA_238 * t)],
                [(t * Math.exp(constants.LAMBDA_235 * t)) /
                ((Math.exp(constants.LAMBDA_238 * t) - 1) * constants.R238_235S),
                    (-t * Math.exp(constants.LAMBDA_238 * t) * (Math.exp(constants.LAMBDA_235 * t) - 1)) /
                    (Math.pow(Math.exp(constants.LAMBDA_238 * t) - 1, 2) * constants.R238_235S)]
            ];
        };

        var v = function (t) {
            return [-wasserburg.y.prime(t), wasserburg.x.prime(t)];
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
            return 2 * Math.cos(Math.atan(-wasserburg.x.prime(t) / wasserburg.y.prime(t)))
                * Math.sqrt(variance(t));
        };

        var deltaY = function (t) {
            return 2 * Math.sin(Math.atan(-wasserburg.x.prime(t) / wasserburg.y.prime(t)))
                * Math.sqrt(variance(t));
        };

        wasserburg.upperEnvelope = function (t) {
            return new Vector2D(
                wasserburg.upperEnvelope.x(t),
                wasserburg.upperEnvelope.y(t));
        };

        wasserburg.upperEnvelope.prime = wasserburg.prime;

        wasserburg.upperEnvelope.x = function (t) {
            return wasserburg.x(t) - deltaX(t);
        };

        wasserburg.upperEnvelope.x.prime = wasserburg.x.prime;

        wasserburg.upperEnvelope.y = function (t) {
            return wasserburg.y(t) - deltaY(t);
        };

        wasserburg.upperEnvelope.y.prime = wasserburg.y.prime;

        wasserburg.lowerEnvelope = function (t) {
            return new Vector2D(
                wasserburg.lowerEnvelope.x(t),
                wasserburg.lowerEnvelope.y(t));
        };

        wasserburg.lowerEnvelope.prime = wasserburg.prime;

        wasserburg.lowerEnvelope.x = function (t) {
            return wasserburg.x(t) + deltaX(t);
        };

        wasserburg.lowerEnvelope.x.prime = wasserburg.x.prime;

        wasserburg.lowerEnvelope.y = function (t) {
            return wasserburg.y(t) + deltaY(t);
        };

        wasserburg.lowerEnvelope.y.prime = wasserburg.y.prime;
    }
})();