# TCK

[cache-tests](https://github.com/jsr107/jsr107tck/tree/master/cache-tests) contains the sources of the TCK

[implementation-tester](https://github.com/jsr107/jsr107tck/tree/master/implementation-tester) uses the TCK to test the [RI](https://github.com/jsr107/RI)

For a test outside the jsr107 depot look [here](https://github.com/yannis666/AcmeCache/)

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



To run basic tests on the RI:

    mvn -P test-basic-cache \
        -Dimplementation-groupId=javax.cache.implementation \
        -Dimplementation-artifactId=cache-ri \
        -Dimplementation-version=0.2-SNAPSHOT \
        test

The optional features are JTA and Annotations. To run optional tests:

    mvn -P test-optional-cache \
        -Dimplementation-groupId=javax.cache.implementation \
        -Dimplementation-artifactId=cache-ri \
        -Dimplementation-version=0.2-SNAPSHOT \
        test

Finally, to run a single test class:

    mvn -Dtest=CacheManagerFactoryTest \
        -Dimplementation-groupId=javax.cache.implementation \
        -Dimplementation-artifactId=cache-ri \
        -Dimplementation-version=0.2-SNAPSHOT \
        test

An example for something other than the RI, a cache implementation by the fictional Acme company, illustrates how
to run a single test class:

    mvn -Dtest=CacheManagerFactoryTest \
        -Dimplementation-groupId=acme.cache \
        -Dimplementation-artifactId=acme-cache \
        -Dimplementation-version=0.1-SNAPSHOT \
        test
