package org.eclipse.basyx.testsuite.regression.aas.factory.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.AASHTTPServerResource;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class TestAASJSONContainsAssetReference {
	private final static String AAS_ID_SHORT = "aas01";
	private final static String ASSET_ID_SHORT = "asset1IdShort";
	private final static String ASSET_ID = "asset1";
	private final static IIdentifier aasId = new Identifier(IdentifierType.CUSTOM, "aas01");
	private final static String HTTP_REST_PATH = "http://localhost:8080/basys.sdk/shells";
	private final static String EXPECTED_ELEMENT_TYPE = "Asset";
	private final static String EXPECTED_ELEMENT_VALUE = "asset1";
	private final static String ASSET_KEYS = "keys";
	private final static String ASSET_KEY_IDTYPE = "type";
	private final static String ASSET_KEY_IDVALUE = "value";
	
	private static AssetAdministrationShell aas1 = new AssetAdministrationShell(AAS_ID_SHORT, aasId, new Asset(ASSET_ID_SHORT, new Identifier(IdentifierType.CUSTOM, ASSET_ID), AssetKind.INSTANCE));
	private static AASAggregator aggregator = new AASAggregator(); 
	private static BaSyxContext context = new BaSyxContext("/basys.sdk", System.getProperty("java.io.tmpdir")).addServletMapping("/*", new VABHTTPInterface<AASAggregatorProvider>(new AASAggregatorProvider(aggregator)));
	private GSONTools gsonTools = new GSONTools(new DefaultTypeFactory());

	@ClassRule
	public static AASHTTPServerResource res = new AASHTTPServerResource(context);

	@BeforeClass
	public static void setUp() {
		AASAggregatorProxy aggregatorProxy = new AASAggregatorProxy(HTTP_REST_PATH);
		aggregatorProxy.createAAS(aas1);
	}

	@Test
	public void testGetAssetReferenceFromAASJSON() {
		Builder request = buildRequest(HTTP_REST_PATH);
		Response rsp = request.get();
		String aasJSON = rsp.readEntity(String.class);
		assertAASJSONContainsAssetReference(aasJSON);
	}

	@SuppressWarnings("unchecked")
	private void assertAASJSONContainsAssetReference(String aasJSON) {
		ArrayList<Object> aasList = (ArrayList<Object>) gsonTools.deserialize(aasJSON);
		LinkedHashMap<String, Object> aasMap = (LinkedHashMap<String, Object>)aasList.get(0);
		LinkedHashMap<String, Object> assetMap = (LinkedHashMap<String, Object>)aasMap.get("asset");
		assertTrue(assetMap.containsKey(ASSET_KEYS));

		ArrayList<Object> assetKeys = (ArrayList<Object>) assetMap.get(ASSET_KEYS);
		LinkedHashMap<String, Object> keyMap = (LinkedHashMap<String, Object>) assetKeys.get(0);
		assertEquals(EXPECTED_ELEMENT_TYPE, keyMap.get(ASSET_KEY_IDTYPE));
		assertEquals(EXPECTED_ELEMENT_VALUE, keyMap.get(ASSET_KEY_IDVALUE));
	}

	private Builder buildRequest(String wsURL) {
		// Called URL
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(wsURL);

		// Build request, set JSON encoding
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON + ";charset=UTF-8");

		// Return JSON request
		return request;
	}


}
