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

import java.util.List;

import org.eclipse.basyx.extensions.submodel.storage.StorageSubmodelAPI;
import org.eclipse.basyx.extensions.submodel.storage.elements.IStorageSubmodelElement;
import org.eclipse.basyx.extensions.submodel.storage.elements.StorageSubmodelElementComponent.StorageSubmodelElementOperations;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.RangeValue;
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
