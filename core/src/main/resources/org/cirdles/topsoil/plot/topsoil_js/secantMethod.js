(function() {
    "use strict";

    window.secantMethod = function (f, value) {
        // if value is provided then shift f by value
        if (typeof value !== "undefined") {
            var unshiftedF = f;

            f = function (x) {
                return unshiftedF(x) - value;
            };

            // the derivative is the same after the shift
            f.prime = unshiftedF.prime;
        }

        var x0 = 1;
        var x1 = 1.24;
        var x2 = 0;

        for (var i = 0; i < 200; i++) {
            x2 = x1 - f(x1) * ((x1 - x0) / (f(x1) - f(x0)));

            if (x2 - x1 < .001) {
                break;
            }
            x0 = x1;
            x1 = x2;
        }
        return x2;
    };
})();
