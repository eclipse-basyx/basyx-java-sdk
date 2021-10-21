/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedSubmodel;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.testsuite.regression.submodel.metamodel.TestSubmodelSuite;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.support.TypeDestroyer;
import org.eclipse.basyx.vab.support.TypeDestroyingProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if a ConnectSubmodel can be created and used correctly
 *
 * @author schnicke
 *
 */
public class TestConnectedSubmodel extends TestSubmodelSuite {

	// String constants used in this test case
	private final static String OP = "add";

	private final String OPERATION_ID = "operation_id";

	ConnectedSubmodel submodel;

	@Before
	public void build() {

		Submodel reference = getReferenceSubmodel();
		Operation op = new Operation((Function<Object[], Object> & Serializable) obj -> {
			return (int) obj[0] + (int) obj[1];
		});
		Property aProp = new Property("a", 1);
		aProp.setModelingKind(ModelingKind.TEMPLATE);
		Property bProp = new Property("b", 2);
		bProp.setModelingKind(ModelingKind.TEMPLATE);
		Property rProp = new Property("r", 3);
		rProp.setModelingKind(ModelingKind.TEMPLATE);
		OperationVariable a = new OperationVariable(aProp);
		OperationVariable b = new OperationVariable(bProp);
		OperationVariable r = new OperationVariable(rProp);
		op.setInputVariables(Arrays.asList(a, b));
		op.setOutputVariables(Collections.singletonList(r));
		op.setIdShort(OP);
		reference.addSubmodelElement(op);

		SubmodelProvider provider = new SubmodelProvider(new TypeDestroyingProvider(new VABLambdaProvider(reference)));

		// Create the ConnectedSubmodel based on the manager
		submodel = new ConnectedSubmodel(new VABElementProxy("/" + SubmodelProvider.SUBMODEL, provider));
	}

	/**
	 * Tests if a Submodel's operations can be used correctly
	 *
	 * @throws Exception
	 */
	@Test
	public void operationsTest() throws Exception {
		// Retrieve all operations
		Map<String, IOperation> ops = submodel.getOperations();

		// Check if number of operations is as expected
		assertEquals(1, ops.size());

		// Check the operation itself
		IOperation op = ops.get(OP);
		assertEquals(5, op.invokeSimple(2, 3));
	}

	@Test
	public void saveAndLoadOperationTest() throws Exception {
		// Get sample Operations and save them into Submodel
		Map<String, IOperation> testOperations = getTestOperations();
		for (ISubmodelElement element : testOperations.values()) {
			submodel.addSubmodelElement(element);
		}

		// Load it
		Map<String, IOperation> map = submodel.getOperations();

		// Check if it loaded correctly
		checkOperations(map);
	}

	@Test
	public void testGetLocalCopy() {
		Submodel reference = getReferenceSubmodel();
		SubmodelProvider provider = new SubmodelProvider(new TypeDestroyingProvider(new VABLambdaProvider(reference)));

		// Create the ConnectedSubmodel based on the manager
		VABElementProxy smProxy = new VABElementProxy("/" + SubmodelProvider.SUBMODEL, provider);
		ConnectedSubmodel cSM = new ConnectedSubmodel(smProxy);

		Object expected = TypeDestroyer.destroyType(reference);
		Object actual = cSM.getLocalCopy();
		assertEquals(expected, actual);
	}

	/**
	 * Generates test IOperations
	 */
	private Map<String, IOperation> getTestOperations() {
		Map<String, IOperation> ret = new HashMap<>();

		Operation operation = new Operation();
		operation.setIdShort(OPERATION_ID);
		ret.put(operation.getIdShort(), operation);

		return ret;
	}

	/**
	 * Checks if the given Map contains all expected IOperations
	 */
	private void checkOperations(Map<String, ? extends ISubmodelElement> actual) throws Exception {
		assertNotNull(actual);

		Map<String, IOperation> expected = getTestOperations();

		IOperation expectedOperation = expected.get(OPERATION_ID);
		IOperation actualOperation = (IOperation) actual.get(OPERATION_ID);

		assertNotNull(actualOperation);
		assertEquals(expectedOperation.getIdShort(), actualOperation.getIdShort());
	}

	@Override
	protected ConnectedSubmodel getSubmodel() {
		return submodel;
	}
}
