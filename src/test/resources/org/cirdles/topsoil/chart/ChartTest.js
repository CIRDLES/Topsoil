/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

buster.spec.expose();

describe("Chart", function () {
    it("has an instance 'chart'", function () {
        buster.assert.equals("object", typeof chart);
    });

    it("has attribute 'width'", function () {
        buster.assert.isNumber(chart.width);
    });

    it("has attribute 'height'", function () {
        buster.assert.isNumber(chart.height);
    });
});

