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
package org.switchyard.component.camel.core.deploy;

import org.switchyard.component.camel.common.deploy.BaseBindingComponent;
import org.switchyard.component.camel.core.model.direct.v1.V1CamelDirectBindingModel;
import org.switchyard.component.camel.core.model.mock.v1.V1CamelMockBindingModel;
import org.switchyard.component.camel.core.model.seda.v1.V1CamelSedaBindingModel;
import org.switchyard.component.camel.core.model.timer.v1.V1CamelTimerBindingModel;
import org.switchyard.component.camel.core.model.v1.V1CamelBindingModel;

/**
 * Camel core component. Supports camel-core bindings.
 */
public class CamelCoreComponent extends BaseBindingComponent {

    /**
     * Creates new core binding component.
     */
    public CamelCoreComponent() {
        super("CamelCoreComponent",
            V1CamelBindingModel.URI,
            V1CamelDirectBindingModel.DIRECT,
            V1CamelSedaBindingModel.SEDA,
            V1CamelTimerBindingModel.TIMER,
            V1CamelMockBindingModel.MOCK
        );
    }

}
