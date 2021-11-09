/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/
package org.eclipse.basyx.aas.factory.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.SubmodelElementXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.operation.OperationXMLConverter;

/**
 * This class contains workarounds needed to be able to load .xml
 * files produced by the AASXPackageExplorer in BaSyx.
 * 
 * @author conradi
 *
 */
public class AASXPackageExplorerCompatibilityHandler {

	/**
	 * This function makes sure the operation vars are in the correct map.
	 * AASXPackageExplorer uses multiple e.g. &lt;aas:inputVariable&gt; tags instead
	 * of a single &lt;aas:inputVariable&gt; with multiple &lt;aas:operationVariable&gt; tags within
	 * 
	 * @param xmlObject
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> prepareOperationVariableMap(Object xmlObject) {
		if(xmlObject == null) {
			return null;
		} else if (isValidMap(xmlObject)) {
			return (Map<String, Object>) xmlObject;
		} else if (xmlObject instanceof List) {
			return handleInvalidVariableList((List<?>) xmlObject);
		} else if (xmlObject instanceof Map) {
			return handleInvalidVariableMap((Map<String, Object>) xmlObject);
		} else {
			throw createUnexpectedObjectRuntimeException(xmlObject);
		}

	}

	private static Map<String, Object> handleInvalidVariableMap(Map<String, Object> map) throws RuntimeException {
		if (hasValueTag(map)) {
			return insertOperationVariableTag(map);
		} else {
			throw createUnexpectedObjectRuntimeException(map);
		}
	}

	private static boolean hasValueTag(Map<String, Object> map) {
		return map.containsKey(SubmodelElementXMLConverter.VALUE);
	}

	private static Map<String, Object> insertOperationVariableTag(Map<String, Object> map) {
		// This Map contains directly the aas:value key and one variable
		// e.g. <aas:inputVariable><aas:value> was used instead of
		// <aas:inputVariable><aas:value><aas:operationVariable>
		Map<String, Object> correctMap = new HashMap<>();
		correctMap.put(OperationXMLConverter.OPERATION_VARIABLE, map);
		return correctMap;
	}

	@SuppressWarnings("unchecked")
	private static boolean isValidMap(Object xmlObject) {
		if (!(xmlObject instanceof Map)) {
			return false;
		}

		Map<String, Object> map = (Map<String, Object>) xmlObject;
		return map.containsKey(OperationXMLConverter.OPERATION_VARIABLE);
	}

	private static Map<String, Object> handleInvalidVariableList(List<?> xmlObject) {
		// If object is a List
		// Multiple <aas:inputVariable> was used instead of
		// <aas:inputVariable> and multiple <aas:operationVariable> within that
		// Wrap List in Map with aas:operationVariable as key
		Map<String, Object> correctMap = new HashMap<>();
		correctMap.put(OperationXMLConverter.OPERATION_VARIABLE, xmlObject);
		return correctMap;
	}

	private static RuntimeException createUnexpectedObjectRuntimeException(Object xmlObject) {
		return new RuntimeException("Unexpected object: " + xmlObject);
	}
	
	
	/**
	 * The AASXPackageExplorer uses "Template" instead of "Type" AssetKind
	 * This converts "Template" to "Type"
	 * 
	 * @param assetKind
	 * @return
	 */
	public static String convertAssetKind(String assetKind) {
		if(isTemplateAssetKind(assetKind)) {
			assetKind = AssetKind.TYPE.toString();
		}

		return assetKind;
	}

	private static boolean isTemplateAssetKind(String assetKind) {
		return assetKind.toLowerCase().equals("template");
	}
	
}
