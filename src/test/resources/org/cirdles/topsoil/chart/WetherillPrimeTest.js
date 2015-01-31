/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

buster.spec.expose();

describe("WetherillPrime", function() {
    before(function() {
        var LAMBDA_235 = 9.8485e-10,
            LAMBDA_238 = 1.55125e-10,
            vX = Math.exp(LAMBDA_235 * 10) * LAMBDA_235,
            vY = Math.exp(LAMBDA_238 * 10) * LAMBDA_238;
        this.vector = new Vector2D(vX, vY);
    });
    
    it("instantiates an instance of Wetherill correctly (prime)", function() {
       var wetherill = new Wetherill(10);
       var curve = wetherill.prime(10);
       buster.assert.equals(this.vector, curve);
   });
});
