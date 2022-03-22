/**
 * 
 */
package org.eclipse.basyx.testsuite.regression.aas.restapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.MultiSubmodelElementProvider;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.testsuite.regression.submodel.restapi.SimpleAASSubmodel;
import org.eclipse.basyx.testsuite.regression.vab.manager.VABConnectionManagerStub;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProviderHelper;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the capability to multiplex of a VABMultiSubmodelProvider
 * 
 * @author schnicke, kuhn
 *
 */
public class MultiSubmodelProviderTest {
	private VABElementProxy proxy;

	private static final String AASIDSHORT = "StubAAS";
	private static final ModelUrn AASURN = new ModelUrn("urn:fhg:es.iese:aas:1:1:myAAS#001");
	private String dynamicPropertyStringValue = "Test";

	@Before
	public void build() {
		VABConnectionManagerStub stub = new VABConnectionManagerStub();
		String urn = "urn:fhg:es.iese:aas:1:1:submodel";
		MultiSubmodelProvider provider = new MultiSubmodelProvider();

		// set dummy aas
		AssetAdministrationShell aas = new AssetAdministrationShell(AASIDSHORT, AASURN, new Asset("assetIdShort", new Identifier(IdentifierType.CUSTOM, "assetId"), AssetKind.INSTANCE));
		provider.setAssetAdministrationShell(new AASModelProvider(aas));
		Submodel dynamicSubmodel = createDynamicSubmodel();
		provider.addSubmodel(new SubmodelProvider(dynamicSubmodel));
		stub.addProvider(urn, "", provider);
		proxy = stub.connectToVABElement(urn);
	}
	
	private Submodel createDynamicSubmodel() {
		Submodel sm = new SimpleAASSubmodel();
		Property stringProp = (Property) sm.getSubmodelElements().get("stringProperty");
		stringProp.set(VABLambdaProviderHelper.createSimple(() -> {
			dynamicPropertyStringValue += dynamicPropertyStringValue;
			return dynamicPropertyStringValue;
		}, null), ValueType.String);
		return sm;
	}

	@Test
	public void invokeExceptionTest() {
		// Invoke operationEx1
		try {
			proxy.invokeOperation("/aas/submodels/SimpleAASSubmodel/submodel/submodelElements/exception1/invokable/invoke");
			fail();
		} catch (ProviderException e) {}

		// Invoke operationEx2
		try {
			proxy.invokeOperation("/aas/submodels/SimpleAASSubmodel/submodel/submodelElements/exception2/invokable/invoke", "prop1");
			fail();
		} catch (ProviderException e) {}
	}
	
	@Test
	public void invalidPathPrefixInvokeTest() {
		try {
			proxy.invokeOperation("/aas/submodelX/SimpleAASSubmodel/submodel/submodelElements/complex/" + Operation.INVOKE, 10, 3);
			fail();
		} catch (MalformedRequestException e) {}
		
		try {
			proxy.invokeOperation("/abc/submodels/SimpleAASSubmodel/submodel/submodelElements/complex/" + Operation.INVOKE, 10, 3);
			fail();
		} catch (MalformedRequestException e) {}
	}

