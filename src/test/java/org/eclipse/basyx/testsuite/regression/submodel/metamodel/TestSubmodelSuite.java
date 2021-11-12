/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.registry.descriptor.ModelUrn;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IDataElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.Blob;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.ReferenceElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.RangeValue;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.AnnotatedRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.Test;

/**
 * Abstract Submodel Testsuite to be reused by different implementations of
 * {@link ISubmodel}
 * 
 * @author schnicke
 *
 */
public abstract class TestSubmodelSuite {
	// String constants used in this test case
	private final static String PROP = "prop1";
	private final static String ID = "TestId";

	private final String PROPERTY_ID = "property_id";
	private final String BLOB_ID = "blob_id";
	private final String RELATIONSHIP_ELEM_ID = "relElem_id";
	private final String ANNOTATED_RELATIONSHIP_ELEM_ID = "annotatedRelElem_id";
	private final String SUBMODEL_ELEM_COLLECTION_ID = "elemCollection_id";
	private final String PROPERTY_CONTAINED_ID = "containedProp";
	private final String RANGE_ID = "range_id";
	private final String FILE_ID = "file_id";
	private final String MULTI_LANG_PROP_ID = "multi_lang_prop_id";
	private final String REFERENCE_ELEMENT_ID = "reference_element_id";
	private final String PROPERTY_ID2 = "property_id2";
	

