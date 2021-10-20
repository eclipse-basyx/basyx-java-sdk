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

import com.google.common.base.Strings;

/**
 * Helper class to map custom string literals to StandardizedLiteralEnums.
 *  
 * @author alexandergordt
 */
public class StandardizedLiteralEnumHelper {

	/**
	 * Maps string literals of {@link StandardizedLiteralEnum}s to enum constants.
	 * The string literals read via getStandardizedLiteral() from the enum constants.
	 * 
	 * @param <T> Enum class implementing StandardizedLiteralEnum
	 * @param clazz Target enum with matching custom string literal
	 * @param literal The literal as contained in e.g. XML schema
	 * @return Enum constant
	 * @throws IllegalArgumentException when string literal is not found in enum.
	 */
	public static <T extends StandardizedLiteralEnum> T fromLiteral(Class<T> clazz, String literal) {
		if (Strings.isNullOrEmpty(literal)) {
			return null;
		}
		
		T[] enumConstants = clazz.getEnumConstants();
		for(T constant : enumConstants) {
			if(constant.getStandardizedLiteral().equals(literal)) {
				return constant;
			}
		}
		throw new IllegalArgumentException("The literal '" + literal + "' is not contained in enum " + clazz.getName());
	}
}
