/**
 *  Copyright 2011 Terracotta, Inc.
 *  Copyright 2011 Oracle, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package javax.cache.util;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.Set;
import java.util.logging.Logger;

/**
 * For the TCK we need to have an exclude list of bad tests so that disabling tests
 * can be done without changing code.
 *
 * This class creates a rule for the class provided
 *
 * The exclude list is created by {@link ExcludeList}
 *
 * @author Yannis Cosmadopoulos
 * @since 1.7
 */
public class TestExcluder implements MethodRule {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final Set<String> excludes;

    /**
     * Constructor for TestExcluder.
     * Uses the supplied class name and {@link ExcludeList#getExcludes(String)} to
     * determine the methods to be excluded.
     *
     * @param c the class for which tests should be excluded
     */
    public TestExcluder(Class c) {
        excludes = ExcludeList.INSTANCE.getExcludes(c.getName());
    }

    /**
     * {@inheritDoc}
     */
    public Statement apply(Statement statement, FrameworkMethod frameworkMethod, Object o) {
        final String methodName = frameworkMethod.getName();
        final String className = frameworkMethod.getMethod().getDeclaringClass().getName();
        if (isExcluded(methodName)) {
            return new ExcludedStatement(className, methodName, logger);
        } else {
            return statement;
        }
    }

    private boolean isExcluded(String methodName) {
        return excludes != null && excludes.contains(methodName);
    }

    /**
     * {@inheritDoc}
     */
    public static class ExcludedStatement extends Statement {
        private final String methodName;
        private final String className;
        private final Logger logger;

        public ExcludedStatement(String className, String methodName, Logger logger) {
            this.className = className;
            this.methodName = methodName;
            this.logger = logger;
        }

        @Override
        public void evaluate() throws Throwable {
            logger.info("===== EXCLUDING TEST '" + className + "'\t'" + methodName + "'.");
        }
    }
}
