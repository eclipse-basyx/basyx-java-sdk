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
package org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.factory.xml.AASXPackageExplorerCompatibilityHandler;
import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.SubmodelElementXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses &lt;aas:operation&gt; and builds the Operation object from it <br>
 * Builds &lt;aas:operation&gt; from a given Operation object
 * 
 * @author conradi
 *
 */
public class OperationXMLConverter extends SubmodelElementXMLConverter {

	public static final String OPERATION = "aas:operation";
	public static final String INPUT_VARIABLE = "aas:inputVariable";
	public static final String OUTPUT_VARIABLE = "aas:outputVariable";
	public static final String INOUTPUT_VARIABLE = "aas:inoutputVariable";
	public static final String OPERATION_VARIABLE = "aas:operationVariable";

	/**
	 * Parses a Map containing the content of XML tag &lt;aas:operation&gt;
	 * 
	 * @param xmlObject
	 *            the Map with the content of XML tag &lt;aas:operation&gt;
	 * @return the parsed Operation
	 */
	public static Operation parseOperation(Map<String, Object> xmlObject) {
		List<OperationVariable> inList = new ArrayList<>();
		List<OperationVariable> outList = new ArrayList<>();
		List<OperationVariable> inoutList = new ArrayList<>();

		Map<String, Object> inObj = AASXPackageExplorerCompatibilityHandler.prepareOperationVariableMap(xmlObject.get(INPUT_VARIABLE));
		if (inObj != null) {
			inList = getOperationVariables(inObj);
		}

		Map<String, Object> outObj = AASXPackageExplorerCompatibilityHandler.prepareOperationVariableMap(xmlObject.get(OUTPUT_VARIABLE));
		if (outObj != null) {
			outList = getOperationVariables(outObj);
		}

		Map<String, Object> inoutObj = AASXPackageExplorerCompatibilityHandler.prepareOperationVariableMap(xmlObject.get(INOUTPUT_VARIABLE));
		if (inoutObj != null) {
			inoutList = getOperationVariables(inoutObj);
		}

		Operation operation = new Operation(inList, outList, inoutList, null);
		populateSubmodelElement(xmlObject, operation);
		return operation;
	}

	/**
	 * Parses a Map containing the content of XML tag &lt;aas:operationVariable&gt;
	 * 
	 * @param xmlObject
	 *            the Map with the content of XML tag &lt;aas:operationVariable&gt;
	 * @return the parsed OperationVariable
	 */
	@SuppressWarnings("unchecked")
	private static OperationVariable parseOperationVariable(Map<String, Object> xmlObject) {
		SubmodelElement submodelElement = getSubmodelElement((Map<String, Object>) xmlObject.get(VALUE));
		OperationVariable operationVariable = new OperationVariable(submodelElement);
		return operationVariable;
	}

	/**
	 * Builds the &lt;aas:operation&gt; XML tag for an Operation
	 * 
	 * @param document
	 *            the XML document
	 * @param operation
	 *            the IOperation to build the XML for
	 * @return the &lt;aas:operation&gt; XML tag for the given Operation
	 */
	public static Element buildOperation(Document document, IOperation operation) {
		Element operationRoot = document.createElement(OPERATION);

		populateSubmodelElement(document, operationRoot, operation);

		Collection<IOperationVariable> inout = operation.getInOutputVariables();
		if (inout != null) {
			Element valueRoot = document.createElement(INOUTPUT_VARIABLE);
			operationRoot.appendChild(valueRoot);
			for (IOperationVariable operationVariable : inout) {
				valueRoot.appendChild(buildOperationVariable(document, operationVariable));
			}
		}

		Collection<IOperationVariable> in = operation.getInputVariables();
		if (in != null) {
			Element valueRoot = document.createElement(INPUT_VARIABLE);
			operationRoot.appendChild(valueRoot);
			for (IOperationVariable operationVariable : in) {
				valueRoot.appendChild(buildOperationVariable(document, operationVariable));
			}
		}

		Collection<IOperationVariable> out = operation.getOutputVariables();
		if (out != null) {
			Element valueRoot = document.createElement(OUTPUT_VARIABLE);
			operationRoot.appendChild(valueRoot);
			for (IOperationVariable operationVariable : out) {
				valueRoot.appendChild(buildOperationVariable(document, operationVariable));
			}
		}

		return operationRoot;
	}

	/**
	 * Builds the &lt;aas:operationVariable&gt; XML tag for an OperationVariable
	 * 
	 * @param document
	 *            the XML document
	 * @param operationVariable
	 *            the IOperationVariable to build the XML for
	 * @return the &lt;aas:operationVariable&gt; XML tag for the given
	 *         OperationVariable
	 */
	private static Element buildOperationVariable(Document document, IOperationVariable operationVariable) {
		Element operationVariableRoot = document.createElement(OPERATION_VARIABLE);

		ISubmodelElement value = operationVariable.getValue();

		if (value != null) {
			Element valueRoot = document.createElement(VALUE);
			valueRoot.appendChild(buildSubmodelElement(document, value));
			operationVariableRoot.appendChild(valueRoot);
		}

		return operationVariableRoot;
	}

	/**
	 * Gets Operation Variables In/Out/InOut from variable map
	 * 
	 * @param varObj
	 *            map containing variables
	 * @return List of OperationVariable
	 */
	private static List<OperationVariable> getOperationVariables(Map<String, Object> varObj) {
		List<OperationVariable> variableList = new ArrayList<>();
		Object operationVarObj = varObj.get(OPERATION_VARIABLE);

		List<Map<String, Object>> xmlOpVars = XMLHelper.getList(operationVarObj);

		for (Map<String, Object> map : xmlOpVars) {
			variableList.add(parseOperationVariable(map));
		}

		return variableList;
	}
}
