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
package org.switchyard.component.camel.sftp.model.v1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.apache.camel.component.file.remote.SftpEndpoint;
import org.junit.Test;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelSftpBindingModel}.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelSftpBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelSftpBindingModel, SftpEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-sftp-binding-beans.xml";

    private static final String DIRECTORY = "test";
    private static final String HOST = "localhost";
    private static final int PORT = 9022;
    private static final String KNOWN_HOSTS = "known_hosts";
    private static final String PRIVATE_KEY = "my.key";
    private static final String PRIVATE_KEY_PASSPHRASE = "test";

    private static final String CAMEL_URI = "sftp://localhost:9022/test?knownHostsFile=known_hosts"
        + "&privateKeyFile=my.key&privateKeyFilePassphrase=test";

    public V1CamelSftpBindingModelTest() {
        super(SftpEndpoint.class, CAMEL_XML);
    }

    @Test
    public void validateCamelBindingModelWithBeanElement() throws Exception {
        final V1CamelSftpBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();

        assertTrue(validateModel.getMessage(), validateModel.isValid());
    }

    @Override
    protected void createModelAssertions(V1CamelSftpBindingModel model) {
        assertEquals(DIRECTORY, model.getDirectory());
        assertEquals(HOST, model.getHost());
        assertEquals(KNOWN_HOSTS, model.getKnownHostsFile());
        assertEquals(PRIVATE_KEY, model.getPrivateKeyFile());
        assertEquals(PRIVATE_KEY_PASSPHRASE, model.getPrivateKeyFilePassphrase());
    }

    @Override
    protected V1CamelSftpBindingModel createTestModel() {
        V1CamelSftpBindingModel model = (V1CamelSftpBindingModel) new V1CamelSftpBindingModel()
            .setDirectory(DIRECTORY)
            .setHost(HOST)
            .setPort(PORT);

        return model.setKnownHostsFile(KNOWN_HOSTS)
            .setPrivateKeyFile(PRIVATE_KEY)
            .setPrivateKeyFilePassphrase(PRIVATE_KEY_PASSPHRASE);
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }


}
