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

describe("NewtonMethod", function() {
    it("should verify a self-determined oracle", function() {
        buster.assert.equals(newtonMethod(wetherill.x, 0), 
            newtonMethod(wetherill.x, 0));
    });
    describe("execution time", function() {
        before(function() {
            var startT = new Date().getTime();
            if (newtonMethod(wetherill.x, 1)) {
                this.t = (new Date().getTime()) - startT;
            }
        });
        it("should complete execution in less than 100 milliseconds", function() {
            buster.assert.less(this.t, 100);
        });
    });
    describe("root accuracy", function() {
        before(function() {
            this.f = function (x) {
                return Math.pow(x, 7) - 1000;
            };
            this.f.prime = function (x) {
                return 7 * Math.pow(x, 6);
            };
        });
        it("should return the root of the given function within 0.01+-", function() {
            buster.assert.near(newtonMethod(this.f), 2.69008741, 0.01);
        });
    });
});

