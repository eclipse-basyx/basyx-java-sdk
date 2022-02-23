/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.testsuite.regression.extensions.submodel.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.basyx.extensions.submodel.storage.StorageSubmodelAPI;
import org.eclipse.basyx.extensions.submodel.storage.elements.IStorageSubmodelElement;
import org.eclipse.basyx.extensions.submodel.storage.elements.StorageSubmodelElementComponent.StorageSubmodelElementOperations;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IDataElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.entity.EntityType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.Capability;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.Blob;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.ReferenceElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.RangeValue;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.entity.Entity;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.entity.EntityValue;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.event.BasicEvent;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.AnnotatedRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.AnnotatedRelationshipElementValue;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElementValue;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@RunWith(Parameterized.class)
public class TestStorageSubmodelAPI {
	private final String AAS_ID = "testaasid";
	private final String SUBMODEL_ID = "testsubmodelid";

	private StorageSubmodelAPI storageAPI;
	private EntityManager entityManager;
	private String storageOption;

	@Parameterized.Parameters
	public static String[] storageOptions() {
		return new String[] { "storageElement_sql", "storageElement_nosql" };
	}

	public TestStorageSubmodelAPI(String storageOption) {
		this.storageOption = storageOption;
	}

	@Before
	public void setUp() {
		Submodel sm = new Submodel(SUBMODEL_ID, new Identifier(IdentifierType.CUSTOM, SUBMODEL_ID));
		Reference parentRef = new Reference(new Key(KeyElements.ASSETADMINISTRATIONSHELL, true, AAS_ID, IdentifierType.IRDI));
		sm.setParent(parentRef);

		VABSubmodelAPI vabAPI = new VABSubmodelAPI(new VABMapProvider(sm));

		EntityManagerFactory emf = Persistence.createEntityManagerFactory(storageOption);
		entityManager = emf.createEntityManager();
		storageAPI = new StorageSubmodelAPI(vabAPI, entityManager);
	}

