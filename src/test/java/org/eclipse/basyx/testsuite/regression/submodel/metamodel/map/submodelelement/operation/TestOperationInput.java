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

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;

/**
 * Tests inputs of {@link Operation} for their correctness
 * 
 * @author espen, fischer
 *
 */
public class TestOperationInput extends TestOperationInputSuite {

	@Override
	protected IOperation prepareOperation(Operation operation) {
		return operation;
	}
}
