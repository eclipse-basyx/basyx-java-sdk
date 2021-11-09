/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.WrongNumberOfParametersException;

/**
 * Checks submodel inputs of {@link Operation} to be the expected submodels.
 * 
 * @author fischer, espen
 */
public class OperationCheckHelper {

	private OperationCheckHelper() {
	}

	/**
	 * Checks parameter signature for given complex parameters for an operation.
	 * 
	 * @param givenParameters
	 * @param expectedVariables
	 */
	public static void checkSubmodelElementAsParameter(SubmodelElement[] givenParameters, Collection<IOperationVariable> expectedVariables) {
		Map<String, SubmodelElement> paramsByIdShortMap = createParameterMap(givenParameters);

		compareGivenWithExpectedVariables(paramsByIdShortMap, expectedVariables);
	}

	/**
	 * Checks parameter signature for given complex parameters for an operation.
	 * 
	 * @param givenParameterMap
	 * @param expectedVariables
	 */
	public static void checkSubmodelElementAsParameter(Map<String, SubmodelElement> givenParameterMap, Collection<IOperationVariable> expectedVariables) {
		compareGivenWithExpectedVariables(givenParameterMap, expectedVariables);
	}

	/**
	 * Checks if given parameters correspond with the actual length.
	 * 
	 * @param actualParameterLength
	 *            of the given parameters
	 * @param idShort
	 *            of the operation
	 * @param inputVariables
	 */
	public static void checkValidParameterLength(int actualParameterLength, String idShort, Collection<IOperationVariable> inputVariables) {
		if (actualParameterLength != inputVariables.size()) {
			throw new WrongNumberOfParametersException(idShort, inputVariables, actualParameterLength);
		}
	}

	/**
	 * Checks if the expected variables allow setValue to the given parameters.
	 * 
	 * @param givenParameters
	 * @param expectedVariables
	 */
	public static void checkSubmodelElementExpectedTypes(Object[] givenParameters, Collection<IOperationVariable> expectedVariables) {
		IOperationVariable[] expectedVarArray = expectedVariables.toArray(new IOperationVariable[expectedVariables.size()]);

		for (int i = 0; i < expectedVarArray.length; i++) {
			checkSubmodelElementExpectedType(expectedVarArray[i], givenParameters[i]);
		}

	}

	private static void checkSubmodelElementExpectedType(IOperationVariable iOperationVariable, Object value) {
		SubmodelElement submodelElement = iOperationVariable.getValue().getLocalCopy();

		try {
			submodelElement.setValue(value);
		} catch (Exception e) {
			throw new MalformedRequestException(e);
		}
	}

	private static void compareGivenWithExpectedVariables(Map<String, SubmodelElement> paramsByIdShortMap, Collection<IOperationVariable> expectedVariables) {
		for (IOperationVariable expectedVariable : expectedVariables) {
			compareExpectedVariableToGivenVariableMap(expectedVariable, paramsByIdShortMap);
		}
	}

	private static void compareExpectedVariableToGivenVariableMap(IOperationVariable expectedVariable, Map<String, SubmodelElement> paramsByIdShortMap) {
		SubmodelElement expectedParam = (SubmodelElement) expectedVariable.getValue();
		SubmodelElement givenParam = paramsByIdShortMap.get(expectedParam.getIdShort());

		compareSubmodelElements(expectedParam, givenParam);
	}

	private static void compareSubmodelElements(SubmodelElement expectedParam, SubmodelElement givenParam) {
		checkIfSubmodelElementExists(expectedParam, givenParam);
		checkModelType(expectedParam, givenParam);
	}

	private static Map<String, SubmodelElement> createParameterMap(SubmodelElement[] params) {
		Map<String, SubmodelElement> parameterMap = new HashMap<>();
		for (SubmodelElement param : params) {
			String parameterKey = getKeyForParameter(param);
			parameterMap.put(parameterKey, param);
		}
		return parameterMap;
	}

	private static String getKeyForParameter(SubmodelElement param) {
		IOperationVariable paramOperationVariable = new OperationVariable(param);
		return paramOperationVariable.getValue().getIdShort();
	}

	private static void checkIfSubmodelElementExists(SubmodelElement expectedParam, SubmodelElement givenParam) {
		if (givenParam == null) {
			throw new MalformedRequestException("Expected parameter " + expectedParam.getIdShort() + " missing in request");
		}
	}

	private static void checkModelType(SubmodelElement expectedParam, SubmodelElement givenParam) {
		if (!expectedParam.getModelType().equals(givenParam.getModelType())) {
			throw new MalformedRequestException("Given modelType " + givenParam.getModelType() + " differs from expected modelType " + expectedParam.getModelType());
		}
	}
}
