assert !(new File( basedir, "target/csslint.xml" ).exists());

String allOutput = new File( basedir, "build.log" ).getText();
assert allOutput.contains("csslint: No errors");
assert allOutput.contains("BUILD SUCCESS");
