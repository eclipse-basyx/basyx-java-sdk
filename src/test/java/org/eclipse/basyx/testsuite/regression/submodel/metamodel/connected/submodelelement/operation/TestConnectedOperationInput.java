/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement.operation;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.operation.ConnectedOperation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.OperationProvider;
import org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement.operation.TestOperationInputSuite;
import org.eclipse.basyx.testsuite.regression.vab.manager.VABConnectionManagerStub;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.eclipse.basyx.vab.support.TypeDestroyer;

/**
 * Tests inputs of {@link ConnectedOperation} for their correctness
 *
 *
 * @author espen, fischer
 *
 */
public class TestConnectedOperationInput extends TestOperationInputSuite {

	@Override
	protected IOperation prepareOperation(Operation operation) {
		Map<String, Object> destroyType = TypeDestroyer.destroyType(operation);
		// Create a dummy connection manager containing the created Operation map
		VABConnectionManager manager = new VABConnectionManagerStub(new OperationProvider(new VABMapProvider(destroyType)));

		// Create the ConnectedOperation based on the manager stub
		return new ConnectedOperation(manager.connectToVABElement(""));
	}
}
