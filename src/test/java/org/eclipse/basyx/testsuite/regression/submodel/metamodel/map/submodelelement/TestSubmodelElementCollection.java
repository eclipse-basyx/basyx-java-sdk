/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.basyx.aas.metamodel.exception.IdShortDuplicationException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IDataElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Formula;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifiable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor and getter of {@link SubmodelElementCollection} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestSubmodelElementCollection {
	private static final Reference REFERENCE = new Reference(new Identifier(IdentifierType.CUSTOM, "testValue"), KeyElements.ACCESSPERMISSIONRULE, false);
	private static final String OPERATION_ID = "testOpID";
	private static final String PROPERTY_ID = "testPropID";

	private Collection<ISubmodelElement> elements1;
	private Collection<ISubmodelElement> elements2;
	private SubmodelElementCollection elementCollection;
	
	@Before
	public void buildSubmodelElementCollection() {
		elements1 = new ArrayList<>();
		elements1.add(getProperty());
		elements1.add(getOperation());

		elements2 = new ArrayList<ISubmodelElement>();
		elements2.add(new Property("idShort1", "testId1"));
		elements2.add(new Property("idShort2","testId2"));
		elementCollection = new SubmodelElementCollection(elements2, false, false);
		elementCollection.setIdShort("testCollectionId");
	} 

	@Test
	public void testConstructor() {
		SubmodelElementCollection elementCollection = new SubmodelElementCollection(elements1, true, true);
		assertTrue(elementCollection.isAllowDuplicates());
		assertTrue(elementCollection.isOrdered());
		
		ISubmodelElement checkOperation = elementCollection.getSubmodelElements().get(OPERATION_ID);
		assertEquals(OPERATION_ID, checkOperation.getIdShort());
	} 
	
	@Test
	public void testAddValue() {
		Property element = new Property("testProperty");
		element.setIdShort("propId");
		elementCollection.addSubmodelElement(element);
		elements2.add(element);
		
		ISubmodelElement checkProperty = elementCollection.getSubmodelElements().get("propId");
		assertEquals(element.getIdShort(), checkProperty.getIdShort());
	} 
	
	@Test
	public void testSetDataSpecificationReferences() {
		Collection<IReference> refs = Collections.singleton(REFERENCE);
		elementCollection.setDataSpecificationReferences(refs);
		assertEquals(refs, elementCollection.getDataSpecificationReferences());
	} 
	
	@Test
	public void testSetValue() {
		Collection<ISubmodelElement> elements = new ArrayList<ISubmodelElement>();
		Property element = new Property("testProperty");
		element.setIdShort("propId");
		elements.add(element);
		elementCollection.setValue(elements);
		
		ISubmodelElement checkProperty = elementCollection.getSubmodelElements().get("propId");
		assertEquals(element.getIdShort(), checkProperty.getIdShort());
	} 
	
	@Test
	public void testSetOrdered() {
		elementCollection.setOrdered(false);
		assertTrue(!elementCollection.isOrdered());
	} 
	
	@Test
	public void testSetAllowDuplicates() {
		elementCollection.setAllowDuplicates(false);
		assertTrue(!elementCollection.isAllowDuplicates());
	} 
	
	@Test
	public void testKeepsOrderWhenOrdered() {
	  SubmodelElementCollection sec1 = new SubmodelElementCollection("sec1");
	  sec1.setOrdered(true);
	  sec1.addSubmodelElement(new Property("id1", "blub1"));
	  sec1.addSubmodelElement(new Property("id2", "blub2"));
	  sec1.addSubmodelElement(new Property("id3", "blub3"));
	  sec1.addSubmodelElement(new Property("id4", "blub4"));
	  
	  List<String> idShortsInOrder = sec1.getValue().stream().map(e -> e.getIdShort()).collect(Collectors.toList());
	  assertEquals(Arrays.asList("id1","id2","id3","id4"), idShortsInOrder);
	}
	
	@Test
	public void testSetElements() {
		String idShort = "testIdShort";
		Key key = new Key(KeyElements.BLOB, true, "TestValue", IdentifierType.IRI);
		Reference reference = new Reference(key);
		Formula formula = new Formula(Collections.singleton(reference));
		Qualifiable qualifiable = new Qualifiable(formula);
		ISubmodelElement element = new Property("testId1", new Referable(idShort, "Category", new LangStrings("DE", "Test")), REFERENCE, qualifiable);
		Map<String, ISubmodelElement> elementsMap = new HashMap<String, ISubmodelElement>();
		elementsMap.put(idShort, element);
		elementCollection.setElements(elementsMap);
		assertEquals(elementsMap, elementCollection.getSubmodelElements());
	} 


	@Test
	public void testConstructor1() {
		SubmodelElementCollection collection = new SubmodelElementCollection(elements1, false, false);

		Map<String, IDataElement> dataElements = new HashMap<String, IDataElement>();
		Map<String, IOperation> operations = new HashMap<String, IOperation>();
		Map<String, ISubmodelElement> submodels = new HashMap<String, ISubmodelElement>();
		dataElements.put(PROPERTY_ID, getProperty());
		operations.put(OPERATION_ID, getOperation());
		submodels.putAll(operations);
		submodels.putAll(dataElements);
		assertEquals(dataElements, collection.getProperties());
		assertEquals(operations, collection.getOperations());
		assertEquals(submodels, collection.getSubmodelElements());
	}

	@Test
	public void testAddSubmodelElement() {
		SubmodelElementCollection collection = new SubmodelElementCollection(elements1, false, false);
		String smCollIdShort = "coll1";
		collection.setIdShort(smCollIdShort);
		Property property = new Property("testValue");
		String newIdShort = "newIdShort";
		property.put(Referable.IDSHORT, newIdShort);
		collection.addSubmodelElement(property);
		assertEquals(new Reference(new Key(KeyElements.SUBMODELELEMENTCOLLECTION, true, smCollIdShort, KeyType.IDSHORT)), property.getParent());
		Map<String, ISubmodelElement> submodelElements = new HashMap<String, ISubmodelElement>();
		submodelElements.put(PROPERTY_ID, getProperty());
		submodelElements.put(OPERATION_ID, getOperation());
		submodelElements.put(newIdShort, property);
		assertEquals(submodelElements, collection.getSubmodelElements());
	}
	
	@Test
	public void testGetSubmodelElement() {
		SubmodelElementCollection collection = new SubmodelElementCollection(elements1, false, false);
		ISubmodelElement retrievedElement = collection.getSubmodelElement(PROPERTY_ID);
		assertEquals(getProperty(), retrievedElement);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void testGetSubmodelElementNotExist() {
		SubmodelElementCollection collection = new SubmodelElementCollection(elements1, false, false);
		collection.getSubmodelElement("Id_Which_Does_Not_Exist");
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void testDeleteSubmodelElement() {
		SubmodelElementCollection collection = new SubmodelElementCollection(elements1, false, false);
		collection.deleteSubmodelElement(PROPERTY_ID);
		collection.getSubmodelElement(PROPERTY_ID);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void testDeleteSubmodelElementNotExist() {
		SubmodelElementCollection collection = new SubmodelElementCollection(elements1, false, false);
		collection.deleteSubmodelElement("Id_Which_Does_Not_Exist");
	}
	
	@Test
	public void testSubmodel() {
		
	}
	
	@Test
	public void testGetValues() {
		SubmodelElementCollection collection = new SubmodelElementCollection(elements1, false, false);
		collection.setIdShort("smColl");
		Map<String, Object> elements = collection.getValues();
		Property property = getProperty();
		assertEquals(1, elements.size());
		assertTrue(elements.containsKey(PROPERTY_ID));
		assertEquals(property.getValue(), elements.get(PROPERTY_ID));
		
		String newKey = "newKey";
		String newValue = "newValue";
		
		Property property2 = new Property(newKey, newValue);
		property2.setValueType(ValueType.String);
		collection.addSubmodelElement(property2);
		
		elements = collection.getValues();
		assertEquals(2, elements.size());
		assertTrue(elements.containsKey(newKey));
		assertEquals(newValue, elements.get(newKey));
	}

	/**
	 * Get a dummy property
	 * 
	 * @return property
	 */
	private Property getProperty() {
		Referable referable = new Referable(PROPERTY_ID, "testCategory", new LangStrings("DE", "test"));
		Reference semanticId = new Reference(new Key(KeyElements.ASSET, true, "testValue", IdentifierType.IRI));
		Qualifiable qualifiable = new Qualifiable(new Formula(Collections
				.singleton(new Reference(new Key(KeyElements.BLOB, true, "TestValue", IdentifierType.IRI)))));
		Property property = new Property("testValue", referable, semanticId, qualifiable);
		return property;
	}

	/**
	 * Get a dummy operation
	 * 
	 * @return operation
	 */
	private Operation getOperation() {
		Property property = new Property("testOpVariableId");
		property.setModelingKind(ModelingKind.TEMPLATE);
		List<OperationVariable> variable = Collections
				.singletonList(new OperationVariable(property));
		Operation operation = new Operation(variable, variable, variable, null);
		operation.put(Referable.IDSHORT, OPERATION_ID);
		return operation;
	}
	
	@Test(expected = IdShortDuplicationException.class)
	public void checkForExceptionWithDuplicateIdShortInSubmodelElementCollection() {
		Map<String, Object> faultySubmodelElementCollection = createSubmodelElementCollectionWithDuplicateIdShortProperties();

		SubmodelElementCollection.createAsFacade(faultySubmodelElementCollection);
	}

	private Map<String, Object> createSubmodelElementCollectionWithDuplicateIdShortProperties() {
		Property property1 = new Property("testProp", 5);
		Property property2 = new Property("testProp", 7);
		
		Collection<Map<String, Object>> collection = new ArrayList<>();
		
		collection.add(property1);
		collection.add(property2);
		
		SubmodelElementCollection submodelElementCollection = new SubmodelElementCollection(Referable.IDSHORT);
		
		submodelElementCollection.setValue(collection);
		
		return submodelElementCollection;
	}
	
}
