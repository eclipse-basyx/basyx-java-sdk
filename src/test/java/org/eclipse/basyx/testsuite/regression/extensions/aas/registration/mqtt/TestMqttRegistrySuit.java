/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.testsuite.regression.extensions.aas.registration.mqtt;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.extensions.shared.mqtt.MqttEventService;
import org.eclipse.basyx.registry.api.IRegistry;
import org.eclipse.basyx.registry.descriptor.AASDescriptor;
import org.eclipse.basyx.registry.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.registry.descriptor.parts.Endpoint;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.MqttTestListener;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;

/**
 * Test suit for mqtt registry tests.
 *
 * @author haque, fischer
 *
 */
public abstract class TestMqttRegistrySuit {
	protected static final String AASID = "aasid1";
	protected static final String SUBMODELID = "submodelid1";
	protected static final String AASENDPOINT = "http://localhost:8080/aasList/" + AASID + "/aas";
	protected static final Identifier AASIDENTIFIER = new Identifier(IdentifierType.IRI, AASID);
	protected static final Identifier SUBMODELIDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODELID);

	protected static Server mqttBroker;
	protected MqttTestListener listener;
	protected IRegistry proxyAPI;

	/**
	 * Sets up the MQTT broker and AASRegistryService for tests
	 */
	@BeforeClass
	public static void setUpAbstractClass() throws IOException {
		// Start MQTT broker
		mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);

	}

	protected abstract IRegistry getAPI() throws MqttException;

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.stopServer();
	}

	@Before
	public void setUp() {
		try {
			proxyAPI = getAPI();
		} catch (MqttException e) {
		}

		AssetAdministrationShell shell = new AssetAdministrationShell(AASID, AASIDENTIFIER, new Asset("assetid1", new Identifier(IdentifierType.IRI, "assetid1"), AssetKind.INSTANCE));
		AASDescriptor aasDescriptor = new AASDescriptor(shell, new Endpoint(AASENDPOINT));
		proxyAPI.register(aasDescriptor);

		Submodel submodel = new Submodel(SUBMODELID, SUBMODELIDENTIFIER);
		String submodelEndpoint = AASENDPOINT + "/submodels/" + SUBMODELID + "/submodel";
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(submodel, new Endpoint(submodelEndpoint));
		proxyAPI.registerSubmodelForShell(AASIDENTIFIER, submodelDescriptor);

		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);
	}

	@After
	public void tearDown() {
		deleteAASFromRegistryIfExisting();
		mqttBroker.removeInterceptHandler(listener);
	}

	private void deleteAASFromRegistryIfExisting() {
		try {
			proxyAPI.deleteShell(AASIDENTIFIER);
		} catch (ProviderException e) {
		}
	}

	@Test
	public void testRegisterAAS() {
		String newAASId = "aasid2";
		Identifier newIdentifier = new Identifier(IdentifierType.IRI, newAASId);
		AssetAdministrationShell shell = new AssetAdministrationShell(newAASId, newIdentifier, new Asset("assetid1", new Identifier(IdentifierType.IRI, "assetid2"), AssetKind.INSTANCE));
		String aasEndpoint = "http://localhost:8080/aasList/" + newAASId + "/aas";

		AASDescriptor aasDescriptor = new AASDescriptor(shell, new Endpoint(aasEndpoint));
		proxyAPI.register(aasDescriptor);

		assertEquals(newAASId, listener.lastPayload);
		assertEquals(MqttEventService.TOPIC_REGISTERAAS, listener.lastTopic);
	}

	@Test
	public void testRegisterSubmodel() {
		String submodelid = "submodelid2";
		Identifier newSubmodelIdentifier = new Identifier(IdentifierType.IRI, submodelid);
		Submodel submodel = new Submodel(submodelid, newSubmodelIdentifier);
		String submodelEndpoint = AASENDPOINT + "/submodels/" + submodelid + "/submodel";
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(submodel, new Endpoint(submodelEndpoint));

		proxyAPI.registerSubmodelForShell(AASIDENTIFIER, submodelDescriptor);

		assertEquals(MqttEventService.concatShellSubmodelId(AASIDENTIFIER, newSubmodelIdentifier), listener.lastPayload);
		assertEquals(MqttEventService.TOPIC_REGISTERSUBMODEL, listener.lastTopic);
	}

	@Test
	public void testDeleteAAS() {
		proxyAPI.deleteShell(AASIDENTIFIER);

		assertEquals(AASID, listener.lastPayload);
		assertEquals(MqttEventService.TOPIC_DELETEAAS, listener.lastTopic);
	}

	@Test
	public void testDeleteSubmodel() {
		proxyAPI.deleteSubmodelFromShell(AASIDENTIFIER, SUBMODELIDENTIFIER);

		assertEquals(MqttEventService.concatShellSubmodelId(AASIDENTIFIER, SUBMODELIDENTIFIER), listener.lastPayload);
		assertEquals(MqttEventService.TOPIC_DELETESUBMODEL, listener.lastTopic);
	}
}
