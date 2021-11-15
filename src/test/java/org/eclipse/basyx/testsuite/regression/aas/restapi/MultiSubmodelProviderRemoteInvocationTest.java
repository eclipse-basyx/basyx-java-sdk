/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.aas.restapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.registry.api.IRegistry;
import org.eclipse.basyx.registry.descriptor.AASDescriptor;
import org.eclipse.basyx.registry.descriptor.ModelUrn;
import org.eclipse.basyx.registry.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.registry.descriptor.parts.Endpoint;
import org.eclipse.basyx.registry.memory.InMemoryRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.testsuite.regression.submodel.restapi.SimpleAASSubmodel;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.protocol.basyx.connector.BaSyxConnectorFactory;
import org.eclipse.basyx.vab.protocol.basyx.server.BaSyxTCPServer;
import org.eclipse.basyx.vab.service.api.BaSyxService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the capability remote submodel invocation from registry
 * in VABMultiSubmodelProvider
 *
 * @author haque
 */
public class MultiSubmodelProviderRemoteInvocationTest {
	private static final IIdentifier AASID1 = new ModelUrn("aas");
	private static final String AASIDSHORT1 = "aasIdShort1";

	private static final IIdentifier REMOTESMID = new ModelUrn("remoteSm");
	private static final String REMOTESMIDSHORT = "remoteSmIdShort";

	private static final IIdentifier LOCALSMID = new ModelUrn("localSm");
	private static final String LOCALSMIDSHORT = "localSmIdShort";

	private static final String REMOTEPATH = "/aas/submodels/" + REMOTESMIDSHORT + "/" + SubmodelProvider.SUBMODEL;

	private List<BaSyxService> services = new ArrayList<>();

	private MultiSubmodelProvider provider;

	// Creating a new AAS Registry
	private IRegistry registry = new InMemoryRegistry();

	@Before
	public void init() {


		// Create descriptors for AAS and submodels
		String aasEndpoint = "basyx://localhost:8000/aas";
		String remoteSmEndpoint = "basyx://localhost:8001/submodel";
		String localSmEndpoint = "basyx://localhost:8000/aas/submodels/" + LOCALSMIDSHORT;

		AASDescriptor aasDesc = new AASDescriptor(AASIDSHORT1, AASID1, Arrays.asList(new Endpoint(aasEndpoint)));
		aasDesc.addSubmodelDescriptor(new SubmodelDescriptor(REMOTESMIDSHORT, REMOTESMID, Arrays.asList(new Endpoint(remoteSmEndpoint))));
		aasDesc.addSubmodelDescriptor(new SubmodelDescriptor(LOCALSMIDSHORT, LOCALSMID, Arrays.asList(new Endpoint(localSmEndpoint))));

		// Register Asset Administration Shells
		registry.register(aasDesc);


		// Create a VABMultiSubmodelProvider using the registry and a http connector
		provider = new MultiSubmodelProvider(registry, new BaSyxConnectorFactory());

		// Create and add an AAS to the provider with same id as the AAS in the registry
		AssetAdministrationShell aas = new AssetAdministrationShell();
		aas.setIdShort(AASIDSHORT1);
		aas.setIdentification(AASID1);
		provider.setAssetAdministrationShell(new AASModelProvider(aas));

		// Create the local SM
		Submodel localSM = new Submodel(LOCALSMIDSHORT, LOCALSMID);
		provider.addSubmodel(new SubmodelProvider(localSM));

		// Create the remote SM
		Submodel remoteSm = new SimpleAASSubmodel(REMOTESMIDSHORT);
		remoteSm.setIdentification(REMOTESMID.getIdType(), REMOTESMID.getId());

		// Setup and start the BaSyx TCP servers
		services.add(new BaSyxTCPServer<>(provider, 8000));
		services.add(new BaSyxTCPServer<>(new SubmodelProvider(remoteSm), 8001));

		services.forEach(b -> b.start());
	}

