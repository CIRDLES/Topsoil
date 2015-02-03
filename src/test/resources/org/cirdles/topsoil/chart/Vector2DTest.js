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
   
   it("should have x attribute", function() {
       buster.assert.equals(5, this.vector[0]);
   });
   
   it("should have y attribute" , function() {
       buster.assert.equals(5, this.vector[1]);
   });
   
   it("can add itself to another vector", function() {
       var point = {x:5, y:5};
       var expectedResult = new Vector2D(10, 10);
       buster.assert.equals(expectedResult, this.vector.plus(point))
   });
   
   it("can subtract another vector from itself", function() {
       var point = {x:1, y:1};
       var expectedResult = new Vector2D(4, 4);
       buster.assert.equals(expectedResult, this.vector.minus(point));
   });
   
   it("can multiply itself by a scalar", function() {
       var scalar = 5;
       var expectedResult = new Vector2D(25, 25);
       buster.assert.equals(expectedResult, this.vector.times(scalar));
   });
   
   it("can divide itself by a scalar", function() {
       var scalar = 5;
       var expectedResult = new Vector2D(1, 1);
       buster.assert.equals(expectedResult, this.vector.dividedBy(scalar));
   });
});