	@Test
	public void testAddProperty() {
		String elemIdShort = "testAddProp";
		Property prop = new Property(true);
		prop.setIdShort(elemIdShort);
		storageAPI.addSubmodelElement(prop);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(elemIdShort);

		assertEquals(prop.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(prop.getModelType(), storedElement.getModelType());
	}

	@Test
	public void testAddRange() {
		String elemIdShort = "testAddProp";
		Range range = new Range();
		range.setValue(new RangeValue(1, 2));
		range.setIdShort(elemIdShort);
		storageAPI.addSubmodelElement(range);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(elemIdShort);

		assertEquals(range.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(range.getModelType(), storedElement.getModelType());
	}

	@Test
	public void testAddBlob() {
		String elemIdShort = "testAddBlob";
		Blob blob = new Blob();
		blob.setValue("longstring?");
		blob.setMimeType("type");
		blob.setIdShort(elemIdShort);
		storageAPI.addSubmodelElement(blob);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(elemIdShort);

		assertEquals(blob.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(blob.getModelType(), storedElement.getModelType());
	}

	@Test
	public void testAddFile() {
		String elemIdShort = "testAddFile";
		File file = new File();
		file.setValue("filepath");
		file.setMimeType("type");
		file.setIdShort(elemIdShort);
		storageAPI.addSubmodelElement(file);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(elemIdShort);

		assertEquals(file.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(file.getModelType(), storedElement.getModelType());
	}

	@Test
	public void testAddMultiLanguageProperty() {
		String elemIdShort = "testAddMultiLanguageProp";
		MultiLanguageProperty mlProp = new MultiLanguageProperty();
		LangStrings value = new LangStrings(new LangString("EN", "testLangStringEN"));
		value.add(new LangString("de", "testLangStringDE"));
		mlProp.setValue(value);
		mlProp.setIdShort(elemIdShort);
		storageAPI.addSubmodelElement(mlProp);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(elemIdShort);

		assertEquals(mlProp.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(mlProp.getModelType(), storedElement.getModelType());
	}

	@Test
	public void testAddEntity() {
		String elemIdShort = "testAddEntity";
		Entity entity = new Entity();

		Collection<ISubmodelElement> statements = new ArrayList<>();
		Property prop = new Property(true);
		prop.setIdShort(elemIdShort);
		statements.add(prop);
		IReference asset = new Reference();

		EntityValue entityValue = new EntityValue(statements, asset);
		entity.setValue(entityValue);
		entity.setIdShort(elemIdShort);
		entity.setEntityType(EntityType.SELFMANAGEDENTITY);
		storageAPI.addSubmodelElement(entity);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(elemIdShort);

		assertEquals(entity.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(entity.getModelType(), storedElement.getModelType());
	}

	@Test
	public void testAddReferenceElement() {
		String elemIdShort = "testAddReferenceElement";
		ReferenceElement refElement = new ReferenceElement();
		refElement.setValue(new Reference(new Key(KeyElements.ASSETADMINISTRATIONSHELL, true, AAS_ID, IdentifierType.IRDI)));
		refElement.setIdShort(elemIdShort);
		storageAPI.addSubmodelElement(refElement);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(elemIdShort);

		assertEquals(refElement.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(refElement.getModelType(), storedElement.getModelType());
	}

	@Test
	public void testAddRelationshipElement() {
		String elemIdShort = "testAddRelationshipElement";
		RelationshipElement relationshipElement = new RelationshipElement();
		Reference testRef1 = new Reference(new Key(KeyElements.ASSETADMINISTRATIONSHELL, true, AAS_ID, IdentifierType.IRDI));
		Reference testRef2 = new Reference(new Key(KeyElements.ASSETADMINISTRATIONSHELL, true, AAS_ID, IdentifierType.IRDI));
		relationshipElement.setValue(new RelationshipElementValue(testRef1, testRef2));
		relationshipElement.setIdShort(elemIdShort);
		storageAPI.addSubmodelElement(relationshipElement);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(elemIdShort);

		assertEquals(relationshipElement.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(relationshipElement.getModelType(), storedElement.getModelType());
	}

	@Test
	public void testAddAnnotatedRelationshipElement() {
		String elemIdShort = "testAddAnnotatedRelationshipElement";
		AnnotatedRelationshipElement annotatedRelationshipElement = new AnnotatedRelationshipElement();
		Reference testRef1 = new Reference(new Key(KeyElements.ASSETADMINISTRATIONSHELL, true, AAS_ID, IdentifierType.IRDI));
		Reference testRef2 = new Reference(new Key(KeyElements.ASSETADMINISTRATIONSHELL, true, AAS_ID, IdentifierType.IRDI));

		Collection<IDataElement> annotations = new ArrayList<>();
		Property prop = new Property(true);
		prop.setIdShort(elemIdShort);
		annotations.add(prop);

		annotatedRelationshipElement.setValue(new AnnotatedRelationshipElementValue(testRef1, testRef2, annotations));
		annotatedRelationshipElement.setIdShort(elemIdShort);
		storageAPI.addSubmodelElement(annotatedRelationshipElement);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(elemIdShort);

		assertEquals(annotatedRelationshipElement.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(annotatedRelationshipElement.getModelType(), storedElement.getModelType());
	}

	@Test
	public void testAddBasicEvent() {
		String elemIdShort = "testAddBasicEvent";
		BasicEvent basicEvent = new BasicEvent();
		Reference reference = new Reference(new Key(KeyElements.ASSETADMINISTRATIONSHELL, true, AAS_ID, IdentifierType.IRDI));
		basicEvent.setValue(reference);
		basicEvent.setIdShort(elemIdShort);
		storageAPI.addSubmodelElement(basicEvent);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(elemIdShort);

		assertEquals(basicEvent.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(basicEvent.getModelType(), storedElement.getModelType());
	}

	@Test
	public void testAddOperation() {
		String elemIdShort = "testAddOperation";
		Operation operation = new Operation();
		operation.setIdShort(elemIdShort);
		storageAPI.addSubmodelElement(operation);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(elemIdShort);
		assertNull(storedElement);
	}

	@Test
	public void testAddCapability() {
		String elemIdShort = "testAddCapability";
		Capability capability = new Capability();
		capability.setIdShort(elemIdShort);
		storageAPI.addSubmodelElement(capability);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(elemIdShort);
		assertNull(storedElement);
	}

	@Test
	public void testAddNestedSubmodelElement() {
		String collIdShort = "testColl";
		SubmodelElementCollection coll = new SubmodelElementCollection();
		coll.setIdShort(collIdShort);
		storageAPI.addSubmodelElement(coll);

		IStorageSubmodelElement storedColl = getSingleStorageElementWithIdShort(coll.getIdShort());
		assertNull(storedColl.getSerializedElementValue());
		assertEquals(coll.getModelType(), storedColl.getModelType());

		String elementIdShort = "testAddProp";
		Property prop = new Property(true);
		prop.setIdShort("testAddProp");
		coll.addSubmodelElement(prop);
		storageAPI.updateSubmodelElement(collIdShort, coll.getValue());

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(VABPathTools.concatenatePaths(collIdShort, elementIdShort));

		assertEquals(prop.getValue().toString(), storedElement.getSerializedElementValue());
	}

	@Test
	public void testUpdateSubmodelElement() {
		String idShortPath = "testUpdateProp";
		Property prop = new Property(true);
		prop.setIdShort(idShortPath);
		storageAPI.addSubmodelElement(prop);
		storageAPI.updateSubmodelElement(idShortPath, false);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(idShortPath);
		assertFalse(Boolean.parseBoolean(storedElement.getSerializedElementValue()));
	}

	@Test
	public void testHistoricSubmodelElement() {
		int created_elements = 2;
		String idShortPath = "testHistoryProp";
		Property prop = new Property(true);
		prop.setIdShort(idShortPath);
		storageAPI.addSubmodelElement(prop);
		storageAPI.updateSubmodelElement(idShortPath, false);

		List<IStorageSubmodelElement> elements = storageAPI.getSubmodelElementHistoricValues(SUBMODEL_ID, idShortPath);
		assertEquals(created_elements, elements.size());
		assertFalse(Boolean.parseBoolean(elements.get(0).getSerializedElementValue()));
		assertTrue(Boolean.parseBoolean(elements.get(1).getSerializedElementValue()));
	}

	@Test
	public void testDeleteSubmodelElement() {
		String idShortPath = "testDeleteProp";
		Property prop = new Property(true);
		prop.setIdShort(idShortPath);
		storageAPI.addSubmodelElement(prop);
		storageAPI.deleteSubmodelElement(idShortPath);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(idShortPath);

		assertEquals(StorageSubmodelElementOperations.DELETE, storedElement.getOperation());
	}

	private IStorageSubmodelElement getSingleStorageElementWithIdShort(String idShort) {
		List<IStorageSubmodelElement> elements = storageAPI.getSubmodelElementHistoricValues(SUBMODEL_ID, idShort);

		if (elements.isEmpty()) {
			return null;
		}

		IStorageSubmodelElement storedElement = elements.get(0);
		return storedElement;
	}

	@After
	public void tearDown() {
		// clear database table again
		List<IStorageSubmodelElement> elements = storageAPI.getSubmodelElementHistoricValues(SUBMODEL_ID);
		entityManager.getTransaction().begin();
		elements.forEach((n) -> entityManager.remove(n));
		entityManager.getTransaction().commit();
	}
}
