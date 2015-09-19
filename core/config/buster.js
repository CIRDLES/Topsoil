var config = module.exports;

config["topsoil.js"] = {
    environment: "browser",
    rootPath: "../",
    sources: [
        "src/main/resources/org/cirdles/topsoil/chart/**/*.js"
    ],
    tests: [
        "src/test/resources/org/cirdles/topsoil/chart/*Test.js"
    ]
};
