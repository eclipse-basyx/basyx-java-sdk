package org.eclipse.basyx.testsuite.regression.extensions.submodel.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.basyx.extensions.submodel.storage.api.StorageSubmodelAPI;
import org.eclipse.basyx.extensions.submodel.storage.elements.IStorageSubmodelElement;
import org.eclipse.basyx.extensions.submodel.storage.elements.StorageSubmodelElementComponentHelper;
import org.eclipse.basyx.extensions.submodel.storage.elements.StorageSubmodelElementOperations;
import org.eclipse.basyx.extensions.submodel.storage.elements.StorageSubmodelElementStorageOptions;
import org.eclipse.basyx.extensions.submodel.storage.retrieval.StorageSubmodelElementRetrievalAPI;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
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
public class TestStorageSubmodelQualifier {
	private final String AAS_ID = "testaasid";
	private final String SUBMODEL_ID = "testsubmodelid";

	private StorageSubmodelAPI storageAPI;
	private StorageSubmodelElementRetrievalAPI retrievalAPI;
	private EntityManager entityManager;
	private String storageOption;

	@Parameterized.Parameters
	public static String[] storageOptions() {
		return new String[] { "storageElement_sql", "storageElement_nosql" };
	}

	public TestStorageSubmodelQualifier(String storageOption) {
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
		storageAPI = new StorageSubmodelAPI(vabAPI, entityManager, StorageSubmodelElementStorageOptions.QUALIFIER);
		retrievalAPI = new StorageSubmodelElementRetrievalAPI(entityManager);
	}

	private Collection<IConstraint> createStorageQualifiers() {
		Qualifier storageQualifier = new Qualifier(StorageSubmodelElementComponentHelper.QUALIFIER, "true", "string", null);

		Collection<IConstraint> qualifiers = new ArrayList<>();
		qualifiers.add(storageQualifier);

		return qualifiers;
	}

	@Test
	public void testStorageIfQualifierIsSet() {
		String elemIdShort = "testPropWithQualifier";
		Property prop = new Property(true);
		prop.setIdShort(elemIdShort);
		prop.setQualifiers(createStorageQualifiers());
		storageAPI.addSubmodelElement(prop);

		IStorageSubmodelElement storedElement = retrievalAPI.getSubmodelElementHistoricValues(SUBMODEL_ID, elemIdShort).get(0);

		assertEquals(prop.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(prop.getModelType(), storedElement.getModelType());
		assertEquals(StorageSubmodelElementOperations.CREATE, storedElement.getOperation());
	}

	@Test
	public void testStorageIfQualifierIsNotSet() {
		String elemIdShort = "testPropWithoutQualifier";
		Property prop = new Property(true);
		prop.setIdShort(elemIdShort);
		storageAPI.addSubmodelElement(prop);

		assertTrue(retrievalAPI.getSubmodelElementHistoricValues(SUBMODEL_ID, elemIdShort).isEmpty());
	}

	@Test
	public void testComplexSubmodelElementStructureWithQualifiers() {
		String parentElementIdShort = "parentElementIdShort";
		SubmodelElementCollection collectionToStore = new SubmodelElementCollection(parentElementIdShort);
		collectionToStore.setQualifiers(createStorageQualifiers());

		String propToStoreIdShort = "testPropWithQualifier";
		Property propToStore = createPropWithQualifier(propToStoreIdShort);
		collectionToStore.addSubmodelElement(propToStore);

		String propVolatileIdShort = "testPropWithoutQualifier";
		Property propVolatile = createPropWithoutQualifier(propVolatileIdShort);
		collectionToStore.addSubmodelElement(propVolatile);

		storageAPI.addSubmodelElement(collectionToStore);

		checkElementCreation(parentElementIdShort, propToStoreIdShort, propVolatileIdShort, propToStore);

		propToStore.setValue(false);
		propVolatile.setValue(false);

		storageAPI.updateSubmodelElement(parentElementIdShort, collectionToStore.getValue());

		checkElementUpdate(parentElementIdShort, propToStoreIdShort, propVolatileIdShort, propToStore);
	}

	private Property createPropWithQualifier(String elemIdShort) {
		Property propWithQualifier = new Property(true);
		propWithQualifier.setIdShort(elemIdShort);
		propWithQualifier.setQualifiers(createStorageQualifiers());
		return propWithQualifier;
	}

	private Property createPropWithoutQualifier(String elemIdShort) {
		Property propWithoutQualifier = new Property(true);
		propWithoutQualifier.setIdShort(elemIdShort);
		return propWithoutQualifier;
	}

	private void checkElementCreation(String parentElementIdShort, String propStoredIdShort, String propVolatileIdShort, Property propToStore) {
		IStorageSubmodelElement storedCollection = retrievalAPI.getSubmodelElementHistoricValues(SUBMODEL_ID, parentElementIdShort).get(0);
		assertEquals(StorageSubmodelElementOperations.CREATE, storedCollection.getOperation());

		IStorageSubmodelElement storedProp = retrievalAPI.getSubmodelElementHistoricValues(SUBMODEL_ID, VABPathTools.concatenatePaths(parentElementIdShort, propStoredIdShort)).get(0);
		assertEquals(propToStore.getValue().toString(), storedProp.getSerializedElementValue());
		assertEquals(propToStore.getModelType(), storedProp.getModelType());
		assertEquals(StorageSubmodelElementOperations.CREATE, storedProp.getOperation());

		assertTrue(retrievalAPI.getSubmodelElementHistoricValues(SUBMODEL_ID, VABPathTools.concatenatePaths(parentElementIdShort, propVolatileIdShort)).isEmpty());
	}

	private void checkElementUpdate(String parentElementIdShort, String propStoredIdShort, String propVolatileIdShort, Property propStored) {
		IStorageSubmodelElement storedCollection = retrievalAPI.getSubmodelElementHistoricValues(SUBMODEL_ID, parentElementIdShort).get(0);
		assertEquals(StorageSubmodelElementOperations.UPDATE, storedCollection.getOperation());

		IStorageSubmodelElement storedProp = retrievalAPI.getSubmodelElementHistoricValues(SUBMODEL_ID, VABPathTools.concatenatePaths(parentElementIdShort, propStoredIdShort)).get(0);
		assertEquals(propStored.getValue().toString(), storedProp.getSerializedElementValue());
		assertEquals(propStored.getModelType(), storedProp.getModelType());
		assertEquals(StorageSubmodelElementOperations.UPDATE, storedProp.getOperation());

		assertTrue(retrievalAPI.getSubmodelElementHistoricValues(SUBMODEL_ID, VABPathTools.concatenatePaths(parentElementIdShort, propVolatileIdShort)).isEmpty());
	}

	@After
	public void tearDown() {
		// clear database table again
		List<IStorageSubmodelElement> elements = retrievalAPI.getSubmodelElementHistoricValues(SUBMODEL_ID);
		entityManager.getTransaction().begin();
		elements.forEach((n) -> entityManager.remove(n));
		entityManager.getTransaction().commit();
	}

}