	private final static Reference testSemanticIdRef = new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "testVal", IdentifierType.CUSTOM));

	public Submodel getReferenceSubmodel() {

		// Create a simple value property
		Property propertyMeta = new Property(PROP, ValueType.Integer);
		propertyMeta.setValue(100);

		// Create the Submodel using the created property and operation
		IIdentifier submodelId = new ModelUrn("testUrn");
		Submodel localSubmodel = new Submodel(ID, submodelId);
		localSubmodel.addSubmodelElement(propertyMeta);
		localSubmodel.setSemanticId(testSemanticIdRef);

		return localSubmodel;
	}

	protected abstract ISubmodel getSubmodel();

	/**
	 * Tests if a Submodel's id can be retrieved correctly
	 */
	@Test
	public void getIdTest() {
		ISubmodel submodel = getSubmodel();
		assertEquals(ID, submodel.getIdShort());
	}

	/**
	 * Tests if a Submodel's properties can be used correctly
	 */
	@Test
	public void propertiesTest() throws Exception {
		ISubmodel submodel = getSubmodel();
		// Retrieve all properties
		Map<String, IProperty> props = submodel.getProperties();

		// Check if number of properties is as expected
		assertEquals(1, props.size());

		// Check the value of the property
		IProperty prop = props.get(PROP);
		assertEquals(100, prop.getValue());
	}


	@Test
	public void saveAndLoadPropertyTest() throws Exception {
		ISubmodel submodel = getSubmodel();

		// Get sample DataElements and save them into Submodel
		Map<String, IProperty> testData = getTestDataProperty();
		for (ISubmodelElement element : testData.values()) {
			submodel.addSubmodelElement(element);
		}

		// Load it
		Map<String, IProperty> map = submodel.getProperties();

		// Check if it loaded correctly
		checkProperties(map);
	}

	@Test
	public void saveAndLoadSubmodelElementTest() throws Exception {
		ISubmodel submodel = getSubmodel();

		// Get sample DataElements and save them into Submodel
		Map<String, IProperty> testDataElements = getTestDataProperty();
		for (ISubmodelElement element : testDataElements.values()) {
			submodel.addSubmodelElement(element);
		}


		// Get sample SubmodelElements and save them into Submodel
		Map<String, ISubmodelElement> testSMElements = getTestSubmodelElements();
		for (ISubmodelElement element : testSMElements.values()) {
			submodel.addSubmodelElement(element);
		}

		// Load it
		Map<String, ISubmodelElement> map = submodel.getSubmodelElements();

		// Check if it loaded correctly
		// Including DataElements and Operations as they are also SubmodelElements
		checkProperties(map);
		checkSubmodelElements(map);
	}

	/**
	 * Tests if the semantic Id can be retrieved correctly
	 */
	@Test
	public void semanticIdRetrievalTest() {
		ISubmodel submodel = getSubmodel();
		IReference ref = submodel.getSemanticId();
		assertEquals(testSemanticIdRef, ref);
	}

	/**
	 * Tests if the adding a submodel element is correctly done Also checks the
	 * addition of parent reference to the submodel
	 */
	@Test
	public void addSubmodelElementTest() throws Exception {
		ISubmodel submodel = getSubmodel();
		Property property = new Property("testProperty");
		property.setIdShort("testIdShort");
		submodel.addSubmodelElement(property);

		IProperty connectedProperty = (IProperty) submodel.getSubmodelElements().get("testIdShort");
		assertEquals(property.getIdShort(), connectedProperty.getIdShort());
		assertEquals(property.getValue(), connectedProperty.getValue());

		// creates an expected reference for assertion
		IReference expected = submodel.getReference();
		assertEquals(expected, property.getParent());
	}

	@Test
	public void testGetSubmodelElement() {
		ISubmodel submodel = getSubmodel();
		ISubmodelElement element = submodel.getSubmodelElement(PROP);
		assertEquals(PROP, element.getIdShort());
	}

	@Test(expected = ResourceNotFoundException.class)
	public void testDeleteSubmodelElement() {

		ISubmodel submodel = getSubmodel();
		submodel.deleteSubmodelElement(PROP);
		submodel.getSubmodelElement(PROP);
	}

	/**
	 * Tests getValues function
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValues() {
		ISubmodel submodel = getSubmodel();

		// Add elements to the Submodel
		Map<String, ISubmodelElement> testSMElements = getTestSubmodelElements();
		for (ISubmodelElement element : testSMElements.values()) {
			submodel.addSubmodelElement(element);
		}

		Map<String, Object> values = submodel.getValues();

		assertEquals(10, values.size());

		// Check if all expected Values are present
		assertEquals(100, values.get(PROP));
		assertEquals(Base64.getEncoder().encodeToString(new byte[] { 1, 2, 3 }), values.get(BLOB_ID));

		assertTrue(values.containsKey(RELATIONSHIP_ELEM_ID));
		assertTrue(values.containsKey(SUBMODEL_ELEM_COLLECTION_ID));
		Map<String, Object> collection = (Map<String, Object>) values.get(SUBMODEL_ELEM_COLLECTION_ID);
		assertTrue(collection.containsKey(PROPERTY_CONTAINED_ID));
		assertTrue(values.containsKey(PROPERTY_ID2));
		assertTrue(values.containsKey(BLOB_ID));
		assertTrue(values.containsKey(RANGE_ID));
		assertTrue(values.containsKey(MULTI_LANG_PROP_ID));
		assertTrue(values.containsKey(FILE_ID));
		assertTrue(values.containsKey(REFERENCE_ELEMENT_ID));
	}

	/**
	 * Generates test IDataElements
	 */
	private Map<String, IProperty> getTestDataProperty() {
		Map<String, IProperty> ret = new HashMap<>();

		Property property = new Property();
		property.setIdShort(PROPERTY_ID);
		property.setValue("test2");
		ret.put(property.getIdShort(), property);

		Property byteProp = new Property();
		byteProp.setIdShort("byte_prop01");
		Byte byteNumber = Byte.parseByte("2");
		byteProp.setValue(byteNumber);
		ret.put(byteProp.getIdShort(), byteProp);

		Property durationProp = new Property();
		durationProp.setIdShort("duration_prop01");
		Duration duration = Duration.ofSeconds(10);
		durationProp.setValue(duration);
		ret.put(durationProp.getIdShort(), durationProp);

		Property periodProp = new Property();
		periodProp.setIdShort("period_prop01");
		LocalDate today = LocalDate.now();
		LocalDate birthday = LocalDate.of(1960, Month.JANUARY, 1);
		Period p = Period.between(birthday, today);
		periodProp.setValue(p);
		ret.put(periodProp.getIdShort(), periodProp);

		Property bigNumberProp = new Property();
		bigNumberProp.setIdShort("bignumber_prop01");
		BigInteger bignumber = new BigInteger("9223372036854775817");
		property.setValue(bignumber);
		ret.put(bigNumberProp.getIdShort(), bigNumberProp);

		return ret;
	}
	/**
	 * Generates test ISubmodelElements
	 */
	private Map<String, ISubmodelElement> getTestSubmodelElements() {
		Map<String, ISubmodelElement> ret = new HashMap<>();

		SubmodelElementCollection smECollection = new SubmodelElementCollection();
		smECollection.setIdShort(SUBMODEL_ELEM_COLLECTION_ID);

		// Create a Property to use as Value for smECollection
		List<ISubmodelElement> values = new ArrayList<>();
		Property contained = new Property(PROPERTY_CONTAINED_ID, true);
		values.add(contained);


		smECollection.setValue(values);
		ret.put(smECollection.getIdShort(), smECollection);

		Blob blob = new Blob(BLOB_ID, "text/json");
		blob.setByteArrayValue(new byte[] { 1, 2, 3 });
		ret.put(blob.getIdShort(), blob);

		Reference first = new Reference(new Key(KeyElements.BASICEVENT, true, "testFirst", IdentifierType.CUSTOM));
		Reference second = new Reference(new Key(KeyElements.BASICEVENT, true, "testSecond", IdentifierType.CUSTOM));
		
		RelationshipElement relElement = new RelationshipElement(RELATIONSHIP_ELEM_ID, first, second);
		ret.put(relElement.getIdShort(), relElement);

		AnnotatedRelationshipElement annotatedRelElement = new AnnotatedRelationshipElement(ANNOTATED_RELATIONSHIP_ELEM_ID, first, second);
		List<IDataElement> annotations = new ArrayList<>();
		Property annotationProperty = new Property("id", 10);
		annotations.add(annotationProperty);
		annotatedRelElement.setAnnotation(annotations);
		ret.put(annotatedRelElement.getIdShort(), annotatedRelElement);
		
		Property property = new Property(PROPERTY_ID2, ValueType.AnySimpleType);
		ret.put(property.getIdShort(), property);
		
		Range range = new Range(RANGE_ID, ValueType.Integer);
		range.setValue(new RangeValue(-100, +100));
		ret.put(range.getIdShort(), range);
		
		File file = new File("text/plain");
		file.setIdShort(FILE_ID);
		file.setValue("fileUrl");
		ret.put(file.getIdShort(), file);
		
		MultiLanguageProperty languageProperty = new MultiLanguageProperty(MULTI_LANG_PROP_ID);
		languageProperty.setValue(new LangStrings(new LangString("en-en", "TestDescription")));
		ret.put(languageProperty.getIdShort(), languageProperty);
		
		ReferenceElement referenceElement = new ReferenceElement(REFERENCE_ELEMENT_ID, first);
		ret.put(referenceElement.getIdShort(), referenceElement);

		return ret;
	}

	/**
	 * Checks if the given Map contains all expected IDataElements
	 */
	private void checkProperties(Map<String, ? extends ISubmodelElement> actual) throws Exception {
		assertNotNull(actual);

		Map<String, IProperty> expected = getTestDataProperty();

		// Check value and type of each property in the submodel
		expected.forEach((id, prop) -> {
			IProperty expectedProperty = expected.get(PROPERTY_ID);
			IProperty actualProperty = (IProperty) actual.get(PROPERTY_ID);
			assertNotNull(actualProperty);
			try {
				assertEquals(expectedProperty.getValue(), actualProperty.getValue());
				assertEquals(expectedProperty.getValueType(), actualProperty.getValueType());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Checks if the given Map contains all expected ISubmodelElements
	 */
	private void checkSubmodelElements(Map<String, ISubmodelElement> actual) throws Exception {
		assertNotNull(actual);

		Map<String, ISubmodelElement> expected = getTestSubmodelElements();

		ISubmodelElementCollection expectedCollection = (ISubmodelElementCollection) expected.get(SUBMODEL_ELEM_COLLECTION_ID);
		ISubmodelElementCollection actualCollection = (ISubmodelElementCollection) actual.get(SUBMODEL_ELEM_COLLECTION_ID);

		assertNotNull(actualCollection);

		Collection<ISubmodelElement> elements = actualCollection.getSubmodelElements().values();


		// Check for correct Type
		assertEquals(1, elements.size());
		assertTrue(elements.iterator().next() instanceof IProperty);

		assertEquals(expectedCollection.getSubmodelElements().size(), elements.size());

		// Check value of all submodel elements
		for (ISubmodelElement elem : expected.values()) {
			// Equality of Ids is implicitly checked by this retrieval
			ISubmodelElement actualElem = actual.get(elem.getIdShort());
			assertNotNull(actualElem);

			assertEquals(elem.getValue(), actualElem.getValue());
		}
	}

	/**
	 * This method tests deleteSubmodelElement method of Submodel class with non
	 * existing element id
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void testDeleteSubmodelElementNotExist() {
		getSubmodel().deleteSubmodelElement("Id_Which_Does_Not_Exist");
	}
}
