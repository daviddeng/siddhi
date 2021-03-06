/*
 * Copyright (c) 2005 - 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.wso2.siddhi.core.util;

import org.wso2.siddhi.core.exception.CannotLoadClassException;
import org.wso2.siddhi.core.exception.ExecutionPlanCreationException;
import org.wso2.siddhi.core.util.extension.holder.AbstractExtensionHolder;
import org.wso2.siddhi.query.api.extension.Extension;

public class SiddhiClassLoader {

    private static Object loadClass(String name) throws CannotLoadClassException {
        try {
            return Class.forName(name).newInstance();
        } catch (InstantiationException e) {
            throw new CannotLoadClassException("Cannot restore class: " + name, e);
        } catch (IllegalAccessException e) {
            throw new CannotLoadClassException("Cannot restore class: " + name, e);
        } catch (ClassNotFoundException e) {
            throw new CannotLoadClassException("Cannot restore class: " + name, e);
        } catch (NoClassDefFoundError e) {
            throw new CannotLoadClassException("Cannot restore class: " + name, e);
        }

    }

    private static Object loadClass(Class clazz) throws CannotLoadClassException {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new CannotLoadClassException("Cannot restore class: " + clazz.getName(), e);
        } catch (IllegalAccessException e) {
            throw new CannotLoadClassException("Cannot restore class: " + clazz.getName(), e);
        }
    }


    public static Object loadSiddhiImplementation(String name, Class interfaze) {
        try {
            return SiddhiClassLoader.loadClass(interfaze.getPackage().getName() +
                    "." + name.substring(0, 1).toUpperCase() +
                    name.substring(1) + interfaze.getSimpleName());
        } catch (CannotLoadClassException e) {
            throw new ExecutionPlanCreationException(name + " does not exist in type " + interfaze.getSimpleName(), e, true);
        }
    }

    public static Object loadExtensionImplementation(Extension extension,
                                                     AbstractExtensionHolder extensionHolder) {
        Class clazz = extensionHolder.getExtension(extension.getNamespace(), extension.getFunction());
        if (clazz == null) {
            throw new ExecutionPlanCreationException("No extension exist for " + extension, true);
        }
        try {
            return SiddhiClassLoader.loadClass(clazz);
        } catch (CannotLoadClassException e) {
            throw new ExecutionPlanCreationException("Extension " + clazz.getName() + " cannot be loaded!", true);

        }
    }


}
