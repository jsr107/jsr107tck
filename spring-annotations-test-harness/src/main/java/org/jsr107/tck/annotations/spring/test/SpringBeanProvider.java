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

package org.jsr107.tck.annotations.spring.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.cache.annotation.BeanProvider;

/**
 * Spring specific bean provider that loads up the spring app context when constructed
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class SpringBeanProvider implements BeanProvider {
    private final ApplicationContext applicationContext;
    
    public SpringBeanProvider() {
        final ClassPathXmlApplicationContext classPathXmlApplicationContext = 
                new ClassPathXmlApplicationContext("/annotationsTestContext.xml");
        classPathXmlApplicationContext.registerShutdownHook();
        this.applicationContext = classPathXmlApplicationContext;
    }

    /* (non-Javadoc)
     * @see javax.cache.annotation.BeanProvider#getBeanByType(java.lang.Class)
     */
    @Override
    public <T> T getBeanByType(Class<T> beanClass) {
        return this.applicationContext.getBean(beanClass);
    }

}
