/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function Wetherill(t) {
    var thisWetherill = this;
    
    thisWetherill.LAMBDA_235 = 9.8485e-10;
    thisWetherill.LAMBDA_238 = 1.55125e-10;   
    thisWetherill.curve = new Vector2D(thisWetherill.x(t), thisWetherill.y(t));
    
    return thisWetherill.curve;
}

Wetherill.prototype.x = function(t) {
    var thisWetherill = this;
    return Math.exp(thisWetherill.LAMBDA_235 * t) - 1;
}

Wetherill.prototype.y = function(t) {
    var thisWetherill = this;
    return Math.exp(thisWetherill.LAMBDA_238 * t) - 1;
}

Wetherill.prototype.prime = function(t) {
    var thisWetherill = new Wetherill(t);
    return new Vector2D(thisWetherill.xPrime(t), thisWetherill.yPrime(t));
}

Wetherill.prototype.xPrime = function(t) {
    var thisWetherill = this;
    return Math.exp(thisWetherill.LAMBDA_235 * t) 
            * thisWetherill.LAMBDA_235;
}

Wetherill.prototype.yPrime = function(t) {
    var thisWetherill = this;
    return Math.exp(thisWetherill.LAMBDA_235 * t) 
            * thisWetherill.LAMBDA_238;
}
