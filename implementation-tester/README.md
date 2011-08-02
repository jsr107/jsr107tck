# TCK test of RI

Wires together cache-tests with the RI implementation and runs JUnit against them.

Create a similar module for each new implementation you wish to test.
For a test outside the jsr107 depot look [here](https://github.com/yannis666/AcmeCache/)

## Excluding tests
To exclude tests from being run, please edit file:

    src/test/resources/ExcludeList
