assert !(new File( basedir, "target/csslint.xml" ).exists());

def allOutput = new File( basedir, "build.log" ).getText();
assert allOutput.contains("csslint: There are 9 problems");
assert allOutput.contains("BUILD SUCCESS");