# TCK

[tckSrc](https://github.com/jsr107/jsr107tck/tree/master/tckSrc) contains the sources of the TCK

[testRI](https://github.com/jsr107/jsr107tck/tree/master/testRI) uses the TCK to test the [RI](https://github.com/jsr107/RI)

For a test outside the jsr107 depot look [here](https://github.com/yannis666/AcmeCache/)

Some useful maven targets which can be run from the top level:

`mvn clean install`  - builds all modules

    mvn -P test-base-cache test
    mvn -P test-optional-cache test
    mvn -Dtest=CacheManagerFactoryTest test
