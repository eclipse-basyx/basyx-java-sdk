/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.enumhelper;

import static org.junit.Assert.assertTrue;

import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnum;
import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnumHelper;
import org.junit.Test;

public class StandardizedLiteralEnumHelperTests {

	public static enum SampleEnum implements StandardizedLiteralEnum {
		LITERAL1("Literal1"), LITERAL2("Literal2");
		
		private final String standardizedLiteral;
		
		private SampleEnum(String standardizedLiteral) {
			this.standardizedLiteral = standardizedLiteral;
		}

		@Override
		public String getStandardizedLiteral() {
			return standardizedLiteral;
		}
		
		public static SampleEnum fromString(String literal) {
			return StandardizedLiteralEnumHelper.fromLiteral(SampleEnum.class, literal);
		}
	}
	
	@Test
	public void testGetEnumFromLiteral() {
		assertTrue(SampleEnum.LITERAL1 == SampleEnum.fromString("Literal1"));
	}
}
