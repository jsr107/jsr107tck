#!/bin/sh
#
# Sets up the signature file 
# Author: Greg Luck
# Date: 13 December 2013

java -jar sigtestdev.jar Setup -Classpath ../../jsr107spec/target/cache-api-0.12-SNAPSHOT.jar:$JAVA_HOME/jre/lib/rt.jar -ApiVersion 1.0.0 -Package javax.cache -Filename jcache_1.0.0.sig

