buster.spec.expose();

describe("Topsoil", function () {
    it("has an instance 'topsoil'", function () {
        buster.assert.equals("object", typeof topsoil);
    });

    it("has an instance alias 'ts'", function () {
        buster.assert.equals(topsoil, ts);
    });
});
