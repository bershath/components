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
package org.switchyard.component.jca.config.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.component.jca.JCAConstants;
import org.switchyard.component.jca.config.model.InteractionSpecModel;
import org.switchyard.component.jca.config.model.OperationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * V1 Operation model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class V1OperationModel extends BaseModel implements OperationModel {

    /**
     * Constructor.
     */
    public V1OperationModel() {
        super(new QName(JCAConstants.DEFAULT_NAMESPACE, JCAConstants.OPERATION));
        setModelChildrenOrder(JCAConstants.INTERACTION_SPEC);
    }
    
    /**
     * Constructor.
     * 
     * @param config configuration
     * @param desc descriptor
     */
    public V1OperationModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getName() {
        return getModelAttribute(JCAConstants.NAME);
    }
    
    @Override
    public OperationModel setName(String name) {
        setModelAttribute(JCAConstants.NAME, name);
        return this;
    }
    
    @Override
    public InteractionSpecModel getInteractionSpec() {
        return (InteractionSpecModel) getFirstChildModel(JCAConstants.INTERACTION_SPEC);
    }

    @Override
    public OperationModel setInteractionSpec(InteractionSpecModel model) {
        setChildModel(model);
        return this;
    }

}
