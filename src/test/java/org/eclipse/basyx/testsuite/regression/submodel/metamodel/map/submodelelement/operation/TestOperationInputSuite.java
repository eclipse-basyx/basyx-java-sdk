/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement.operation;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for different parameters in operations with submodelElements.
 *
 * @author espen, fischer
 *
 */
public abstract class TestOperationInputSuite {
	protected static final String IN_VALUE = "inValue";
	protected static final String IN_IDSHORT1 = "testIn1";
	protected static final String IN_IDSHORT2 = "testIn2";
	protected static final String OUT_VALUE = "outValue";
	protected static final String OUT_IDSHORT = "testOut";
	protected static final String INOUT_VALUE = "inOutValue";
	protected static final String INOUT_IDSHORT = "testInOut";
	protected static final String RELATIONSHIP_ID_SHORT = "relationshipIdShort";
	protected static final String RELATIONSHIP_FIRST_REFERENCE_ID_SHORT = "firstId";
	protected static final String RELATIONSHIP_SECOND_REFERENCE_ID_SHORT = "secondId";
	protected static final String OPERATION_ID_SHORT = "operationIdShort";
	protected static final String OPERATION_PROPERTY_ID_SHORT = "propertyId";

	protected static Collection<OperationVariable> TWO_IN;
	protected static Collection<OperationVariable> OUT;
	protected static Collection<OperationVariable> INOUT;

	protected static final Function<Map<String, SubmodelElement>, SubmodelElement[]> OPERATION_FUNC = (Function<Map<String, SubmodelElement>, SubmodelElement[]>) inputMap -> {
		return new SubmodelElement[] { inputMap.get(OPERATION_ID_SHORT) };
	};

	protected static final Function<Object[], Object> SIMPLE_FUNC = (Function<Object[], Object>) v -> {
		return (int) v[0] + (int) v[1];
	};

	protected IOperation simpleOperation;
	protected IOperation operationOperation;

	/**
	 * Converts an Operation into the IOperation to be tested
	 */
	protected abstract IOperation prepareOperation(Operation operation);

	@Before
	public void setup() {
		TWO_IN = createTwoInputVariables();
		OUT = createOutputVariables();
		INOUT = createInOutVariables();

		operationOperation = createOperationOperation();
		simpleOperation = createAddOperation();
	}

	private IOperation createAddOperation() {
		Operation op = new Operation(TWO_IN, OUT, INOUT, SIMPLE_FUNC);
		op.setIdShort("SimpleOperation");
		return prepareOperation(op);
	}

	private IOperation createOperationOperation() {
		Operation op = new Operation("operation");
		op.setInputVariables(createOperationOperationVariable());
		op.setOutputVariables(createOperationOperationVariable());
		op.setWrappedInvokable(OPERATION_FUNC);

		return prepareOperation(op);
	}

	private Collection<OperationVariable> createOperationOperationVariable() {
		Operation operation = createOperationForOperationInputVariables();

		OperationVariable operationVariable = new OperationVariable(operation);

		return Arrays.asList(operationVariable);
	}

	private Operation createOperationForOperationInputVariables() {
		Property p1 = new Property(OPERATION_PROPERTY_ID_SHORT, "");
		OperationVariable opV = new OperationVariable(p1);

		Operation operation = new Operation("inputOperation");
		operation.setInputVariables(Arrays.asList(opV));
		operation.setIdShort(OPERATION_ID_SHORT);
		operation.setModelingKind(ModelingKind.TEMPLATE);

		return operation;
	}

	private RelationshipElement createRelationshipElementAsInput(String idShort) {
		Reference firstReference = new Reference(new Key(KeyElements.RELATIONSHIPELEMENT, false, RELATIONSHIP_FIRST_REFERENCE_ID_SHORT, KeyType.IDSHORT));
		Reference secondReference = new Reference(new Key(KeyElements.RELATIONSHIPELEMENT, false, RELATIONSHIP_SECOND_REFERENCE_ID_SHORT, KeyType.IDSHORT));
		RelationshipElement relationshipElement = new RelationshipElement(idShort, firstReference, secondReference);

		return relationshipElement;
	}

	private Collection<OperationVariable> createInOutVariables() {
		Property inOutProp = new Property(INOUT_IDSHORT, INOUT_VALUE);
		inOutProp.setValueType(ValueType.Integer);
		inOutProp.setModelingKind(ModelingKind.TEMPLATE);
		return Arrays.asList(new OperationVariable(inOutProp));
	}

	private Collection<OperationVariable> createOutputVariables() {
		Property outProp = new Property(OUT_IDSHORT, OUT_VALUE);
		outProp.setValueType(ValueType.Integer);
		outProp.setModelingKind(ModelingKind.TEMPLATE);

		return Arrays.asList(new OperationVariable(outProp));
	}

	private Collection<OperationVariable> createTwoInputVariables() {
		Property inProp1 = new Property(IN_IDSHORT1, IN_VALUE);
		inProp1.setModelingKind(ModelingKind.TEMPLATE);

		Property inProp2 = new Property(IN_IDSHORT2, IN_VALUE);
		inProp2.setModelingKind(ModelingKind.TEMPLATE);

		return Arrays.asList(new OperationVariable(inProp1), new OperationVariable(inProp2));
	}

	@Test
	public void testInvokeWithInvalidIdShortAndModelType() {
		RelationshipElement input = createRelationshipElementAsInput(RELATIONSHIP_ID_SHORT);

		try {
			operationOperation.invoke(input);
			fail();
		} catch (Exception e) {
			// Exceptions from ConnectedOperation are wrapped in ProviderException
			assertTrue(e instanceof MalformedRequestException || e.getCause() instanceof MalformedRequestException);
		}
	}

	@Test
	public void testInvokeWithInvalidModelType() {
		RelationshipElement input = createRelationshipElementAsInput(OPERATION_ID_SHORT);

		try {
			operationOperation.invoke(input);
			fail();
		} catch (Exception e) {
			// Exceptions from ConnectedOperation are wrapped in ProviderException
			assertTrue(e instanceof MalformedRequestException || e.getCause() instanceof MalformedRequestException);
		}
	}

	@Test
	public void testInvokeWithInvalidSubmodelElementType() {
		Property invalidInput = new Property(OPERATION_ID_SHORT, 2);

		try {
			operationOperation.invoke(invalidInput);
			fail();
		} catch (Exception e) {
			// Exceptions from ConnectedOperation are wrapped in ProviderException
			assertTrue(e instanceof MalformedRequestException || e.getCause() instanceof MalformedRequestException);
		}
	}

	@Test
	public void testSimpleInvokeWithInvalidSubmodelElementType() {
		try {
			operationOperation.invokeSimple("10");
			fail();
		} catch (Exception e) {
			// Exceptions from ConnectedOperation are wrapped in ProviderException
			assertTrue(e instanceof MalformedRequestException || e.getCause() instanceof MalformedRequestException);
		}
	}

	@Test
	public void testInvokeSimpleOperationWithInvalidSubmodelElementType() {
		RelationshipElement input = createRelationshipElementAsInput(RELATIONSHIP_ID_SHORT);

		try {
			simpleOperation.invoke(input, input);
			fail();
		} catch (Exception e) {
			// Exceptions from ConnectedOperation are wrapped in ProviderException
			assertTrue(e instanceof MalformedRequestException || e.getCause() instanceof MalformedRequestException);
		}
	}
}
