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

describe("Vector2D", function() {
    it("should have x attribute", function() {
        buster.assert.equals(5, (new Vector2D(5, 5)).x);
    });
    it("should have y attribute", function() {
        buster.assert.equals(5, (new Vector2D(5, 5)).y);
    });
    it("can add itself to another vector", function() {
        buster.assert.equals(new Vector2D(6, 6), 
            (new Vector2D(5, 5)).plus({x:1,y:1}));
    });
    it("can subtract another vector from itself", function() {
        buster.assert.equals(new Vector2D(4, 4), 
            (new Vector2D(5, 5)).minus({x:1,y:1}));
    });
    it("should be able to multiply itself by a scalar", function() {
        buster.assert.equals(new Vector2D(25, 25), 
            (new Vector2D(5, 5)).times(5));
    });
    it("should be able to divide itself by a scalar", function() {
        buster.assert.equals(new Vector2D(1, 1), 
            (new Vector2D(5, 5)).dividedBy(5));
    });
});

