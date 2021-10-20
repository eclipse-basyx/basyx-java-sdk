/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.dataspecification;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IEmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IValueReferencePair;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.enums.DataTypeIEC61360;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.enums.LevelType;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.DataSpecificationIEC61360;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.DataSpecificationIEC61360Content;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.ValueReferencePair;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Test;

/**
 * Tests constructor and getter of {@link DataSpecification} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestEmbeddedDataSpecification {
	private static final LangStrings SHORT_NAME = new LangStrings("DE", "testShortName");
	private static final String UNIT = "testUnit";
	private static final String SYMBOL = "testSymbol";
	private static final DataTypeIEC61360 DATA_TYPE = DataTypeIEC61360.REAL_MEASURE;
	private static final String VALUE_FORMAT = "testValueFormat";
	private static final LangStrings PREFERRED_NAME  = new LangStrings("DE", "testName");
	private static final String SOURCE_OF_DEFINITION = "testSource";
	private static final LangStrings DEFINITION = new LangStrings("DE", "testDefinition");
	private static final KeyElements KEY_ELEMENTS = KeyElements.ASSET;
	private static final boolean IS_LOCAL = false;
	private static final String VALUE = "testValue";
	private static final IdentifierType ID_TYPE = IdentifierType.CUSTOM;
	private static final Identifier IDENTIFIER = new Identifier(ID_TYPE, VALUE);
	private static final Reference UNIT_ID = new Reference(IDENTIFIER, KEY_ELEMENTS, IS_LOCAL);
	private static final Reference VALUE_ID = new Reference(IDENTIFIER, KEY_ELEMENTS, IS_LOCAL);
	private static final IValueReferencePair VALUEREFPAIR = new ValueReferencePair(VALUE, VALUE_ID);
	private static final LevelType LEVELTYPE = LevelType.TYP;
	
	@Test
	public void testConstructor() {
		DataSpecificationIEC61360Content content = new DataSpecificationIEC61360Content(PREFERRED_NAME, SHORT_NAME,
				UNIT, UNIT_ID, SOURCE_OF_DEFINITION, SYMBOL, DATA_TYPE, DEFINITION, VALUE_FORMAT,
				Arrays.asList(VALUEREFPAIR), VALUE, VALUE_ID, LEVELTYPE);
		IEmbeddedDataSpecification specification = new DataSpecificationIEC61360(content);
		assertEquals(content, specification.getContent());
	}
}
