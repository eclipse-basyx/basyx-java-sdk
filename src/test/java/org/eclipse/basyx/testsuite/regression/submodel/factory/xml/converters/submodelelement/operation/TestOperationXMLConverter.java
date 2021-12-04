/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.testsuite.regression.submodel.factory.xml.converters.submodelelement.operation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.ReferableXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.operation.OperationXMLConverter;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.junit.Test;

/**
 * This class tests {@link OperationXMLConverter}
 * @author haque
 *
 */
public class TestOperationXMLConverter {

	@Test
	public void testParseOperationWithoutVariables() {
		String idShort = "operation_ID";
		Map<String, Object> operationMap = new LinkedHashMap<String, Object>();
		operationMap.put(ReferableXMLConverter.ID_SHORT, "operation_ID");
		Operation parsedOperation = OperationXMLConverter.parseOperation(operationMap);
		assertEquals(idShort, parsedOperation.getIdShort());
		assertEquals(new ArrayList<OperationVariable>(), parsedOperation.getInputVariables());
		assertEquals(new ArrayList<OperationVariable>(), parsedOperation.getOutputVariables());
		assertEquals(new ArrayList<OperationVariable>(), parsedOperation.getInOutputVariables());		
	}
}
