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

describe("Wetherill", function() {
    initializeWetherill(constants);

    it("can calculate its value at time zero", function() {
        buster.assert.equals(new Vector2D(0, 0), wetherill(0));
    });

    it("can calculate it's derivative at time zero", function() {
        buster.assert.equals(new Vector2D(constants.LAMBDA_235, constants.LAMBDA_238),
            wetherill.prime(0));
    });

    it("can calculate it's value at time t", function() {
        buster.assert.equals(new Vector2D((Math.exp(constants.LAMBDA_235 * 10) - 1),
            (Math.exp(constants.LAMBDA_238 * 10) - 1)),
            wetherill(10));
    });

    it("can calculate it's derivative at time t", function() {
        buster.assert.equals(new Vector2D((constants.LAMBDA_235 * Math.exp(constants.LAMBDA_235 * 10)),
            (constants.LAMBDA_238 * Math.exp(constants.LAMBDA_238 * 10))),
            wetherill.prime(10));
    });
});

