/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.enumhelper;

/**
 * Enums with this interface hold a custom string literal that is used during e.g. XML serialization.
 * You may use the {@link StandardizedLiteralEnumHelper} to map a custom string literal to an enum. 
 * 
 * @author alexandergordt
 */
public interface StandardizedLiteralEnum {

	/**
	 * Custom string for use in case sensitive environments or during serialization.
	 * 
	 * @return Case sensitive string
	 */
	String getStandardizedLiteral();
}