	/**
	 * Checks if GET is correctly forwarded by checking if the Id of the remote
	 * submodel can be retrieved
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetModelPropertyValue() throws Exception {
		Submodel sm = getRemoteSubmodel();
		assertEquals(sm.getIdentification().getId(), REMOTESMID.getId());
	}

	/**
	 * Checks if a call to "/aas/submodels" correctly includes the remote submodels
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAllSubmodels() {
		Collection<Map<String, Object>> collection = (Collection<Map<String, Object>>) provider.getValue("/aas/submodels");
		Collection<String> smIdShorts = collection.stream().map(m -> Submodel.createAsFacade(m)).map(sm -> sm.getIdShort()).collect(Collectors.toList());
		assertTrue(smIdShorts.contains(REMOTESMIDSHORT));
		assertTrue(smIdShorts.contains(LOCALSMIDSHORT));
		assertTrue(smIdShorts.size() == 2);
	}

	/**
	 * Tries to register a Submodel, not actually push it to the server and try to request all Submodels
	 * This will result in an endless loop if not handled correctly
	 */
	@Test
	public void testGetRegisteredButNotExistentSM() {
		// Create a Descriptor for a Submodel (with a local Endpoint), that is not present on the server
		SubmodelDescriptor smDescriptor = new SubmodelDescriptor("nonexist", new ModelUrn("nonexisting"), Arrays.asList(new Endpoint("basyx://localhost:8000/aas/submodels/nonexist")));

		// Register this SubmodelDescriptor
		registry.registerSubmodelForShell(AASID1, smDescriptor);

		// Try to request all Submodels from the server
		try {
			provider.getValue("/aas/submodels");
			fail();
		} catch (ResourceNotFoundException e) {
		}
	}

	/**
	 * Checks if SET is correctly forwarded by checking if a property value of the
	 * remote submodel can be changed
	 *
	 * @throws Exception
	 */
	@Test
	public void testSetModelPropertyValue() throws Exception {
		int newVal = 0;
		String path = VABPathTools.concatenatePaths(REMOTEPATH, Submodel.SUBMODELELEMENT, SimpleAASSubmodel.INTPROPIDSHORT, Property.VALUE);
		provider.setValue(path, newVal);

		Submodel sm = getRemoteSubmodel();
		assertEquals(newVal, sm.getProperties().get(SimpleAASSubmodel.INTPROPIDSHORT).getValue());
	}

	/**
	 * Checks if CREATE is correctly forwarded by checking if a new property can be
	 * created in the remote submodel
	 *
	 * @throws Exception
	 */
	@Test
	public void testCreateModelPropertyValue() throws Exception {
		Property p = new Property(5);
		String testPropIdShort = "testProperty";
		p.setIdShort(testPropIdShort);

		provider.setValue(REMOTEPATH + "/submodelElements/" + testPropIdShort, p);

		assertTrue(getRemoteSubmodel().getProperties().containsKey(testPropIdShort));
	}

	/**
	 * Checks if DELETE is correctly forwarded by checking if a property can be
	 * deleted in the remote submodel
	 *
	 * @throws Exception
	 */
	@Test
	public void testDeleteModelPropertyValue() throws Exception {
		String path = VABPathTools.concatenatePaths(REMOTEPATH, Submodel.SUBMODELELEMENT, SimpleAASSubmodel.INTPROPIDSHORT);
		provider.deleteValue(path);
		assertFalse(getRemoteSubmodel().getProperties().containsKey(SimpleAASSubmodel.INTPROPIDSHORT));
	}

	/**
	 * Checks if INVOKE is correctly forwarded by checking if an operation can be
	 * called in the remote submodel
	 *
	 * @throws Exception
	 */
	@Test
	public void testInvoke() throws Exception {
		String path = VABPathTools.concatenatePaths(REMOTEPATH, Submodel.SUBMODELELEMENT, SimpleAASSubmodel.OPERATIONSIMPLEIDSHORT, Operation.INVOKE);
		assertTrue((Boolean) provider.invokeOperation(path));
	}

	@SuppressWarnings("unchecked")
	private Submodel getRemoteSubmodel() {
		return Submodel.createAsFacade((Map<String, Object>) provider.getValue(REMOTEPATH));
	}

	@After
	public void tearDown() {
		services.forEach(b -> b.stop());
	}
}
