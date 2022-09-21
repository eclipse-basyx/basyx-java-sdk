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
package org.eclipse.basyx.aas.factory.xml;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.SubmodelElementXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.operation.OperationXMLConverter;

/**
 * This class contains workarounds needed to be able to load .xml files produced
 * by the AASXPackageExplorer in BaSyx.
 * 
 * @author conradi
 *
 */
public class AASXPackageExplorerCompatibilityHandler {

	/**
	 * This function makes sure the operation vars are in the correct map.
	 * AASXPackageExplorer uses multiple e.g. &lt;aas:inputVariable&gt; tags instead
	 * of a single &lt;aas:inputVariable&gt; with multiple
	 * &lt;aas:operationVariable&gt; tags within
	 * 
	 * @param xmlObject
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> prepareOperationVariableMap(Object xmlObject) {
		if (xmlObject == null) {
			return null;
		} else if (isValidMap(xmlObject)) {
			return (Map<String, Object>) xmlObject;
		} else if (xmlObject instanceof List) {
			return handleInvalidVariableList((List<?>) xmlObject);
		} else if (xmlObject instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) xmlObject;
			return map.isEmpty() ? map : handleInvalidVariableMap(map);
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
		Map<String, Object> correctMap = new LinkedHashMap<>();
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
		Map<String, Object> correctMap = new LinkedHashMap<>();
		correctMap.put(OperationXMLConverter.OPERATION_VARIABLE, xmlObject);
		return correctMap;
	}

	private static RuntimeException createUnexpectedObjectRuntimeException(Object xmlObject) {
		return new RuntimeException("Unexpected object: " + xmlObject);
	}

	/**
	 * The AASXPackageExplorer uses "Template" instead of "Type" AssetKind This
	 * converts "Template" to "Type"
	 * 
	 * @param assetKind
	 * @return
	 */
	public static String convertAssetKind(String assetKind) {
		if (isTemplateAssetKind(assetKind)) {
			assetKind = AssetKind.TYPE.toString();
		}

		return assetKind;
	}

	private static boolean isTemplateAssetKind(String assetKind) {
		return assetKind.toLowerCase().equals("template");
	}

}
