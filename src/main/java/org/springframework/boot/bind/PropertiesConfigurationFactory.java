/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.bind;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.core.env.PropertySources;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;

import java.util.Properties;

/**
 * Validate some {@link Properties} (or optionally {@link PropertySources}) by binding
 * them to an object of a specified type and then optionally running a {@link Validator}
 * over it.
 *
 * @param <T> The target type
 * @author Dave Syer
 */
public class PropertiesConfigurationFactory<T>
        implements FactoryBean<T>, MessageSourceAware, InitializingBean {
    public PropertiesConfigurationFactory(T target) {
    }

    public void setPropertySources(PropertySources propertySources) {

    }

    public void bindPropertiesToTarget() throws BindException {


    }

    public void setTargetName(String targetName) {

    }

    @Override
    public T getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setMessageSource(MessageSource messageSource) {

    }
}
