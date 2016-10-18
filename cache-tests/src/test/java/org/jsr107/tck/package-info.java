/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

/**
 This package contains the TCK for JSR107.

 The tests use JUnit and should run both from an IDE and Maven using the supplied pom.xml.

 All optional features are in their own test classes with a MethodRule used to exclude them if the
 implementation does not support the feature as interrogated by a capabilities on CacheManagerFactoryProvider.


 @author Yannis Cosmadopoulos
 @author Greg Luck
 */
package org.jsr107.tck;
