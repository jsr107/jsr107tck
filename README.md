# TCK

[tckSrc](https://github.com/jsr107/jsr107tck/tree/master/tckSrc) contains the sources of the TCK

[testRI](https://github.com/jsr107/jsr107tck/tree/master/testRI) uses the TCK to test the [RI](https://github.com/jsr107/RI)

For a test outside the jsr107 depot look [here](https://github.com/yannis666/AcmeCache/)

##Maven

To build all modules but not run any tests:

    mvn -DskipTests clean install

This command is required before running tests to install test dependencies.

The ri-tester module wires up tests and the RI. A similar module should be created for each implementation.
The specification has mandatory features in the basic profile.

The following commands should be run from the ri-tester directory:

To run basic tests:

    mvn -P test-basic-cache test

The optional features are JTA and Annotations. To run optional tests:

    mvn -P test-optional-cache test

Finally, to run a single test class:

    mvn -Dtest=CacheManagerFactoryTest test
