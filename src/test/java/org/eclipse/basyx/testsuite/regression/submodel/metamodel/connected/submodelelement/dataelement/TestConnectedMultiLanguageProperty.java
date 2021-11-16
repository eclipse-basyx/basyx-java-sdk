/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement.dataelement;

import static org.junit.Assert.assertEquals;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedMultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement.SubmodelElementTestHelper;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if a ConnectedMultiLanguageProperty can be created and used correctly
 * 
 * @author conradi
 *
 */
public class TestConnectedMultiLanguageProperty {
	
	ConnectedMultiLanguageProperty connectedMLP;
	MultiLanguageProperty MLP;
	
	@Before
	public void build() {
		LangStrings langStrings = new LangStrings("de", "TestBeschreibung");
		
		MLP = new MultiLanguageProperty("idShort");
		
		MLP.setValue(langStrings);
		
		VABElementProxy elementProxy = SubmodelElementTestHelper.provideElementProxy(MLP);

		connectedMLP = new ConnectedMultiLanguageProperty(elementProxy);
	}
	
	/**
	 * Tests if getValue() returns the correct value
	 */
	@Test
	public void testGetValue() {
		assertEquals(MLP.getValue().get("de"), connectedMLP.getValue().get("de"));
	}
	
	/**
	 * Tests if getValueId() returns the correct value
	 */
	@Test
	public void testGetValueId() {
		assertEquals(MLP.getValueId(), connectedMLP.getValueId());
	}
	
	@Test
	public void setValueUpdatesValueCorrectly() {
		triggerCachingOfSubmodelElement();
		
		LangStrings expected = new LangStrings("en", "English Language");	

		connectedMLP.setValue(expected);
		
		assertEquals(expected, connectedMLP.getValue());
	}

	private void triggerCachingOfSubmodelElement() {
		connectedMLP.getElem();
	}
}
