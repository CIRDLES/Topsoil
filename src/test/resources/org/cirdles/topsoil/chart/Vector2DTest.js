/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

buster.spec.expose();

var spec = describe("Vector2D", function() {
   before(function() {
       this.vector = new Vector2D(5, 5);
   });
   
   it("should have x attribute", function() {
       buster.assert.equals(5, this.vector.x);
   });
   
   it("should have y attribute" , function() {
       buster.assert.equals(5, this.vector.y);
   });
   
   it("should have x attribute (accessed via [] index)", function() {
       buster.assert.equals(5, this.vector[0]);
   });
   
   it("should have y attribute (accessed via [] index)" , function() {
       buster.assert.equals(5, this.vector[1]);
   });
   
   it("can call vector.plus for correct result", function() {
       var point = {x:5, y:5};
       var expectedResult = new Vector2D(10, 10);
       buster.assert.equals(expectedResult, this.vector.plus(point))
   });
   
   it("can call vector.minus for correct result", function() {
       var point = {x:1, y:1};
       var expectedResult = new Vector2D(4, 4);
       buster.assert.equals(expectedResult, this.vector.minus(point));
   });
   
   it("can call vector.times for correct result", function() {
       var scalar = 5;
       var expectedResult = new Vector2D(25, 25);
       buster.assert.equals(expectedResult, this.vector.times(scalar));
   });
   
   it("can call vector.divideBy for correct result", function() {
       // TBI
       buster.assert.equals(true, true);
   });
});
