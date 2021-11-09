/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.exception.provider;

import java.util.Collection;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;

public class WrongNumberOfParametersException extends MalformedRequestException {
	/**
	 * Version information for serialized instances
	 */
	private static final long serialVersionUID = 1L;

	public WrongNumberOfParametersException(String operationIdShort, Collection<IOperationVariable> expected, Object... actual) {
		super(constructErrorMessage(operationIdShort, expected, actual.length));
	}

	public WrongNumberOfParametersException(String operationIdShort, Collection<IOperationVariable> expected, int actualSize) {
		super(constructErrorMessage(operationIdShort, expected, actualSize));
	}

	private static String constructErrorMessage(String operationIdShort, Collection<IOperationVariable> expected, int actualSize) {
		return "Operation with idShort " + operationIdShort + " was called using the wrong number of parameters. Expected size: " + expected.size() + ", actual size: " + actualSize;
	}
}
