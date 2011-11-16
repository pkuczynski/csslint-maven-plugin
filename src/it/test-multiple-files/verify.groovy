assert new File( basedir, "target/csslint.xml" ).exists();

def pluginOutput = new XmlSlurper().parse(new File( basedir, "target/csslint.xml" ));
assert pluginOutput.file.size() == 2
assert pluginOutput.depthFirst().grep{ it.@severity == 'warning' }.size() == 18
assert pluginOutput.depthFirst().grep{ it.@severity == 'error' }.size() == 0

def allOutput = new File( basedir, "build.log" ).getText();
assert allOutput.contains("BUILD SUCCESS");