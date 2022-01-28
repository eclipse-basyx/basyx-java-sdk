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

import java.io.IOException;

import org.eclipse.basyx.extensions.submodel.storage.StorageSubmodelAPI;
import org.eclipse.basyx.extensions.submodel.storage.StorageSubmodelAPIHelper.StorageSubmodelElementOperations;
import org.eclipse.basyx.extensions.submodel.storage.StorageSubmodelElement;
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
import org.junit.BeforeClass;
import org.junit.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

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
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("basyx_sdk");
		entityManager = emf.createEntityManager();
		storageAPI = new StorageSubmodelAPI(vabAPI, entityManager);
	}

	@Test
	public void testAddSubmodelElement() throws InterruptedException {
		String elemIdShort = "testAddProp";
		Property prop = new Property(true);
		prop.setIdShort(elemIdShort);
		storageAPI.addSubmodelElement(prop);

		StorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(elemIdShort);

		assertEquals(storedElement.getSerializedElementValue(), prop.getValue().toString());
	}

	@Test
	public void testAddNestedSubmodelElement() {
		String idShortPath = "/testColl/testAddProp/";
		SubmodelElementCollection coll = new SubmodelElementCollection();
		coll.setIdShort("testColl");
		storageAPI.addSubmodelElement(coll);

		StorageSubmodelElement storedColl = getSingleStorageElementWithIdShort(coll.getIdShort());
		assertEquals(storedColl.getSerializedElementValue(), coll.getValue().toString());

		Property prop = new Property(true);
		prop.setIdShort("testAddProp");
		storageAPI.addSubmodelElement(idShortPath, prop);

		StorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(idShortPath);

		assertEquals(storedElement.getSerializedElementValue(), prop.getValue().toString());
	}

	@Test
	public void testDeleteSubmodelElement() {
		String idShortPath = "/testDeleteProp";
		Property prop = new Property(true);
		prop.setIdShort("testDeleteProp");
		storageAPI.addSubmodelElement(prop);
		storageAPI.deleteSubmodelElement(idShortPath);

		StorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(idShortPath);

		assertEquals(storedElement.getOperation(), StorageSubmodelElementOperations.DELETE);
	}

	@Test
	public void testUpdateSubmodelElement() {
		String idShortPath = "testUpdateProp";
		Property prop = new Property(true);
		prop.setIdShort(idShortPath);
		storageAPI.addSubmodelElement(prop);
		storageAPI.updateSubmodelElement(idShortPath, false);

		StorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(idShortPath);
		assertFalse(Boolean.parseBoolean(storedElement.getSerializedElementValue()));
	}

	private StorageSubmodelElement getSingleStorageElementWithIdShort(String idShort) {
		Query query = entityManager.createQuery("Select s FROM StorageSubmodelElement s WHERE s.idShort = :id AND s.submodelId = :submodelId ORDER BY s.timestamp DESC");
		query.setParameter("id", idShort);
		query.setParameter("submodelId", SUBMODELID);
		query.setMaxResults(1);
		StorageSubmodelElement storedElement = (StorageSubmodelElement) query.getSingleResult();
		return storedElement;
	}
}
