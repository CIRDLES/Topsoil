/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function Vector2D(x, y) {
    var thisVector2D = this;
    thisVector2D.x = thisVector2D[0] = x;
    thisVector2D.y = thisVector2D[1] = y;
}

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
