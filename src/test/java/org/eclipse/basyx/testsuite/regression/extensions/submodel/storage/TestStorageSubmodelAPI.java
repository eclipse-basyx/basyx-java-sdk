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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.eclipse.basyx.extensions.submodel.storage.StorageSubmodelAPI;
import org.eclipse.basyx.extensions.submodel.storage.StorageSubmodelAPIHelper.StorageSubmodelElementOperations;
import org.eclipse.basyx.extensions.submodel.storage.elements.IStorageSubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestStorageSubmodelAPI {
	private static final String AASID = "testaasid";
	private static final String SUBMODELID = "testsubmodelid";

	private static StorageSubmodelAPI storageAPI;
	private static EntityManager entityManager;

	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		Submodel sm = new Submodel(SUBMODELID, new Identifier(IdentifierType.CUSTOM, SUBMODELID));
		Reference parentRef = new Reference(new Key(KeyElements.ASSETADMINISTRATIONSHELL, true, AASID, IdentifierType.IRDI));
		sm.setParent(parentRef);

		VABSubmodelAPI vabAPI = new VABSubmodelAPI(new VABMapProvider(sm));

		// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>HIER<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("storageElement_sql");
		entityManager = emf.createEntityManager();
		storageAPI = new StorageSubmodelAPI(vabAPI, entityManager);

		// TODO: use factory for StorageSubmodelAPI as well to use different
		// StorageSubmodelElements (NoSQL/SQL)
		// entityManager.getProperties().get("eclipselink.target-database");
	}

	@Test
	public void testAddSubmodelElement() throws InterruptedException {
		String elemIdShort = "testAddProp";
		Property prop = new Property(true);
		prop.setIdShort(elemIdShort);
		storageAPI.addSubmodelElement(prop);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(elemIdShort);

		assertEquals(storedElement.getSerializedElementValue(), prop.getValue().toString());
	}

	@Test
	public void testAddNestedSubmodelElement() {
		String idShortPath = "/testColl/testAddProp/";
		SubmodelElementCollection coll = new SubmodelElementCollection();
		coll.setIdShort("testColl");
		storageAPI.addSubmodelElement(coll);

		IStorageSubmodelElement storedColl = getSingleStorageElementWithIdShort(coll.getIdShort());
		assertEquals(storedColl.getSerializedElementValue(), coll.getValue().toString());

		Property prop = new Property(true);
		prop.setIdShort("testAddProp");
		storageAPI.addSubmodelElement(idShortPath, prop);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(idShortPath);

		assertEquals(storedElement.getSerializedElementValue(), prop.getValue().toString());
	}

	@Test
	public void testDeleteSubmodelElement() {
		String idShortPath = "/testDeleteProp";
		Property prop = new Property(true);
		prop.setIdShort("testDeleteProp");
		storageAPI.addSubmodelElement(prop);
		storageAPI.deleteSubmodelElement(idShortPath);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(idShortPath);

		assertEquals(storedElement.getOperation(), StorageSubmodelElementOperations.DELETE);
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
		String idShortPath = "testHistoryProp";
		Property prop = new Property(true);
		prop.setIdShort(idShortPath);
		storageAPI.addSubmodelElement(prop);
		storageAPI.updateSubmodelElement(idShortPath, false);

		List<IStorageSubmodelElement> elements = storageAPI.getSubmodelElementHistoricValues(SUBMODELID, idShortPath);
		assertTrue(elements.size() == 2);
		assertFalse(Boolean.parseBoolean(elements.get(0).getSerializedElementValue()));
		assertTrue(Boolean.parseBoolean(elements.get(1).getSerializedElementValue()));
	}

	private IStorageSubmodelElement getSingleStorageElementWithIdShort(String idShort) {
		List<IStorageSubmodelElement> elements = storageAPI.getSubmodelElementHistoricValues(SUBMODELID, idShort);

		IStorageSubmodelElement storedElement = elements.get(0);
		return storedElement;
	}

	@AfterClass
	public static void tearDownClass() {
		// clear database table again
		List<IStorageSubmodelElement> elements = storageAPI.getSubmodelElementHistoricValues(SUBMODELID);
		entityManager.getTransaction().begin();
		elements.forEach((n) -> entityManager.remove(n));
		entityManager.getTransaction().commit();
	}
}
