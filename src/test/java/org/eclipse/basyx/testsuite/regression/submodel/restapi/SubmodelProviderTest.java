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
package org.eclipse.basyx.testsuite.regression.submodel.restapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueTypeHelper;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.MultiSubmodelElementProvider;
import org.eclipse.basyx.submodel.restapi.OperationProvider;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.submodel.restapi.operation.CallbackResponse;
import org.eclipse.basyx.submodel.restapi.operation.ExecutionState;
import org.eclipse.basyx.submodel.restapi.operation.InvocationResponse;
import org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement.operation.AsyncOperationHelper;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.TestsuiteDirectory;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.ConnectorFactory;
import org.junit.Test;

public class SubmodelProviderTest {
	private VABConnectionManager connManager;
	protected static final String submodelAddr = "urn:fhg:es.iese:aas:1:1:submodel";
	protected static final String SMPROVIDER_PATH_PREFIX = "/" + SubmodelProvider.SUBMODEL + "/";
	protected static final String SIMPLE_FILE_VALUE = "simpleFile.xml";
	protected static final String SIMPLE_PROPERTY_VALUE = "simpleProperty";

	protected VABConnectionManager getConnectionManager() {
		if (connManager == null) {
			connManager = new VABConnectionManager(new TestsuiteDirectory(), new ConnectorFactory() {
				@Override
				protected IModelProvider createProvider(String addr) {
					// Simple submodel for testing specific mappings for submodels
					return new SubmodelProvider(new SimpleAASSubmodel("mySubmodelId"));
				}
			});
		}
		return connManager;
	}

	/**
	 * Tests all basic submodel operations with idShorts composed of various
	 * keywords.
	 *
	 * <p>
	 * The test counts as successful if it finished without any exceptions thrown.
	 */
	public void testPropertyIdShortsWithKeywords() {
		final String base_path = SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/keywords/";
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		for (String keyword : SimpleAASSubmodel.KEYWORDS) {
			Property prop = new Property();
			prop.setIdShort(keyword + "Property");
			prop.setValueType(ValueType.String);
			prop.setValue(null);

			String path = base_path + prop.getIdShort();
			submodelElement.setValue(path, prop);
			submodelElement.getValue(path);
			submodelElement.setValue(path + "/value", "Test");
			submodelElement.deleteValue(path);
		}
	}

