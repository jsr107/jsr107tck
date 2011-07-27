# Running tests using a different cache impl

Running an implementation other than the RI can be achieved by using -D flags to pint at a dependency other than the RI

Issuing:

    mvn test

is equivalent to:

    mvn \
        -Dtest.cache.groupId='javax.cache.implementation' \
        -Dtest.cache.artifactId='cache-ri' \
        -Dtest.cache.version='0.2-SNAPSHOT' \
        test

By providing system properties corresponding to the above you can use a dependency other than the RI.
The implementation jar should contain a file META-INF/services/javax.cache.spi.CacheManagerFactoryProvider as described in
[chapter 9](https://docs.google.com/document/d/1YZ-lrH6nW871Vd9Z34Og_EqbX_kxxJi55UrSn4yL2Ak/edit?hl=en&authkey=CMCdo8kE&pli=1#heading=h.qojqofiovvda) of the spec.

To run for example just the CacheManagerTest using the acme cache issue the command:

    mvn -Dtest=CacheManagerTest \
        -Dtest.cache.groupId='acme.cache' \
        -Dtest.cache.artifactId='acmeCache' \
        -Dtest.cache.version='0.2-SNAPSHOT' \
        test

## Excluding tests
To exclude tests from being run, please edit file:

    src/test/resources/ExcludeList
