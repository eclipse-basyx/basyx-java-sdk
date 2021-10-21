/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.opcua.connector;

import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.ConnectorFactory;

/**
 * OPC UA connector factory.
 */
public class OpcUaConnectorFactory extends ConnectorFactory {

    private final ClientConfiguration defaultConfiguration;

    /**
     * Creates a new connector factory.
     */
    public OpcUaConnectorFactory() {
        super();
        defaultConfiguration = null;
    }

    /**
     * Creates a new connector factory which applies a default client configuration to connectors.
     *
     * <p>
     * Note, that the configuration is copied internally, so changes made to the object after creating
     * the factory do not apply to connectors created by it.
     *
     * @param defaultConfiguration The default connector configuration.
     */
    public OpcUaConnectorFactory(ClientConfiguration defaultConfiguration) {
        super();
        this.defaultConfiguration = defaultConfiguration;
    }

    /**
     * Creates a new {@link OpcUaConnector} for the given endpoint URL.
     *
     * <p>
     * If a default configuration has been set, it will be applied to the connector before it is
     * returned.
     *
     * @return A new {@link OpcUaConnector} for the given endpoint URL.
     */
    @Override
    protected IModelProvider createProvider(String addr) {
        OpcUaConnector c = new OpcUaConnector(addr);

        if (defaultConfiguration != null) {
            c.getClient().setConfiguration(defaultConfiguration);
        }

        return c;
    }
}
