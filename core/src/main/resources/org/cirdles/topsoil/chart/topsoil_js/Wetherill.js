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
    
    window.wetherill = function(t) {
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
})();