/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.component.camel.jpa.model.v1;

import org.switchyard.component.camel.common.marshaller.BaseModelMarshaller;
import org.switchyard.component.camel.common.marshaller.ModelCreator;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

import static org.switchyard.component.camel.jpa.model.Constants.JPA_NAMESPACE_V1;

/**
 * Jpa model marshaller.
 */
public class V1CamelJpaModelMarshaller extends BaseModelMarshaller {

    /**
     * Creates new marshaller.
     * 
     * @param desc Descriptor
     */
    public V1CamelJpaModelMarshaller(Descriptor desc) {
        super(desc, JPA_NAMESPACE_V1);

        registerBinding(V1CamelJpaBindingModel.JPA, new ModelCreator<V1CamelJpaBindingModel>() {
            @Override
            public V1CamelJpaBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelJpaBindingModel(config, descriptor);
            }
        });
    }

}
