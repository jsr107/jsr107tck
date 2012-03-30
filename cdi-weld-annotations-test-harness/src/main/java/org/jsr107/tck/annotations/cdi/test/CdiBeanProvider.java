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

package org.jsr107.tck.annotations.cdi.test;

import java.util.Set;

import javax.cache.annotation.BeanProvider;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * Weld specific bean provider that loads up the CDI BeanManager when constructed
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CdiBeanProvider implements BeanProvider {
    private final BeanManager beanManager;
    
    public CdiBeanProvider() {
        Weld weld = new Weld();
        WeldContainer delegate = weld.initialize();
        beanManager = delegate.getBeanManager();
    }

    /* (non-Javadoc)
     * @see javax.cache.annotation.BeanProvider#getBeanByType(java.lang.Class)
     */
    @Override
    public <T> T getBeanByType(Class<T> beanClass) {
        if (beanClass == null) {
            throw new IllegalArgumentException("CDI Bean type cannot be null");
        }

        Set<Bean<?>> beans = beanManager.getBeans(beanClass);
        if (beans.isEmpty()) {
            throw new IllegalStateException("Could not locate a bean of type " + beanClass.getName());
        }
        Bean<?> bean = beanManager.resolve(beans);
        CreationalContext<?> context = beanManager
                .createCreationalContext(bean);
        @SuppressWarnings("unchecked")
        T result = (T) beanManager.getReference(bean, bean.getBeanClass(),
                context);
        return result;
    }

}
