# Running tests using a different cache impl

## Adding a implementation jar
Edit pom.xml and add a dependency on your implementation. Look for the text:

		<!--Insert JSR107 implementation as a dependency here -->

and add for example:

        <dependency>
            <groupId>acme.cache</groupId>
            <artifactId>acmeCache</artifactId>
            <version>0.2-SNAPSHOT</version>
        </dependency>

The jar should contain a file META-INF/services/javax.cache.spi.CacheManagerFactoryProvider as described in
[chapter 9](https://docs.google.com/document/d/1YZ-lrH6nW871Vd9Z34Og_EqbX_kxxJi55UrSn4yL2Ak/edit?hl=en&authkey=CMCdo8kE&pli=1#heading=h.qojqofiovvda) of the spec.

to run for example just the CacheManagerTest issue the command:

    mvn -Dtest=CacheManagerTest test

## Excluding tests
To exclude tests from being run, please edit file:

    src/test/resources/ExcludeList

## Class Factory
*NOTE: please think of better ways to do this.*

Some tests require an implementation of
[javax.cache.InstanceFactory](https://github.com/jsr107/jsr107tck/blob/master/src/test/java/javax/cache/InstanceFactory.java)
to create instance of classes not creatable directly from the API (Cache, CacheConfiguration).
By default RI versions of these classes will be created. To use your implementation of the factory add

    -Dcache.test.FactoryClass=<YourFactoryClassName>

to the target ensuring your factory is in the test classpath.
So, to use the RI implementation (the default) can use:

    mvn -Dtest=CacheConfigurationTest -Dcache.test.FactoryClass='javax.cache.TestInstanceFactory$RIInstanceFactory' test

A possible solution, allowing the elimination of InstanceFatory in the TCK, is for us to add the factory methods:

    <K, V> Cache<K, V> createCache(String name);
    CacheConfiguration createCacheConfiguration();

to the api. For example we could add them to the  javax.cache.spi.CacheManagerFactoryProvider interface and to javax.cache.CacheManagerFactory. If we did this then the 2 should be renamed