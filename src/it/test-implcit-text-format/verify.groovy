assert !(new File( basedir, "target/csslint.xml" ).exists());

def allOutput = new File( basedir, "build.log" ).getText();
assert allOutput.contains("csslint: There are 9 problems");

/*
    In Maven v2.x, there is "BUILD SUCCESSFUL" phrase.
    In Maven v3.x, there is "BUILD SUCCESS" phrase.
 */
assert allOutput.contains("BUILD SUCCESS");