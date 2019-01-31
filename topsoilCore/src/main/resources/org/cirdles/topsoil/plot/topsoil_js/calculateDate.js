(function() {
    "use strict";

    window.calculateDate = function (r207_206r, startDate) {

        var xn = startDate;

        if (xn <= 0.0) {

            xn = 10.0e9 * (4.5695 - 5.3011 * (Math.exp(-5.4731 * r207_206r)));
        }

        for (var i = 0; i < 35; i++) {

            var expLambda238xnMinus1 = Math.expm1(constants.LAMBDA_238 * xn);
            var expLambda235xnMinus1 = Math.expm1(constants.LAMBDA_235 * xn);

            var new10 = (expLambda235xnMinus1 / expLambda238xnMinus1 / constants.R238_235S) - r207_206r;

            var new11 = ((constants.R238_235S * expLambda238xnMinus1 * constants.LAMBDA_235
                * (1.0 + expLambda235xnMinus1))
                - (expLambda235xnMinus1 * constants.R238_235S * constants.LAMBDA_238 * (1.0 + expLambda238xnMinus1)))
                / constants.R238_235S
                / constants.R238_235S
                / expLambda238xnMinus1
                / expLambda238xnMinus1;

            xn -= (new10 / new11);
        }
        return xn;


    };
})();