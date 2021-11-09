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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for different parameters in operations with submodelElements.
 *
 * @author espen, fischer
 *
 */
public abstract class TestOperationParameterSuite {
	protected static final String RELATIONSHIP_ID_SHORT = "relationshipIdShort";
	protected static final String RELATIONSHIP_FIRST_REFERENCE_ID_SHORT = "firstId";
	protected static final String RELATIONSHIP_SECOND_REFERENCE_ID_SHORT = "secondId";
	protected static final String OPERATION_ID_SHORT = "operationIdShort";
	protected static final String OPERATION_PROPERTY_ID_SHORT = "propertyId";

	protected static final Function<Map<String, SubmodelElement>, SubmodelElement[]> RELATIONSHIP_FUNC = (Function<Map<String, SubmodelElement>, SubmodelElement[]>) inputMap -> {
		return new SubmodelElement[] { inputMap.get(RELATIONSHIP_ID_SHORT) };
	};

	protected static final Function<Map<String, SubmodelElement>, SubmodelElement[]> OPERATION_FUNC = (Function<Map<String, SubmodelElement>, SubmodelElement[]>) inputMap -> {
		return new SubmodelElement[] { inputMap.get(OPERATION_ID_SHORT) };
	};

	protected IOperation relationshipOperation;
	protected IOperation operationOperation;

	/**
	 * Converts an Operation into the IOperation to be tested
	 */
	protected abstract IOperation prepareOperation(Operation operation);

	@Before
	public void setup() {
		relationshipOperation = createRelationshipOperation();
		operationOperation = createOperationOperation();
	}

	protected IOperation createOperationOperation() {
		Operation op = new Operation("operation");
		op.setInputVariables(createOperationOperationVariable());
		op.setOutputVariables(createOperationOperationVariable());
		op.setWrappedInvokable(OPERATION_FUNC);

		return prepareOperation(op);
	}

	protected IOperation createRelationshipOperation() {
		Operation op = new Operation("relationship");
		op.setInputVariables(createRelationshipOperationVariable());
		op.setOutputVariables(createRelationshipOperationVariable());
		op.setWrappedInvokable(RELATIONSHIP_FUNC);

		return prepareOperation(op);
	}

	private Collection<OperationVariable> createRelationshipOperationVariable() {
		RelationshipElement relationshipElementAsOperationVariable = createRelationshipElementForOperationInputVariables();

		OperationVariable relationshipElementVariable = new OperationVariable(relationshipElementAsOperationVariable);

		return Arrays.asList(relationshipElementVariable);
	}

	private RelationshipElement createRelationshipElementForOperationInputVariables() {
		Reference firstReference = new Reference(new Key(KeyElements.RELATIONSHIPELEMENT, false, "", KeyType.IDSHORT));
		Reference secondReference = new Reference(new Key(KeyElements.RELATIONSHIPELEMENT, false, "", KeyType.IDSHORT));
		RelationshipElement relationshipElementAsOperationVariable = new RelationshipElement(RELATIONSHIP_ID_SHORT, firstReference, secondReference);

		relationshipElementAsOperationVariable.setModelingKind(ModelingKind.TEMPLATE);

		return relationshipElementAsOperationVariable;
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

	@Test
	public void testInvokeSubmodelElementWithValues() {
		RelationshipElement input = createRelationshipElementAsInput();
		SubmodelElement[] responseArray = relationshipOperation.invoke(input);
		RelationshipElement responseRelationship = (RelationshipElement) responseArray[0];
		checkRelationshipElementOutput(responseRelationship);
	}

	private RelationshipElement createRelationshipElementAsInput() {
		Reference firstReference = new Reference(new Key(KeyElements.RELATIONSHIPELEMENT, false, RELATIONSHIP_FIRST_REFERENCE_ID_SHORT, KeyType.IDSHORT));
		Reference secondReference = new Reference(new Key(KeyElements.RELATIONSHIPELEMENT, false, RELATIONSHIP_SECOND_REFERENCE_ID_SHORT, KeyType.IDSHORT));
		RelationshipElement relationshipElement = new RelationshipElement(RELATIONSHIP_ID_SHORT, firstReference, secondReference);

		return relationshipElement;
	}

	private void checkRelationshipElementOutput(RelationshipElement responseRelationship) {
		List<IKey> firstReferenceKeys = responseRelationship.getFirst().getKeys();
		List<IKey> secondReferenceKeys = responseRelationship.getSecond().getKeys();

		IKey lastKeyFirstReference = firstReferenceKeys.get(firstReferenceKeys.size() - 1);
		IKey lastKeySecondReference = secondReferenceKeys.get(firstReferenceKeys.size() - 1);

		assertEquals(RELATIONSHIP_ID_SHORT, responseRelationship.getIdShort());
		assertEquals(RELATIONSHIP_FIRST_REFERENCE_ID_SHORT, lastKeyFirstReference.getValue());
		assertEquals(RELATIONSHIP_SECOND_REFERENCE_ID_SHORT, lastKeySecondReference.getValue());
	}

	@Test
	public void testInvokeSubmodelElementWithoutValues() {
		Operation input = createOperationAsInput();
		SubmodelElement[] responseArray = operationOperation.invoke(input);
		Operation responseOperation = (Operation) responseArray[0];
		checkOperationOutput(responseOperation);
	}

	private Operation createOperationAsInput() {
		Operation operation = new Operation("inputOperation");
		operation.setIdShort(OPERATION_ID_SHORT);

		return operation;
	}

	private void checkOperationOutput(Operation responseOperation) {
		assertEquals(OPERATION_ID_SHORT, responseOperation.getIdShort());
		assertTrue(responseOperation.getInputVariables().isEmpty());
	}
}
