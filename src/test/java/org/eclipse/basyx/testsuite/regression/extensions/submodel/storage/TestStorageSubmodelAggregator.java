package org.eclipse.basyx.testsuite.regression.extensions.submodel.storage;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.basyx.extensions.submodel.storage.StorageSubmodelAPI;
import org.eclipse.basyx.extensions.submodel.storage.StorageSubmodelAggregator;
import org.eclipse.basyx.extensions.submodel.storage.elements.IStorageSubmodelElement;
import org.eclipse.basyx.extensions.submodel.storage.elements.StorageSubmodelElementComponent.StorageSubmodelElementOperations;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
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

/**
 * Class to test check if the StorageSubmodelAggregator creates database entries
 *
 * @author fischer
 *
 */
@RunWith(Parameterized.class)
public class TestStorageSubmodelAggregator {
	protected Submodel submodel;
	private static final String SUBMODEL_IDSHORT = "submodelIdShort";
	private static final String SUBMODEL_ID = "submodelId";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);

	private static final String NEW_SUBMODEL_IDSHORT = "newSubmodelIdShort";
	private static final String NEW_SUBMODEL_ID = "newSubmodelId";
	private static final Identifier NEW_SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRDI, NEW_SUBMODEL_ID);

	private static final String SUBMODEL_ELEMENT_IDSHORT = "submodelElementIdShort";
	private static final Property SUBMODEL_ELEMENT_PROPERTY = new Property(SUBMODEL_ELEMENT_IDSHORT, 1);

	private String storageOption;
	private EntityManager entityManager;
	private StorageSubmodelAggregator storageSubmodelAggregator;
	private StorageSubmodelAPI storageAPI;

	@Parameterized.Parameters
	public static String[] storageOptions() {
		return new String[] { "storageElement_sql", "storageElement_nosql" };
	}

	public TestStorageSubmodelAggregator(String storageOption) {
		this.storageOption = storageOption;
	}

	@Before
	public void setUp() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(storageOption);
		entityManager = emf.createEntityManager();
		storageSubmodelAggregator = new StorageSubmodelAggregator(entityManager);

		submodel = new Submodel(SUBMODEL_IDSHORT, SUBMODEL_IDENTIFIER);
		submodel.addSubmodelElement(SUBMODEL_ELEMENT_PROPERTY);
		storageSubmodelAggregator.createSubmodel(submodel);

		VABSubmodelAPI vabAPI = new VABSubmodelAPI(new VABMapProvider(submodel));
		storageAPI = new StorageSubmodelAPI(vabAPI, entityManager);
	}

	@Test
	public void testCreateSubmodelWithElement() {
		Submodel newSubmodel = new Submodel(NEW_SUBMODEL_IDSHORT, NEW_SUBMODEL_IDENTIFIER);

		String elementIdShort = "newElement";
		Property newProp = createTestProperty(elementIdShort);
		newSubmodel.addSubmodelElement(newProp);

		storageSubmodelAggregator.createSubmodel(newSubmodel);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(NEW_SUBMODEL_ID, elementIdShort);
		assertEquals(newProp.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(StorageSubmodelElementOperations.CREATE, storedElement.getOperation());
	}

	@Test
	public void testCreateSubmodelElementCollection() {
		String collectionIdShort = "submodelElementCollectionIdShort";
		SubmodelElementCollection collection = new SubmodelElementCollection(collectionIdShort);

		String elementIdShort = "newElementInCollection";
		Property newProp = createTestProperty(elementIdShort);
		collection.addSubmodelElement(newProp);

		submodel.addSubmodelElement(collection);
		storageSubmodelAggregator.createSubmodel(submodel);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(SUBMODEL_ID, VABPathTools.concatenatePaths(collectionIdShort, elementIdShort));
		assertEquals(newProp.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(StorageSubmodelElementOperations.CREATE, storedElement.getOperation());
	}

	@Test
	public void testUpdateSubmodelAddNewElement() {
		String elementIdShort = "newElementForCollection";
		Property newProp = createTestProperty(elementIdShort);
		submodel.addSubmodelElement(newProp);

		storageSubmodelAggregator.updateSubmodel(submodel);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(SUBMODEL_ID, elementIdShort);
		assertEquals(newProp.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(StorageSubmodelElementOperations.CREATE, storedElement.getOperation());
	}

	@Test
	public void testUpdateSubmodelElementCollection() {
		String collectionIdShort = "submodelElementCollectionIdShort";
		SubmodelElementCollection collection = new SubmodelElementCollection(collectionIdShort);

		String elementIdShort = "newElementInCollection";
		Property newProp = createTestProperty(elementIdShort);
		collection.addSubmodelElement(newProp);

		submodel.addSubmodelElement(collection);
		storageSubmodelAggregator.createSubmodel(submodel);

		newProp.setValue(false);
		storageSubmodelAggregator.updateSubmodel(submodel);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(SUBMODEL_ID, VABPathTools.concatenatePaths(collectionIdShort, elementIdShort));
		assertEquals(newProp.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(StorageSubmodelElementOperations.UPDATE, storedElement.getOperation());
	}

	@Test
	public void testDeleteSubmodelWithElementByIdentifier() {
		storageSubmodelAggregator.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(SUBMODEL_ID, SUBMODEL_ELEMENT_IDSHORT);
		assertEquals(StorageSubmodelElementOperations.DELETE, storedElement.getOperation());
	}

	@Test
	public void testDeleteSubmodelByIdShort() {
		storageSubmodelAggregator.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(SUBMODEL_ID, SUBMODEL_ELEMENT_IDSHORT);
		assertEquals(StorageSubmodelElementOperations.DELETE, storedElement.getOperation());
	}

	private Property createTestProperty(String idShort) {
		Property newProp = new Property(true);
		newProp.setIdShort(idShort);
		return newProp;
	}

	private IStorageSubmodelElement getSingleStorageElementWithIdShort(String submodelId, String elementIdShort) {
		List<IStorageSubmodelElement> elements = storageAPI.getSubmodelElementHistoricValues(submodelId, elementIdShort);
		if (elements.isEmpty()) {
			return null;
		}

		IStorageSubmodelElement storedElement = elements.get(elements.size() - 1);
		return storedElement;
	}

	@After
	public void tearDown() {
		String[] submodelIds = { SUBMODEL_ID, NEW_SUBMODEL_ID };
		for (String submodelId : submodelIds) {
			List<IStorageSubmodelElement> elements = storageAPI.getSubmodelElementHistoricValues(submodelId);
			entityManager.getTransaction().begin();
			elements.forEach((n) -> entityManager.remove(n));
			entityManager.getTransaction().commit();
		}
	}
}
