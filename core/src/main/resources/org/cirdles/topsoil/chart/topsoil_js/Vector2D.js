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

(function(){
    "use strict";
    
    window.Vector2D = function(x, y) {
        var thisVector2D = this;
        thisVector2D.x = thisVector2D[0] = x;
        thisVector2D.y = thisVector2D[1] = y;
    };

    Vector2D.prototype = {
        plus: function (point) {
            return new Vector2D(this.x + point.x, this.y + point.y);
        },
        minus: function (point) {
            return new Vector2D(this.x - point.x, this.y - point.y);
        },
        times: function (scalar) {
            return new Vector2D(this.x * scalar, this.y * scalar);
        },
        dividedBy: function (scalar) {
            return new Vector2D(this.x / scalar, this.y / scalar);
        },
        scaleBy: function (xScale, yScale) {
            return new Vector2D(xScale(this.x), yScale(this.y));
        }
    };
    
    return Vector2D;
}());