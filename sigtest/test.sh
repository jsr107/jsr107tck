#!/bin/sh
#
# Tests a version of the API against the 1.0.0 signature file
# Author: Greg Luck
# Date: 13 December 2013

java -jar sigtestdev.jar Test -Classpath ../../jsr107spec/target/cache-api-0.12-SNAPSHOT.jar:$JAVA_HOME/jre/lib/rt.jar -ApiVersion 1.0.0 -Package javax.cache -Filename jcache_1.0.0.sig -Mode bin -Static
