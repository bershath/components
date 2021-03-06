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

package org.switchyard.tools.forge.common;

import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresPackagingType;
import org.switchyard.tools.forge.AbstractFacet;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;

/**
 * Forge facet for Common Component functionality.
 */
@Alias("switchyard.common")
@RequiresFacet({ DependencyFacet.class, PackagingFacet.class, SwitchYardFacet.class })
@RequiresPackagingType(PackagingType.JAR)
public class CommonFacet extends AbstractFacet {
    
    private static final String COMMON_MAVEN_ID = 
        "org.switchyard.components:switchyard-component-common";
    
    @Override
    public boolean install() {
        installDependencies();
        return true;
    }
    
    /**
     * Create a new Common Facet.
     */
    public CommonFacet() {
        super(COMMON_MAVEN_ID);
    }
}