	public void testOperationIdShortsWithKeywords() {
		final String base_path = SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/keywords/";
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);
		for (String keyword : SimpleAASSubmodel.KEYWORDS) {
			Operation op = new Operation();
			op.setIdShort(keyword + "Operation");
			op.setInvokable(() -> {
				// Do nothing
			});

			Map<String, Object> param = wrapParameter("argument", 5);

			String path = base_path + op.getIdShort();
			submodelElement.setValue(path, op);
			submodelElement.getValue(path);
			submodelElement.invokeOperation(path + "/invoke", param);
			submodelElement.deleteValue(path);
		}
	}

	@Test
	public void testSMElementCollectionIdShortWithKeywords() {
		final String base_path = SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/keywords/";
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		for (String keyword : SimpleAASSubmodel.KEYWORDS) {
			SubmodelElementCollection smc = new SubmodelElementCollection();
			smc.setIdShort(keyword + "Property");

			Property prop = new Property(500);
			prop.setIdShort("testProp");

			String path = base_path + smc.getIdShort();
			submodelElement.setValue(path, smc);
			submodelElement.setValue(path + "/" + prop.getIdShort(), prop);
			submodelElement.getValue(path);
			submodelElement.deleteValue(path);
		}
	}

	/**
	 * Tests accessing different paths that should be supported
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPathsRaw() throws Exception {
		SubmodelProvider provider = new SubmodelProvider(new SimpleAASSubmodel("mySubmodelId"));
		provider.getValue("/" + SubmodelProvider.SUBMODEL);
		provider.getValue(SMPROVIDER_PATH_PREFIX);
	}

	@Test
	public void testInvalidGetPath() {
		VABElementProxy smProxy = getConnectionManager().connectToVABElement(submodelAddr);
		try {
			smProxy.getValue("");
			fail();
		} catch (MalformedRequestException e) {
		}
	}

	/**
	 * Test creating single property
	 */
	@Test
	public void testCreateProperty() {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		// Create element
		Property prop = new Property(500);
		prop.setIdShort("newProperty");
		submodelElement.setValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/newProperty", prop);

		// Read back value
		Integer result = (Integer) submodelElement.getValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/newProperty/value");
		assertEquals(500, result.intValue());
	}

	@Test
	public void testCreatePath() {
		VABElementProxy smProxy = getConnectionManager().connectToVABElement(submodelAddr);
		Property prop = new Property(500);
		prop.setIdShort("newProperty");
		try {
			smProxy.createValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/newProperty", prop);
			fail();
		} catch (MalformedRequestException e) {
		}
	}

	@Test
	public void testInvalidSetPath() {
		VABElementProxy smProxy = getConnectionManager().connectToVABElement(submodelAddr);
		Property prop = new Property(500);
		prop.setIdShort("newProperty");
		try {
			smProxy.setValue(MultiSubmodelElementProvider.ELEMENTS + "/newProperty", prop);
			fail();
		} catch (MalformedRequestException e) {
		}
	}

	/**
	 * Test updating a full property
	 */
	@Test
	public void testUpdateFullProperty() {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		// Create element
		Property prop = new Property("newProperty", 500);
		submodelElement.setValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/newProperty", prop);

		// Update element
		Property updatedProp = new Property("newProperty", 400);
		submodelElement.setValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/newProperty", updatedProp);

		// Read back value
		Integer result = (Integer) submodelElement.getValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/newProperty/value");
		assertEquals(400, result.intValue());
	}

	/**
	 * Test creating single property
	 */
	@Test
	public void testCreatePropertyInCollection() {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		// Create element
		Property prop = new Property(500);
		prop.setIdShort("newProperty");
		submodelElement.setValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/containerRoot/newProperty", prop);

		// Read back value
		Integer result = (Integer) submodelElement.getValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/containerRoot/newProperty/value");
		assertEquals(500, result.intValue());

		submodelElement.setValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/containerRoot/container/newProperty", prop);

		// Read back value
		result = (Integer) submodelElement.getValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/containerRoot/container/newProperty/value");
		assertEquals(500, result.intValue());
	}

	@Test
	public void testUpdatePropertyInCollection() {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		// Create element
		Property prop = new Property("newProperty", 500);
		submodelElement.setValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/containerRoot/container/newProperty", prop);

		// Update element
		Property prop2 = new Property("newProperty", 400);
		submodelElement.setValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/containerRoot/container/newProperty", prop2);

		// Read back value
		Integer result = (Integer) submodelElement.getValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/containerRoot/container/newProperty/value");
		assertEquals(400, result.intValue());
	}

	/**
	 * Test reading single property
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testReadProperty() {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		// Read list of properties
		Object result = submodelElement.getValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "");
		Collection<Map<String, Object>> propertySet = (Collection<Map<String, Object>>) result;
		Map<String, Object> property = propertySet.stream().filter(elem -> elem.get(Identifiable.IDSHORT).equals("integerProperty")).findFirst().get();
		assertEquals(123, property.get(Property.VALUE));

		// Read whole property
		result = submodelElement.getValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/integerProperty");
		property = (Map<String, Object>) result;
		assertEquals(123, property.get(Property.VALUE));

		// Read idShort
		result = submodelElement.getValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/stringProperty");
		property = (Map<String, Object>) result;
		assertEquals("stringProperty", property.get(Identifiable.IDSHORT));

		// Read single value
		String resString = (String) submodelElement.getValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/stringProperty/value");
		assertEquals("Test", resString);

		// Read null value
		Object resObject = submodelElement.getValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/nullProperty/value");
		assertEquals(null, resObject);

		// Read container property
		Collection<Object> resSet = (Collection<Object>) submodelElement.getValue(SMPROVIDER_PATH_PREFIX + "submodelElements/containerRoot/value");
		assertEquals(1, resSet.size());

		// Get Collection from root-Collection
		Map<String, Object> container = (Map<String, Object>) resSet.iterator().next();

		assertEquals("container", container.get(Referable.IDSHORT));
		assertTrue(container.get(Property.VALUE) instanceof Collection<?>);

		// Get Value of nested Collection
		Map<String, Object> containerValue = SubmodelElementMapCollectionConverter.convertCollectionToIDMap(container.get(Property.VALUE));

		// Check content of nested Collection
		assertTrue(containerValue.containsKey("operationId"));
		assertTrue(containerValue.containsKey("integerProperty"));
		assertEquals(123, ((Property) containerValue.get("integerProperty")).getValue());

		// Read nested property
		String pathToNestedContainer = SMPROVIDER_PATH_PREFIX + "submodelElements/containerRoot/container";
		String pathToNestedProperty = pathToNestedContainer + "/integerProperty/";
		result = submodelElement.getValue(pathToNestedProperty);
		property = (Map<String, Object>) result;
		assertEquals(123, property.get(Property.VALUE));
	}

	/**
	 * Test updating single property
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateProperty() {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		// Update element
		submodelElement.setValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/integerProperty/value", 3);

		// Check result
		Map<String, Object> result = (Map<String, Object>) submodelElement.getValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/integerProperty");
		assertEquals(3, result.get(Property.VALUE));
	}

	/**
	 * Test updating a SubmodelElementCollection
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateSmElementCollection() {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		Collection<ISubmodelElement> smElements = new ArrayList<>();
		Property newProperty = new Property("propValue");
		newProperty.setIdShort("propIdShort");
		smElements.add(newProperty);

		// update value of smElemCollection
		String path = VABPathTools.concatenatePaths(SubmodelProvider.SUBMODEL, MultiSubmodelElementProvider.ELEMENTS, "containerRoot");
		submodelElement.setValue(path + "/value", smElements);

		// read back the collection
		Map<String, Object> map = (Map<String, Object>) submodelElement.getValue(path);

		assertTrue(map.get(Property.VALUE) instanceof Collection<?>);

		Collection<Map<String, Object>> elements = (Collection<Map<String, Object>>) map.get(Property.VALUE);
		assertEquals(1, elements.size());

		Iterator<Map<String, Object>> i = elements.iterator();

		assertEquals("propIdShort", i.next().get(Referable.IDSHORT));
	}

	/**
	 * Test updating a Property inside a SubmodelElementCollection
	 */
	@Test
	public void testUpdateElementInSmElementCollection() {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		String path = VABPathTools.concatenatePaths(SubmodelProvider.SUBMODEL, MultiSubmodelElementProvider.ELEMENTS, "containerRoot", "container", "integerProperty", "value");

		Integer value = (Integer) submodelElement.getValue(path);
		assertEquals(123, value.intValue());

		submodelElement.setValue(path, 321);

		value = (Integer) submodelElement.getValue(path);
		assertEquals(321, value.intValue());
	}

	/**
	 * Test getting a file in a collection having the "File" keyword as the start of
	 * its IdShort.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void getFileFromCollectionWithFileKeywordCollision() {
		VABElementProxy submodelProxy = getConnectionManager().connectToVABElement(submodelAddr);

		String colIdShort = "Files";
		String fileIdShort = "File";

		SubmodelElementCollection fileCollection = new SubmodelElementCollection(colIdShort);
		File fileSubmodelElement = new File("application/xml");
		String fileValue = "simpleFile.xml";
		fileSubmodelElement.setValue(fileValue);
		fileSubmodelElement.setIdShort(fileIdShort);
		fileCollection.addSubmodelElement(fileSubmodelElement);

		String smeCollectionPath = VABPathTools.concatenatePaths(SMPROVIDER_PATH_PREFIX, MultiSubmodelElementProvider.ELEMENTS, colIdShort);
		submodelProxy.setValue(smeCollectionPath, fileCollection);

		String fileValuePath = VABPathTools.concatenatePaths(smeCollectionPath, fileIdShort);
		Map<String, Object> resultingFileMap = (Map<String, Object>) submodelProxy.getValue(fileValuePath);
		File resultingFile = File.createAsFacade(resultingFileMap);
		assertEquals(fileValue, resultingFile.getValue());
	}

	/**
	 * Test getting a file with a non colliding IdShort.
	 */
	@Test
	public void getSimpleFileSubmodelElement() {
		VABElementProxy submodelProxy = getConnectionManager().connectToVABElement(submodelAddr);
		String fileIdShort = "MySimpleFile";
		createSimpleFile(submodelProxy, fileIdShort);
		assertSimpleFileValue(submodelProxy, fileIdShort);
	}

	/**
	 * Test getting a property that happens to have the "File" keyword as its
	 * IdShort.
	 */
	@Test
	public void getPropertyWithFileKeywordCollision() {
		VABElementProxy submodelProxy = getConnectionManager().connectToVABElement(submodelAddr);
		String fileIdShort = "File";

		Property fileProperty = new Property(fileIdShort, SIMPLE_PROPERTY_VALUE);
		String propertyAccessPath = VABPathTools.concatenatePaths(SMPROVIDER_PATH_PREFIX, MultiSubmodelElementProvider.ELEMENTS, fileIdShort);
		submodelProxy.setValue(propertyAccessPath, fileProperty);

		assertSimplePropertyValue(submodelProxy, fileIdShort);
	}

	/**
	 * Test getting a File with the "File" keyword as its IdShort.
	 */
	@Test
	public void getFileWithFileKeywordCollision() {
		VABElementProxy submodelProxy = getConnectionManager().connectToVABElement(submodelAddr);
		String fileIdShort = "File";
		createSimpleFile(submodelProxy, fileIdShort);
		assertSimpleFileValue(submodelProxy, fileIdShort);
	}

	private void createSimpleFile(VABElementProxy submodelProxy, String fileIdShort) {
		File fileSubmodelElement = new File("application/xml");
		fileSubmodelElement.setValue(SIMPLE_FILE_VALUE);
		fileSubmodelElement.setIdShort(fileIdShort);

		String fileAccessPath = VABPathTools.concatenatePaths(SMPROVIDER_PATH_PREFIX, MultiSubmodelElementProvider.ELEMENTS, fileIdShort);
		submodelProxy.setValue(fileAccessPath, fileSubmodelElement);
	}

	@SuppressWarnings("unchecked")
	private void assertSimpleFileValue(VABElementProxy submodelProxy, String fileIdShort) {
		String fileAccessPath = VABPathTools.concatenatePaths(SMPROVIDER_PATH_PREFIX, MultiSubmodelElementProvider.ELEMENTS, fileIdShort);
		Map<String, Object> resultingFileMap = (Map<String, Object>) submodelProxy.getValue(fileAccessPath);
		File resultingFile = File.createAsFacade(resultingFileMap);
		assertEquals(SIMPLE_FILE_VALUE, resultingFile.getValue());
	}

	@SuppressWarnings("unchecked")
	private void assertSimplePropertyValue(VABElementProxy submodelProxy, String propertyIdShort) {
		String accessPath = VABPathTools.concatenatePaths(SMPROVIDER_PATH_PREFIX, MultiSubmodelElementProvider.ELEMENTS, propertyIdShort);
		Map<String, Object> resultingPropertyMap = (Map<String, Object>) submodelProxy.getValue(accessPath);
		Property resultingProperty = Property.createAsFacade(resultingPropertyMap);
		assertEquals(SIMPLE_PROPERTY_VALUE, resultingProperty.getValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void nestedCollectionIdShortFileCollision() {
		VABElementProxy submodelProxy = getConnectionManager().connectToVABElement(submodelAddr);

		String fileIdShort = "File";
		File fileSubmodelElement = new File("application/xml");
		String fileValue = "simpleFile.xml";
		fileSubmodelElement.setValue(fileValue);
		fileSubmodelElement.setIdShort(fileIdShort);

		String innerColIdShort = "Files";
		SubmodelElementCollection innerCollection = new SubmodelElementCollection(innerColIdShort);
		innerCollection.addSubmodelElement(fileSubmodelElement);

		String outerColIdShort = "Files";
		SubmodelElementCollection outerCollection = new SubmodelElementCollection(outerColIdShort);
		outerCollection.addSubmodelElement(innerCollection);

		String outerCollectionPath = VABPathTools.concatenatePaths(SMPROVIDER_PATH_PREFIX, MultiSubmodelElementProvider.ELEMENTS, outerColIdShort);
		submodelProxy.setValue(outerCollectionPath, outerCollection);

		String fileValuePath = VABPathTools.concatenatePaths(outerCollectionPath, innerColIdShort, fileIdShort);
		Map<String, Object> resultingFileMap = (Map<String, Object>) submodelProxy.getValue(fileValuePath);
		File resultingFile = File.createAsFacade(resultingFileMap);
		assertEquals(fileValue, resultingFile.getValue());
	}

	/**
	 * Test reading a single operation
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testReadSingleOperation() {
		VABElementProxy submodel = getConnectionManager().connectToVABElement(submodelAddr);
		Map<String, Object> operation = (Map<String, Object>) submodel.getValue(SMPROVIDER_PATH_PREFIX + "submodelElements/simple");
		assertEquals("simple", operation.get(Identifiable.IDSHORT));

		try {
			submodel.getValue(SMPROVIDER_PATH_PREFIX + "submodelElements/simple/value");
			fail();
		} catch (MalformedRequestException e) {
			// An Operation has no value
		}
	}

	/**
	 * Checks if the submodel elements in a read submodel are within a collection
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testReadSubmodelCheckElementsInCollection() {
		VABElementProxy submodel = getConnectionManager().connectToVABElement(submodelAddr);
		Map<String, Object> smMap = (Map<String, Object>) submodel.getValue("/" + SubmodelProvider.SUBMODEL);
		Object o = smMap.get(Submodel.SUBMODELELEMENT);
		assertTrue(o instanceof Collection<?>);
	}

	/**
	 * Test reading all submodel elements of the submodel
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testReadSubmodelElements() {
		VABElementProxy submodel = getConnectionManager().connectToVABElement(submodelAddr);
		Collection<Map<String, Object>> set = (Collection<Map<String, Object>>) submodel.getValue(SMPROVIDER_PATH_PREFIX + "submodelElements");
		assertEquals(9, set.size());
	}

	@Test
	public void testInvalidDeletePath() {
		VABElementProxy smProxy = getConnectionManager().connectToVABElement(submodelAddr);
		try {
			smProxy.deleteValue(MultiSubmodelElementProvider.ELEMENTS + "/integerProperty");
			fail();
		} catch (MalformedRequestException e) {
		}
	}

	@Test
	public void testDeleteSingleProperty() {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		// Delete property
		submodelElement.deleteValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/integerProperty");

		// Test, if it has been deleted
		try {
			submodelElement.getValue(SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/integerProperty");
			fail();
		} catch (ResourceNotFoundException e) {
		}
	}

	/**
	 * Test deleting a single operation
	 */
	@Test
	public void testDeleteOperation() {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		// Delete operation
		submodelElement.deleteValue(SMPROVIDER_PATH_PREFIX + "submodelElements/simple");

		// Test, if it has been deleted
		try {
			submodelElement.getValue(SMPROVIDER_PATH_PREFIX + "submodelElements/simple");
			fail();
		} catch (ResourceNotFoundException e) {
		}
	}

	/**
	 * Test deleting a single property from a SubmodelElementCollection
	 */
	@Test
	public void testDeletePropertyFromCollection() {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		String path = VABPathTools.concatenatePaths(SubmodelProvider.SUBMODEL, MultiSubmodelElementProvider.ELEMENTS, "containerRoot", "container", "integerProperty");

		assertNotNull(submodelElement.getValue(path));

		// Delete property
		submodelElement.deleteValue(path);

		// Test if parent Collection is still there
		assertNotNull(submodelElement.getValue(VABPathTools.getParentPath(path)));

		// Test, if it has been deleted
		try {
			submodelElement.getValue(path);
			fail();
		} catch (ResourceNotFoundException e) {
		}

		// Test delete the Collection "container"
		path = VABPathTools.getParentPath(path);

		// Delete property
		submodelElement.deleteValue(path);

		// Test if parent Collection is still there
		assertNotNull(submodelElement.getValue(VABPathTools.getParentPath(path)));

		// Test, if it has been deleted
		try {
			submodelElement.getValue(path);
			fail();
		} catch (ResourceNotFoundException e) {
		}
	}

	@Test
	public void testInvokeOperation() {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		// Wrap parameters before invoking add-operation
		Map<String, Object> param1 = wrapParameter("FirstNumber", 5);
		Map<String, Object> param2 = wrapParameter("SecondNumber", 2);

		// Invoke operation with wrapped parameters and check result
		Object result = submodelElement.invokeOperation(SMPROVIDER_PATH_PREFIX + "submodelElements/complex/invoke", param1, param2);
		assertEquals(3, result);

		// Invoke operation on parent element
		result = submodelElement.invokeOperation(SMPROVIDER_PATH_PREFIX + "submodelElements/simple/invoke");
		assertTrue((boolean) result);
	}

	@Test
	public void testInvokeOperationWithExplicitSync() {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		// Wrap parameters before invoking add-operation
		Map<String, Object> param1 = wrapParameter("FirstNumber", 5);
		Map<String, Object> param2 = wrapParameter("SecondNumber", 2);

		// Invoke operation with wrapped parameters and check result
		Object result = submodelElement.invokeOperation(SMPROVIDER_PATH_PREFIX + "submodelElements/complex/invoke?async=false", param1, param2);
		assertEquals(3, result);

		// Invoke operation on parent element
		result = submodelElement.invokeOperation(SMPROVIDER_PATH_PREFIX + "submodelElements/simple/invoke?async=false");
		assertTrue((boolean) result);
	}

	@Test
	public void testInvalidInvokePath() {
		VABElementProxy smProxy = getConnectionManager().connectToVABElement(submodelAddr);
		try {
			smProxy.invokeOperation("/submodelElements/simple/invoke" + Operation.INVOKE);
			fail();
		} catch (MalformedRequestException e) {
		}
	}

	/**
	 * Test invoking an operation from within a SubmodelElementCollection
	 */
	@Test
	public void testInvokeOperationInCollection() {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);

		String path = VABPathTools.concatenatePaths(SubmodelProvider.SUBMODEL, MultiSubmodelElementProvider.ELEMENTS, "containerRoot", "container", "operationId", "invoke");

		Object result = submodelElement.invokeOperation(path);
		assertEquals(123, result);
	}

	/**
	 * Test getting /values of the Submodel
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValues() {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);
		Map<String, Object> values = (Map<String, Object>) submodelElement.getValue("submodel/" + SubmodelProvider.VALUES);

		assertEquals(5, values.size());

		// Check if all expected Values are present
		assertTrue(values.containsKey("containerRoot"));
		Map<String, Object> collection1 = (Map<String, Object>) values.get("containerRoot");

		assertTrue(collection1.containsKey("container"));
		Map<String, Object> collection2 = (Map<String, Object>) collection1.get("container");

		// Check the Value in /containerRoot/container/integerProperty
		assertEquals(123, collection2.get("integerProperty"));

		assertEquals("Test", values.get("stringProperty"));
		assertEquals(123, values.get("integerProperty"));
		assertEquals(null, values.get("nullProperty"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInvokeAsync() throws Exception {
		VABElementProxy elementProxy = getConnectionManager().connectToVABElement(submodelAddr);
		AsyncOperationHelper helper = new AsyncOperationHelper();

		String path = VABPathTools.concatenatePaths(SubmodelProvider.SUBMODEL, MultiSubmodelElementProvider.ELEMENTS, AsyncOperationHelper.ASYNC_OP_ID);
		elementProxy.setValue(path, helper.getAsyncOperation());

		// Wrap parameters before invoking add-operation
		Map<String, Object> param1 = wrapParameter("FirstNumber", 5);
		Map<String, Object> param2 = wrapParameter("SecondNumber", 2);

		path = VABPathTools.concatenatePaths("submodel", MultiSubmodelElementProvider.ELEMENTS, AsyncOperationHelper.ASYNC_OP_ID, "invoke?async=true");

		CallbackResponse response = CallbackResponse.createAsFacade((Map<String, Object>) elementProxy.invokeOperation(path, param1, param2));
		String requestId = response.getRequestId();

		String listPath = VABPathTools.concatenatePaths(SubmodelProvider.SUBMODEL, MultiSubmodelElementProvider.ELEMENTS, AsyncOperationHelper.ASYNC_OP_ID, OperationProvider.INVOCATION_LIST);

		// Try correct operationId, wrong requestId
		try {
			elementProxy.getValue(VABPathTools.append(listPath, "nonexistent"));
			fail();
		} catch (ResourceNotFoundException e) {
		}

		// Try wrong operationId, correct requestId
		try {
			elementProxy.getValue(SMPROVIDER_PATH_PREFIX + "submodelElements/simple/invocationList/" + requestId);
			fail();
		} catch (ResourceNotFoundException e) {
		}

		String requestPath = VABPathTools.append(listPath, requestId);

		// Check that it has not finished yet
		InvocationResponse result = (InvocationResponse) elementProxy.getValue(requestPath);
		assertEquals(ExecutionState.INITIATED, result.getExecutionState());

		helper.releaseWaitingOperation();

		result = (InvocationResponse) elementProxy.getValue(requestPath);
		assertEquals(7, result.getFirstOutput());

		// Check if the async-invocation is deleted after retrieving its result
		try {
			elementProxy.getValue(requestPath);
			fail();
		} catch (ResourceNotFoundException e) {
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInvokeAsyncException() throws Exception {
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);
		AsyncOperationHelper helper = new AsyncOperationHelper();

		String path = VABPathTools.concatenatePaths(SubmodelProvider.SUBMODEL, MultiSubmodelElementProvider.ELEMENTS, AsyncOperationHelper.ASYNC_EXCEPTION_OP_ID);
		submodelElement.setValue(path, helper.getAsyncExceptionOperation());

		path = VABPathTools.concatenatePaths(SubmodelProvider.SUBMODEL, MultiSubmodelElementProvider.ELEMENTS, AsyncOperationHelper.ASYNC_EXCEPTION_OP_ID, "invoke?async=true");

		CallbackResponse response = (CallbackResponse) submodelElement.invokeOperation(path);
		String requestId = response.getRequestId();

		String requestPath = VABPathTools.concatenatePaths(SubmodelProvider.SUBMODEL, MultiSubmodelElementProvider.ELEMENTS, AsyncOperationHelper.ASYNC_EXCEPTION_OP_ID, OperationProvider.INVOCATION_LIST, requestId);

		// Check that it has not finished yet
		InvocationResponse invResp = InvocationResponse.createAsFacade((Map<String, Object>) submodelElement.getValue(requestPath));
		assertNotEquals(ExecutionState.COMPLETED, invResp.getExecutionState());
		assertNotEquals(ExecutionState.FAILED, invResp.getExecutionState());

		helper.releaseWaitingOperation();

		invResp = InvocationResponse.createAsFacade((Map<String, Object>) submodelElement.getValue(requestPath));
		assertEquals(ExecutionState.FAILED, invResp.getExecutionState());

		// Check if the async-invocation is deleted after retrieving its result
		try {
			submodelElement.getValue(requestPath);
			fail();
		} catch (ResourceNotFoundException e) {
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIndirectSubmodelElementCollectionValueType() {
		VABElementProxy submodelProvider = getConnectionManager().connectToVABElement(submodelAddr);
		String containerRootPath = VABPathTools.concatenatePaths(SubmodelProvider.SUBMODEL, MultiSubmodelElementProvider.ELEMENTS, "containerRoot");
		Map<String, Object> directCollection = (Map<String, Object>) submodelProvider.getValue(containerRootPath);
		Object indirectCollectionValue = directCollection.get(Property.VALUE);

		assertTrue(indirectCollectionValue instanceof Collection<?>);
	}

	@Test
	public void testDirectSubmodelElementCollectionValueType() {
		VABElementProxy submodelProvider = getConnectionManager().connectToVABElement(submodelAddr);
		String containerRootValuePath = VABPathTools.concatenatePaths(SubmodelProvider.SUBMODEL, MultiSubmodelElementProvider.ELEMENTS, "containerRoot", Property.VALUE);
		Object directCollectionValue = submodelProvider.getValue(containerRootValuePath);

		assertTrue(directCollectionValue instanceof Collection<?>);
	}

	protected Map<String, Object> wrapParameter(String name, Object value) {
		Map<String, Object> param = new LinkedHashMap<>();
		param.put(Identifiable.IDSHORT, name);
		param.put(Property.VALUE, value);
		param.put(Property.VALUETYPE, ValueTypeHelper.getType(value).toString());
		return param;
	}
}
