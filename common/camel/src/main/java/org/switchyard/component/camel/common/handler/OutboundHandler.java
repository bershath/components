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
package org.switchyard.component.camel.common.handler;

import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.SwitchYardException;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.model.CamelBindingModel;
import org.switchyard.component.camel.common.transaction.TransactionHelper;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

/**
 * A handler that is capable of calling Apache Camel components and returning responses 
 * from Camel to SwitchYard.
 * <p>
 * The typical usage would be when a POJO has a field with a reference annotation. SwitchYard 
 * can inject a proxy instance which will invoke a Camel endpoint. It is an instance of this 
 * class that will handle the invocation of the Camel endpoint.
 * 
 * @author Daniel Bevenius
 *
 */
public class OutboundHandler extends BaseServiceHandler {

    private static Logger _logger = Logger.getLogger(OutboundHandler.class);

    private final MessageComposer<CamelBindingData> _messageComposer;
    private final ProducerTemplate _producerTemplate;
    private final SwitchYardCamelContext _camelContext;
    private final String _bindingName;
    private final String _referenceName;
    private final String _uri;

    /**
     * Constructor that will create a default {@link ProducerTemplate}.
     * 
     * @param binding The Camel binding.
     * @param context The {@link CamelContext}.
     * @param messageComposer the MessageComposer this handler should use
     * @param domain the service domain
     */
    public OutboundHandler(final CamelBindingModel binding, final SwitchYardCamelContext context, MessageComposer<CamelBindingData> messageComposer, ServiceDomain domain) {
        this(binding, context, messageComposer, null, domain);
    }

    /**
     * Constructor that allows for specifying a specific {@link ProducerTemplate}.
     * 
     * @param binding The Camel binding.
     * @param context The {@link CamelContext}.
     * @param messageComposer the MessageComposer this handler should use.
     * @param producerTemplate The {@link ProducerTemplate} to be used by this handler.
     * @param domain the service domain
     */
    public OutboundHandler(final CamelBindingModel binding, final SwitchYardCamelContext context, MessageComposer<CamelBindingData> messageComposer, ProducerTemplate producerTemplate, ServiceDomain domain) {
        super(domain);
        if (binding == null) {
            throw new IllegalArgumentException("binding argument must not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("camelContext argument must not be null");
        }
        if (binding.getComponentURI() == null) {
            throw new IllegalArgumentException("binding uri must not be null");
        }
        _uri = binding.getComponentURI().toString();
        _bindingName = binding.getName();
        _referenceName = binding.getReference().getName();
        _camelContext = context;
        _messageComposer = messageComposer;
        _producerTemplate = producerTemplate == null ? _camelContext.createProducerTemplate() : producerTemplate;

        TransactionHelper.useTransactionManager(_uri, context);
    }

    /**
     * Starts the {@link ProducerTemplate}.
     */
    @Override
    protected void doStart() {
        try {
            _producerTemplate.start();
            _logger.debug("Started producer template for " + _uri);
        } catch (Exception e) {
            throw new SwitchYardException("Failed to start Camel producer template for " + _uri, e);
        }
    }

    /**
     * Stops the {@link ProducerTemplate}.
     */
    @Override
    protected void doStop() {
        try {
            _producerTemplate.stop();
            _logger.debug("Stopped producer template for " + _uri);
        } catch (Exception ex) {
            throw new SwitchYardException("Failed to stop Camel producer template for " + _uri, ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        // identify ourselves
        exchange.getContext().setProperty(ExchangeCompletionEvent.GATEWAY_NAME, _bindingName, Scope.EXCHANGE)
                .addLabels(BehaviorLabel.TRANSIENT.label());

        if (getState() != State.STARTED) {
            throw new HandlerException(String.format("Reference binding \"%s/%s\" is not started.", _referenceName,
                    _bindingName));
        }
        if (isInOnly(exchange)) {
            handleInOnly(exchange);
        } else {
            handleInOut(exchange);
        }
    }

    private boolean isInOnly(final Exchange exchange) {
        return exchange.getContract().getConsumerOperation().getExchangePattern() == ExchangePattern.IN_ONLY;
    }

    private void handleInOnly(final Exchange exchange) throws HandlerException {
        try {
            _producerTemplate.send(_uri, createProcessor(exchange));
        } catch (final CamelExecutionException e) {
            throw new HandlerException(e);
        }
    }

    private void handleInOut(final Exchange switchyardExchange) throws HandlerException {
        final org.apache.camel.Exchange camelExchange = _producerTemplate.request(_uri, createProcessor(switchyardExchange));
        Exception camelException = camelExchange.getException();

        if (!camelExchange.isFailed()) {
            sendResponseToSwitchyard(switchyardExchange, camelExchange.getOut().getBody());

        } else {
            QName faultName = switchyardExchange.getContract().getProviderOperation().getFaultType();
            Class<?> declaredFault = faultName != null && QNameUtil.isJavaMessageType(faultName) ? QNameUtil.toJavaMessageType(faultName) : null;

            Object camelFault = camelException;
            if (camelFault == null) {
                if (camelExchange.hasOut() && camelExchange.getOut().isFault()) {
                    // Use Out body as a fault content if camelExchange.getException() returns null
                    camelFault = camelExchange.getOut().getBody();
                }
            }
            
            if (camelFault != null && declaredFault != null && declaredFault.isAssignableFrom(camelFault.getClass())) {
                Message msg = switchyardExchange.createMessage().setContent(camelFault);
                switchyardExchange.sendFault(msg);
            } else if (camelFault instanceof Throwable) {
                throw new HandlerException(Throwable.class.cast(camelFault));
            } else {
                throw new HandlerException("camel exchange failed without an exception: " + camelFault);
            }
        }
    }

    private void sendResponseToSwitchyard(final Exchange switchyardExchange, final Object payload) {
        switchyardExchange.send(switchyardExchange.createMessage().setContent(payload));
    }

    private Processor createProcessor(final Exchange switchyardExchange) {
        return new DefaultProcessor(_messageComposer, switchyardExchange);
    }

    /**
     * Return the CamelContext used by this handler.
     * @return CamelContext
     */
    public CamelContext getCamelContext() {
        return _camelContext;
    }

    /**
     * Return the Camel endpoint URI used by this handler.
     * @return Camel endpoint URI
     */
    public String getUri() {
        return _uri;
    }

}
