var config = module.exports;

config["topsoil.js"] = {
    environment: "browser",
    rootPath: "../",
    sources: [
        "src/main/resources/org/cirdles/topsoil/chart/*.js",
        "src/main/resources/org/cirdles/topsoil/chart/topsoil_js/*.js"
    ],
    tests: [
        "src/test/resources/org/cirdles/topsoil/chart/*Test.js"
    ]
};
