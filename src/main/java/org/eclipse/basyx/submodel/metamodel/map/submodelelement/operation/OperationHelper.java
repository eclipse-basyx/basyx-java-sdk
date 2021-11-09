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
import java.util.stream.StreamSupport;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;

public class OperationHelper {
	public static Property createPropertyTemplate(ValueType type) {
		Property prop = new Property();
		prop.setValueType(type);
		prop.setModelingKind(ModelingKind.TEMPLATE);
		return prop;
	}

	public static SubmodelElement[] wrapParameters(Collection<IOperationVariable> inputVariables, Object... unwrappedParameters) {
		IOperationVariable[] inputVarArray = inputVariables.toArray(new IOperationVariable[inputVariables.size()]);
		SubmodelElement[] wrappedParameters = new SubmodelElement[inputVariables.size()];
		for (int i = 0; i < wrappedParameters.length; i++) {
			wrappedParameters[i] = wrapSingleParameter(inputVarArray[i], unwrappedParameters[i]);
		}
		return wrappedParameters;
	}

	public static Map<String, SubmodelElement> wrapSimpleInputParametersInMap(Object[] simpleParams, Collection<IOperationVariable> inputVariables) {
		SubmodelElement[] wrappedParameterArray = OperationHelper.wrapParameters(inputVariables, simpleParams);
		return convertSubmodelElementArrayToMap(wrappedParameterArray);
	}

	public static SubmodelElement[] wrapResult(Object unwrappedResult, Collection<IOperationVariable> outputVariables) {
		if (outputVariables.isEmpty()) {
			return new SubmodelElement[] {};
		}
		IOperationVariable resultTemplate = outputVariables.iterator().next();
		SubmodelElement wrappedResult = OperationHelper.wrapSingleParameter(resultTemplate, unwrappedResult);
		return new SubmodelElement[] { wrappedResult };
	}

	public static SubmodelElement wrapSingleParameter(IOperationVariable template, Object simpleValue) {
		ISubmodelElement submodelElementTemplate = template.getValue();
		SubmodelElement submodelElementCopy = submodelElementTemplate.getLocalCopy();
		submodelElementCopy.setValue(simpleValue);
		return submodelElementCopy;
	}

	public static Object[] unwrapInputParameters(Map<String, SubmodelElement> wrappedParamMap, Collection<IOperationVariable> inputVariables) {
		return StreamSupport.stream(inputVariables.spliterator(), false).map(inputVar -> unwrapInputParameter(wrappedParamMap, inputVar)).toArray();
	}

	public static Object unwrapInputParameter(Map<String, SubmodelElement> wrappedParamMap, IOperationVariable parameter) {
		ISubmodelElement parameterElement = parameter.getValue();
		String parameterName = parameterElement.getIdShort();
		SubmodelElement passedParameterElement = wrappedParamMap.get(parameterName);
		return passedParameterElement.getValue();
	}

	public static Object unwrapResult(SubmodelElement[] result) {
		if (result != null && result.length > 0) {
			return result[0].getValue();
		} else {
			return null;
		}
	}

	public static Map<String, SubmodelElement> convertSubmodelElementArrayToMap(SubmodelElement[] elems) {
		Map<String, SubmodelElement> seMap = new HashMap<>();
		for (SubmodelElement se : elems) {
			seMap.put(se.getIdShort(), se);
		}
		return seMap;
	}
}
