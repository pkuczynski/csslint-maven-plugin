assert !(new File( basedir, "target/csslint.xml" ).exists());

String allOutput = new File( basedir, "build.log" ).getText();
assert allOutput.contains("csslint: No errors");

/*
    In Maven v2.x, there is "BUILD SUCCESSFUL" phrase.
    In Maven v3.x, there is "BUILD SUCCESS" phrase.
 */
assert allOutput.contains("BUILD SUCCESS");