	@Test
	public void invokeTest() {
		// Invoke operation
		assertEquals(7, proxy.invokeOperation("/aas/submodels/SimpleAASSubmodel/submodel/submodelElements/complex/" + Operation.INVOKE, 10, 3));
		assertEquals(true, proxy.invokeOperation("/aas/submodels/SimpleAASSubmodel/submodel/submodelElements/simple/" + Operation.INVOKE));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getTest() {
		AssetAdministrationShell aas = AssetAdministrationShell.createAsFacade((Map<String, Object>) proxy.getValue("/aas"));
		assertEquals(AASIDSHORT, aas.getIdShort());

		getTestRunner("SimpleAASSubmodel");
	}
	
	@Test
	public void getDynamicProperty() {
		Object dynamicValue = proxy.getValue("/aas/submodels/SimpleAASSubmodel/submodel/submodelElements/stringProperty/value");
		Object dynamicValue2 = proxy.getValue("/aas/submodels/SimpleAASSubmodel/submodel/submodelElements/stringProperty/value");
		assertNotEquals(dynamicValue, dynamicValue2);
	}
	
	@Test
	public void invalidPathPrefixGetTest() {
		try {
			proxy.getValue("/aas/submodelX/SimpleAASSubmodel/submodel/");
			fail();
		} catch (MalformedRequestException e) {}
		
		try {
			proxy.getValue("/abc/submodel/SimpleAASSubmodel/submodel/");
			fail();
		} catch (MalformedRequestException e) {}
	}

	@Test
	public void updateSubmodelTest() {
		Submodel sm = new SimpleAASSubmodel("TestSM");
		sm.setIdentification(IdentifierType.CUSTOM, "TestId");
		proxy.setValue("/aas/submodels/" + sm.getIdShort(), sm);

		Submodel sm2 = new SimpleAASSubmodel("TestSM");
		sm2.setIdentification(IdentifierType.CUSTOM, "TestId2");
		proxy.setValue("/aas/submodels/" + sm.getIdShort(), sm2);

		ConnectedAssetAdministrationShell shell = new ConnectedAssetAdministrationShell(proxy.getDeepProxy("/aas"));
		String newId = shell.getSubmodels().get("TestSM").getIdentification().getId();
		assertEquals("TestId2", newId);
	}
	
	@Test
	public void invalidPathPrefixSetTest() {
		Submodel sm = new SimpleAASSubmodel("TestSM");
		sm.setIdentification(IdentifierType.CUSTOM, "TestId");
		try {
			proxy.setValue("/aas/submodelX/" + sm.getIdShort(), sm);
			fail();
		} catch (MalformedRequestException e) {}
		
		try {
			proxy.setValue("/abc/submodels/" + sm.getIdShort(), sm);
			fail();
		} catch (MalformedRequestException e) {}
	}

	@Test
	public void createDeleteSubmodelTest() {
		Submodel sm = new SimpleAASSubmodel("TestSM");
		sm.setIdentification(IdentifierType.CUSTOM, "TestId");
		proxy.setValue("/aas/submodels/" + sm.getIdShort(), sm);

		getTestRunner("TestSM");

		// Ensure that the Submodel References were updated
		ConnectedAssetAdministrationShell shell = new ConnectedAssetAdministrationShell(proxy.getDeepProxy("/aas"));
		Collection<IReference> refs = shell.getSubmodelReferences();
		assertEquals(2, refs.size());
		assertEquals(sm.getReference(), refs.iterator().next());

		proxy.deleteValue("/aas/submodels/TestSM");

		// Ensure that the Submodel Reference was removed again
		assertEquals(1, shell.getSubmodelReferences().size());
		assertFalse(shell.getSubmodelReferences().contains(sm.getReference()));

		try {
			proxy.getValue("/aas/submodels/TestSM");
			fail();
		} catch (ProviderException e) {
			// Expected
		}
	}
	
	/**
	 * Tests if SubmodelElements are returned as a List in Submodel and SubmodelElementCollection and not as Map
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void submodelElementsAreContainedAsList() {
		List<Map<String, Object>> submodels = (List<Map<String, Object>>) proxy.getValue("/aas/submodels");
		Map<String, Object> submodel = submodels.get(0);
		assertTrue(submodel.get(Submodel.SUBMODELELEMENT) instanceof List<?>);
		List<Map<String, Object>> smCollections = ((List<Map<String, Object>>) submodel.get(Submodel.SUBMODELELEMENT))
				.stream()
				.filter(e -> ModelType.createAsFacade(e).getName().equals(SubmodelElementCollection.MODELTYPE))
				.collect(Collectors.toList());
		assertTrue(smCollections.get(0).get(Property.VALUE) instanceof List<?>);
	}
	
	@Test
	public void invalidPathPrefixDeleteTest() {
		Submodel sm = new SimpleAASSubmodel("TestSM");
		sm.setIdentification(IdentifierType.CUSTOM, "TestId");
		try {
			proxy.deleteValue("/aas/submodelX/SimpleAASSubmodel");
			fail();
		} catch (MalformedRequestException e) {}
		
		try {
			proxy.deleteValue("/aas/submodelX/SimpleAASSubmodel");
			fail();
		} catch (MalformedRequestException e) {}
	}


	private void getTestRunner(String smId) {
		// Get property value
		Integer value = (Integer) proxy
				.getValue("/aas/submodels/" + smId + "/" + SubmodelProvider.SUBMODEL + "/" + MultiSubmodelElementProvider.ELEMENTS + "/integerProperty/value");
		assertEquals(123, value.intValue());

		// Get property value with /submodel suffix
		value = (Integer) proxy.getValue("/aas/submodels/" + smId + "/" + SubmodelProvider.SUBMODEL + "/" + MultiSubmodelElementProvider.ELEMENTS + "/integerProperty/value");
		assertEquals(123, value.intValue());
	}
}
