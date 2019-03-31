var config = module.exports;

config["topsoil.js"] = {
    environment: "browser",
    rootPath: "../",
    sources: [
        "src/main/resources/org/cirdles/topsoil/plot/*.js",
        "src/main/resources/org/cirdles/topsoil/plot/topsoil_js/*.js"
    ],
    tests: [
        "src/test/resources/org/cirdles/topsoil/plot/*Test.js"
    ]
};
