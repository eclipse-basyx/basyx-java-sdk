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
		
		VABElementProxy elementProxy = SubmodelElementTestHelper.createElementProxy(MLP);

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
