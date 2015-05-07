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

"use strict";

describe("Vector2D", function() {
    it("should have x attribute", function() {
        expect((new Vector2D(5, 5)).x).toEqual(5);
    });
    it("should have y attribute", function() {
        expect((new Vector2D(5, 5)).y).toEqual(5);
    });
    it("can add itself to another vector", function() {
        expect(new Vector2D(6, 6)).toEqual((new Vector2D(5, 5)).plus({x:1,y:1}));
    });
    it("can subtract another vector from itself", function() {
      expect(new Vector2D(4, 4)).toEqual((new Vector2D(5, 5)).minus({x:1,y:1}));
    });
    it("should be able to multiply itself by a scalar", function() {
        expect(new Vector2D(25, 25)).toEqual((new Vector2D(5, 5)).times(5));
    });
    it("should be able to divide itself by a scalar", function() {
        expect(new Vector2D(1, 1)).toEqual((new Vector2D(5, 5)).dividedBy(5));
    });
});
