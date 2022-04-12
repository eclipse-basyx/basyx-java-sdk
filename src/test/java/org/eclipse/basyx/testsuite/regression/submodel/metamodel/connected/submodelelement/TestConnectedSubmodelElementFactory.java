/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.ConnectedCapability;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.ConnectedSubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.ConnectedSubmodelElementFactory;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedBlob;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedFile;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedMultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedProperty;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedRange;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedReferenceElement;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.event.ConnectedBasicEvent;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.operation.ConnectedOperation;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.relationship.ConnectedRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.Capability;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.Blob;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.ReferenceElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.event.BasicEvent;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;
import org.eclipse.basyx.submodel.restapi.MultiSubmodelElementProvider;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.support.TypeDestroyingProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if the ConnectedSubmodelElementFactory
 * handles all ISubmodelElement Objects correctly.
 * 
 * @author conradi
 *
 */
public class TestConnectedSubmodelElementFactory {

	private static final String PROPERTY_ID = "PropertyId";
	private static final String BLOB_ID = "BlobId";
	private static final String FILE_ID = "FileId";
	private static final String MLP_ID = "MLPId";
	private static final String RANGE_ID = "RangeId";
	private static final String REFELEMENT_ID = "RefElementId";
	private static final String BASICEVENT_ID = "BasicEventId";
	private static final String CAPABILITY_ID = "CapabilityId";
	private static final String OPERATION_ID = "OperationId";
	private static final String RELELEMENT_ID = "RelElementId";
	private static final String SMELEMCOLLECTION_ID = "SmElemCollectionId";	
	
	VABElementProxy proxy;
	
	@Before
	public void build() {
		
		Map<String, Object> dataElements = buildDataElements();
		
		Map<String, Object> operations = buildOperations();
		
		Map<String, Object> submodelElements = buildSubmodelElements();
		
		submodelElements.putAll(dataElements);
		submodelElements.putAll(operations);
		
		
		Map<String, Object> values = new LinkedHashMap<>();
		
		values.put(Submodel.SUBMODELELEMENT, submodelElements);
		
		proxy = new VABElementProxy("/" + SubmodelProvider.SUBMODEL,
				new SubmodelProvider(new TypeDestroyingProvider(new VABLambdaProvider(values))));
	}
	
	/**
	 * Builds a Map containing an Object of every IDataElement
	 * @return A Map ID->IDataElement
	 */
	private Map<String, Object> buildDataElements() {
		Map<String, Object> ret = new LinkedHashMap<>();
		
		Property property = new Property();
		property.setIdShort(PROPERTY_ID);
		ret.put(PROPERTY_ID, property);
		
		Blob blob = new Blob();
		blob.setIdShort(BLOB_ID);
		ret.put(BLOB_ID, blob);

		File file = new File();
		file.setIdShort(FILE_ID);
		ret.put(FILE_ID, file);

		MultiLanguageProperty mlp = new MultiLanguageProperty();
		mlp.setIdShort(MLP_ID);
		ret.put(MLP_ID, mlp);

		Range range = new Range();
		range.setIdShort(RANGE_ID);
		ret.put(RANGE_ID, range);

		ReferenceElement refElement = new ReferenceElement();
		refElement.setIdShort(REFELEMENT_ID);
		ret.put(REFELEMENT_ID, refElement);

		return ret;
	}

	/**
	 * Builds a Map containing an Object of every IOperation
	 * @return A Map ID->IOperation
	 */
	private Map<String, Object> buildOperations() {
		Map<String, Object> ret = new LinkedHashMap<>();

		Operation operation = new Operation();
		operation.setIdShort(OPERATION_ID);
		ret.put(OPERATION_ID, operation);
		
		return ret;
	}
	
	/**
	 * Builds a Map containing an Object of every IOperation
	 * @return A Map ID->IOperation
	 */
	private Map<String, Object> buildSubmodelElements() {
		Map<String, Object> ret = new LinkedHashMap<>();
		
		BasicEvent basicEvent = new BasicEvent();
		basicEvent.setIdShort(BASICEVENT_ID);
		ret.put(BASICEVENT_ID, basicEvent);

		Capability capability = new Capability();
		capability.setIdShort(CAPABILITY_ID);
		ret.put(CAPABILITY_ID, capability);
		
		RelationshipElement relElement = new RelationshipElement();
		relElement.setIdShort(RELELEMENT_ID);
		ret.put(RELELEMENT_ID, relElement);
		
		SubmodelElementCollection smElemCollection = new SubmodelElementCollection();
		smElemCollection.setIdShort(SMELEMCOLLECTION_ID);
		ret.put(SMELEMCOLLECTION_ID, smElemCollection);
		
		return ret;
	}
	
	/**
	 * Tests if getProperties() returns the correct value
	 */
	@Test
	public void testGetProperties() {
		Map<String, IProperty> properties =
				ConnectedSubmodelElementFactory.getProperties(
						proxy, MultiSubmodelElementProvider.ELEMENTS, MultiSubmodelElementProvider.ELEMENTS);
		
		assertEquals(1, properties.size());
		assertTrue(properties.get(PROPERTY_ID) instanceof ConnectedProperty);
	}
	
	/**
	 * Tests if getOperations() returns the correct value
	 */
	@Test
	public void testGetOperations() {
		Map<String, IOperation> operations =
				ConnectedSubmodelElementFactory.getOperations(
						proxy, MultiSubmodelElementProvider.ELEMENTS, MultiSubmodelElementProvider.ELEMENTS);
		
		assertEquals(1, operations.size());
		assertTrue(operations.get(OPERATION_ID) instanceof ConnectedOperation);
	}
	
	/**
	 * Tests if getSubmodelElements() returns the correct value
	 */
	@Test
	public void testGetSubmodelElements() {
		Map<String, ISubmodelElement> submodelElements =
				ConnectedSubmodelElementFactory.getConnectedSubmodelElements(
						proxy, Submodel.SUBMODELELEMENT, Submodel.SUBMODELELEMENT);
		
		assertEquals(11, submodelElements.size());
		assertTrue(submodelElements.get(PROPERTY_ID) instanceof ConnectedProperty);
		assertTrue(submodelElements.get(BLOB_ID) instanceof ConnectedBlob);
		assertTrue(submodelElements.get(FILE_ID) instanceof ConnectedFile);
		assertTrue(submodelElements.get(MLP_ID) instanceof ConnectedMultiLanguageProperty);
		assertTrue(submodelElements.get(RANGE_ID) instanceof ConnectedRange);
		assertTrue(submodelElements.get(REFELEMENT_ID) instanceof ConnectedReferenceElement);
		assertTrue(submodelElements.get(OPERATION_ID) instanceof ConnectedOperation);
		assertTrue(submodelElements.get(BASICEVENT_ID) instanceof ConnectedBasicEvent);
		assertTrue(submodelElements.get(CAPABILITY_ID) instanceof ConnectedCapability);
		assertTrue(submodelElements.get(RELELEMENT_ID) instanceof ConnectedRelationshipElement);
		assertTrue(submodelElements.get(SMELEMCOLLECTION_ID) instanceof ConnectedSubmodelElementCollection);
	}
	
}
