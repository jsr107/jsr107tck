# TCK

[cache-tests](https://github.com/jsr107/jsr107tck/tree/master/cache-tests) contains the sources of the TCK

[implementation-tester](https://github.com/jsr107/jsr107tck/tree/master/implementation-tester) uses the TCK to test the [RI](https://github.com/jsr107/RI)

##Building the domain and tests

To build all modules but not run any tests:

    mvn -DskipTests clean install

This command is required before running tests to install test dependencies.

## Running the tests against an implementation


The implementation-tester module wires up tests and an implementation. This is done
by specifying the coordinates for the implementation from the command line.

All the following examples specify the RI. Change the coordinates in the examples to your own implementation
to test it instead.

The following commands should be run from the implementation-tester directory:



To run basic tests on the RI (NOTE setting BUILD_VARS as below is unneccessary as these are the
default values but illustrates how to set values for an alternate implementation):

    BUILD_VARS="-Dimplementation-groupId=org.jsr107.ri \
     -Dimplementation-artifactId=cache-ri-impl \
     -Dimplementation-version=0.12-SNAPSHOT"

    mvn $BUILD_VARS -P test-basic-cache test

The optional features are JTA and Annotations. To run optional tests:

    mvn $BUILD_VARS -P test-optional-cache test

Finally, to run a single test class:

    mvn $BUILD_VARS -pl :specific-implementation-tester -Dtest=CacheTest test
