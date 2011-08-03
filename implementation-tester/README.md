# TCK test of RI

The [TCK](http://download.oracle.com/javame/test-tools/jctt/tck_project_planning_guide.pdf) for javax.cache.

Wires together cache-tests with the implementation and runs JUnit against them.

## ExcludeList
The exclude list for the TCK is here:

    src/test/resources/ExcludeList

Please note that the exclude list is part of the formal TCK and cannot be modified by implementers.

## Running tests against a javax.cache implementation

For implementations hosted in a maven repository, the TCK can be run directly from this directory using maven.
No file modifications are necessary, the dependency to the implementation under test being provided using
system properties to identify the address of the implementation
- implementation-groupId
- implementation-artifactId
- implementation-version

A cache implementation by the fictional Acme company, illustrates Acme cache failing to pass the TCK:

     mvn \
         -Dimplementation-groupId=acme.cache \
         -Dimplementation-artifactId=acme-cache \
         -Dimplementation-version=0.1-SNAPSHOT \
         test

For implementations not in maven you may install jars to the local maven repository or adjust the test class path.