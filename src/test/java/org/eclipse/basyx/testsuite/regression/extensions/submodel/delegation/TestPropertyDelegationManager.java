/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.submodel.delegation;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.basyx.extensions.submodel.delegation.DelegatingDecoratingSubmodelAPIFactory;
import org.eclipse.basyx.extensions.submodel.delegation.DelegatingSubmodelAPI;
import org.eclipse.basyx.extensions.submodel.delegation.PropertyDelegationManager;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedSubmodel;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.ConnectedSubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnector;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests Submodel with delegated Property
 *
 * @author danish
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TestPropertyDelegationManager {
	private static final String SMEC_ID_SHORT = "smc";
	private static final String INNER_SMEC_ID_SHORT = "innerSmc";

	@Test
	public void delegatedPropertyIsTransformed() {
		Property delegated = DelegationTestHelper.createDelegatedProperty();
		
		Submodel submodel = DelegationTestHelper.createSubmodel();
		submodel.addSubmodelElement(delegated);

		PropertyDelegationManager delegatedProvider = createDelegatedProvider();
		delegatedProvider.handleSubmodel(submodel);
		
		assertPropertyValueIsAsExpected(DelegationTestHelper.EXPECTED_VALUE, submodel, delegated);
	}
	
	@Test
	public void normalPropertyIsNotTransformed() {
		Property property = new Property("normalProperty", DelegationTestHelper.EXPECTED_VALUE);

		property.setQualifiers(createNonDelegatingQualifiers());
		
		Submodel submodel = DelegationTestHelper.createSubmodel();
		submodel.addSubmodelElement(property);

		PropertyDelegationManager delegatedProvider = new PropertyDelegationManager(new HTTPConnectorFactory());
		delegatedProvider.handleSubmodel(submodel);
		
		assertPropertyValueIsAsExpected(DelegationTestHelper.EXPECTED_VALUE, submodel, property);
	}
	
	@Test
	public void collectionWithDelegatedPropertyIsTransformed() {
		Property delegated = DelegationTestHelper.createDelegatedProperty();

		SubmodelElementCollection smc = createSubmodelElementCollection(SMEC_ID_SHORT);
		smc.addSubmodelElement(delegated);
		
		Submodel submodel = DelegationTestHelper.createSubmodel();
		submodel.addSubmodelElement(smc);

		PropertyDelegationManager delegatedProvider = createDelegatedProvider();
		delegatedProvider.handleSubmodel(submodel);

		assertPropertyWithinCollectionIsDelegated(DelegationTestHelper.EXPECTED_VALUE, submodel, delegated, smc);
	}
	
	@Test
	public void nestedCollectionWithDelegatedPropertyIsTransformed() {
		Property delegated = DelegationTestHelper.createDelegatedProperty();

		SubmodelElementCollection submodelElementCollection = createNestedSubmodelElementCollection(delegated);
		
		Submodel submodel = DelegationTestHelper.createSubmodel();
		submodel.addSubmodelElement(submodelElementCollection);

		PropertyDelegationManager delegatedProvider = createDelegatedProvider();
		delegatedProvider.handleSubmodel(submodel);

		assertPropertyWithinNestedCollectionIsDelegated(DelegationTestHelper.EXPECTED_VALUE, submodel, delegated);
	}

	@Test
	public void delegatedPropertyWithinSubmodelIsTransformed() {
		Submodel submodel = DelegationTestHelper.createSubmodel();
		
		SubmodelElement delegated = DelegationTestHelper.createDelegatedProperty();
		
		submodel.addSubmodelElement(delegated);
		
		DelegatingDecoratingSubmodelAPIFactory decoratingSubmodelAPIFactory = createMockedDelegationDecoratingSubmodelAPIFactory(submodel);
				
		ISubmodelAPI submodelAPI = decoratingSubmodelAPIFactory.getSubmodelAPI(submodel);
		
		assertEquals(DelegationTestHelper.EXPECTED_VALUE, submodelAPI.getSubmodel().getSubmodelElement(delegated.getIdShort()).getValue());
	}
	
	@Test(expected = MalformedRequestException.class)
	public void exceptionIsThrownWhenUpdateRequestOnDelegatedProperty() {
		Submodel submodel = DelegationTestHelper.createSubmodel();
		
		SubmodelElement delegated = DelegationTestHelper.createDelegatedProperty();
		
		submodel.addSubmodelElement(delegated);
		
		DelegatingDecoratingSubmodelAPIFactory decoratingSubmodelAPIFactory = createMockedDelegationDecoratingSubmodelAPIFactory(submodel);
				
		ISubmodelAPI submodelAPI = decoratingSubmodelAPIFactory.getSubmodelAPI(submodel);
		
		submodelAPI.updateSubmodelElement(delegated.getIdShort(), 13);
	}
	
	@Test
	public void addPropertyToSubmodelWithDelegatingDecoratedSubmodelAPI() {
		Submodel submodel = DelegationTestHelper.createSubmodel();
		
		SubmodelElement delegated = DelegationTestHelper.createDelegatedProperty();
		
		DelegatingDecoratingSubmodelAPIFactory decoratingSubmodelAPIFactory = createMockedDelegationDecoratingSubmodelAPIFactory(submodel);
				
		ISubmodelAPI submodelAPI = decoratingSubmodelAPIFactory.getSubmodelAPI(submodel);
		
		submodelAPI.addSubmodelElement(delegated);
		
		assertEquals(DelegationTestHelper.EXPECTED_VALUE, submodelAPI.getSubmodel().getSubmodelElement(delegated.getIdShort()).getValue());
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void deleteDelegatedPropertyWithDelegatingDecoratedSubmodelAPI() {		
		SubmodelElement delegated = DelegationTestHelper.createDelegatedProperty();
		
		Submodel submodel = DelegationTestHelper.createSubmodel();
		submodel.addSubmodelElement(delegated);
		
		DelegatingDecoratingSubmodelAPIFactory decoratingSubmodelAPIFactory = createMockedDelegationDecoratingSubmodelAPIFactory(submodel);
				
		ISubmodelAPI submodelAPI = decoratingSubmodelAPIFactory.getSubmodelAPI(submodel);
		
		submodelAPI.deleteSubmodelElement(delegated.getIdShort());
		
		submodelAPI.getSubmodelElement(delegated.getIdShort());
	}
	
	@Test
	public void getSubmodelElementsWithDelegatingDecoratedSubmodelAPI() {		
		SubmodelElement property = new Property("testProperty", "test");
		
		Submodel submodel = DelegationTestHelper.createSubmodel();
		submodel.addSubmodelElement(property);
		
		DelegatingDecoratingSubmodelAPIFactory decoratingSubmodelAPIFactory = createMockedDelegationDecoratingSubmodelAPIFactory(submodel);
				
		ISubmodelAPI submodelAPI = decoratingSubmodelAPIFactory.getSubmodelAPI(submodel);
		
		assertEquals(1, submodelAPI.getSubmodelElements().size());
	}
	
	@Test
	public void getSubmodelElementValueWithDelegatingDecoratedSubmodelAPI() {		
		SubmodelElement property = new Property("testProperty", DelegationTestHelper.EXPECTED_VALUE);
		
		Submodel submodel = DelegationTestHelper.createSubmodel();
		submodel.addSubmodelElement(property);
		
		DelegatingDecoratingSubmodelAPIFactory decoratingSubmodelAPIFactory = createMockedDelegationDecoratingSubmodelAPIFactory(submodel);
				
		ISubmodelAPI submodelAPI = decoratingSubmodelAPIFactory.getSubmodelAPI(submodel);
		
		assertEquals(DelegationTestHelper.EXPECTED_VALUE, submodelAPI.getSubmodelElementValue(property.getIdShort()));
	}
	
	private void assertPropertyValueIsAsExpected(int expectedValue, Submodel submodel, Property delegated) {
		ConnectedSubmodel connectedSm = createConnectedSubmodel(submodel);
		
		assertEquals(expectedValue, connectedSm.getSubmodelElement(delegated.getIdShort()).getValue());
	}

	private void assertPropertyWithinCollectionIsDelegated(int expectedValue, Submodel submodel, Property delegated,
			SubmodelElementCollection smc) {
		ConnectedSubmodel connectedSm = createConnectedSubmodel(submodel);

		assertEquals(expectedValue, ((ConnectedSubmodelElementCollection) connectedSm.getSubmodelElement(smc.getIdShort())).getSubmodelElement(delegated.getIdShort()).getValue());
	}

	private void assertPropertyWithinNestedCollectionIsDelegated(int expectedValue, Submodel submodel, Property delegated) {
		ConnectedSubmodel connectedSm = createConnectedSubmodel(submodel);
		
		ConnectedSubmodelElementCollection connectedSubmodelElementCollection = (ConnectedSubmodelElementCollection) connectedSm.getSubmodelElement(SMEC_ID_SHORT);
		ConnectedSubmodelElementCollection innerConnectedSubmodelElementCollection = (ConnectedSubmodelElementCollection) connectedSubmodelElementCollection.getSubmodelElement(INNER_SMEC_ID_SHORT);
		
		assertEquals(expectedValue, innerConnectedSubmodelElementCollection.getSubmodelElement(delegated.getIdShort()).getValue());
	}
	
	private ConnectedSubmodel createConnectedSubmodel(Submodel submodel) {
		SubmodelProvider provider = new SubmodelProvider(new VABLambdaProvider(submodel));

		ConnectedSubmodel connectedSm = new ConnectedSubmodel(new VABElementProxy("/submodel", provider));
		
		return connectedSm;
	}
	
	private SubmodelElementCollection createNestedSubmodelElementCollection(Property delegated) {
		SubmodelElementCollection innerSubmodelElementCollection = createSubmodelElementCollection(INNER_SMEC_ID_SHORT);
		innerSubmodelElementCollection.addSubmodelElement(delegated);

		SubmodelElementCollection submodelElementCollection = createSubmodelElementCollection(SMEC_ID_SHORT);
		submodelElementCollection.addSubmodelElement(innerSubmodelElementCollection);
		
		return submodelElementCollection;
	}
	
	private DelegatingDecoratingSubmodelAPIFactory createMockedDelegationDecoratingSubmodelAPIFactory(Submodel submodel) {
		DelegatingDecoratingSubmodelAPIFactory decoratingSubmodelAPIFactory = Mockito.mock(DelegatingDecoratingSubmodelAPIFactory.class);
		
		HTTPConnectorFactory httpConnectorFactory = createMockedConnectorFactory(DelegationTestHelper.EXPECTED_VALUE, DelegationTestHelper.SERVER_URL, DelegationTestHelper.ENDPOINT);
		
		VABSubmodelAPIFactory vabSubmodelAPIFactory = new VABSubmodelAPIFactory();
		
		DelegatingSubmodelAPI delegatingSubmodelAPI = new DelegatingSubmodelAPI(vabSubmodelAPIFactory.create(submodel),new PropertyDelegationManager(httpConnectorFactory));
		
		Mockito.when(decoratingSubmodelAPIFactory.getSubmodelAPI(submodel)).thenReturn(delegatingSubmodelAPI);
		
		return decoratingSubmodelAPIFactory;
	}

	private PropertyDelegationManager createDelegatedProvider() {
		HTTPConnectorFactory mockedFactory = createMockedConnectorFactory(DelegationTestHelper.EXPECTED_VALUE, DelegationTestHelper.SERVER_URL, DelegationTestHelper.ENDPOINT);
		return new PropertyDelegationManager(mockedFactory);
	}

	private Collection<IConstraint> createNonDelegatingQualifiers() {
		Qualifier nonDelegation = new Qualifier("arbitrary", ValueType.String);
		nonDelegation.setValue("alsoArbitrary");

		return Collections.singleton(nonDelegation);
	}

	private SubmodelElementCollection createSubmodelElementCollection(String idShort) {
		SubmodelElementCollection innerSubmodelElementCollection = new SubmodelElementCollection(idShort);
		return innerSubmodelElementCollection;
	}

	private HTTPConnectorFactory createMockedConnectorFactory(int expectedValue, String serverUrl, String endpoint) {
		HTTPConnectorFactory mockedFactory = Mockito.mock(HTTPConnectorFactory.class);

		HTTPConnector mockedConnector = Mockito.mock(HTTPConnector.class);

		Mockito.when(mockedFactory.create(serverUrl)).thenReturn(new JSONConnector(mockedConnector));

		Mockito.when(mockedConnector.getValue(endpoint)).thenReturn(String.valueOf(expectedValue));
		return mockedFactory;
	}
}
