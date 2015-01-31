/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

buster.spec.expose();

describe("Wetherill", function() {
   before(function() {
        var LAMBDA_235 = 9.8485e-10,
            LAMBDA_238 = 1.55125e-10,
            vX = Math.exp(LAMBDA_235 * 10) - 1,
            vY = Math.exp(LAMBDA_238 * 10) - 1;
        this.vector = new Vector2D(vX, vY);
   });
   
   it("can instantiate an instance (non-prime)", function() {
       var curve = new Wetherill(10);
       buster.assert.equals(this.vector, curve);
   });
});

