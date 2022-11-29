/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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

package org.eclipse.basyx.testsuite.regression.vab.coder.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedSubmodel;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedRange;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.ReferenceElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.RangeValue;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.coder.json.provider.JSONProvider;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.junit.Test;

public class TestSubmodelElementWithNullValue {

	@Test
	public void testPropertyWithNullValue() {
		Property prop = new Property("testprop", ValueType.String);
		prop.setValue(null);
		ConnectedSubmodel cSm = setupConnectedSubmodel();

		cSm.addSubmodelElement(prop);
		assertNull(cSm.getSubmodelElement(prop.getIdShort()).getValue());

		cSm.getSubmodelElement(prop.getIdShort()).setValue("test");
		assertEquals("test", cSm.getSubmodelElement(prop.getIdShort()).getValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRangeElementWIthNullValue() {
		ConnectedSubmodel cSm = setupConnectedSubmodel();
		Range range = new Range(ValueType.Int32, null, null);
		range.setIdShort("testRange");
		cSm.addSubmodelElement(range);
		RangeValue rangeValue = RangeValue.createAsFacade((Map<String, Object>) cSm.getSubmodelElement(range.getIdShort()).getValue());
		assertNull(rangeValue.getMin());

		((ConnectedRange) cSm.getSubmodelElement(range.getIdShort())).setValue(new RangeValue(1, 5));
		rangeValue = RangeValue.createAsFacade((Map<String, Object>) cSm.getSubmodelElement(range.getIdShort()).getValue());
		assertEquals(1, rangeValue.getMin());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReferenceElementWithNullValue() {
		ConnectedSubmodel cSm = setupConnectedSubmodel();
		ReferenceElement referenceElem = new ReferenceElement("testReferece");
		cSm.addSubmodelElement(referenceElem);
		assertNull(cSm.getSubmodelElement(referenceElem.getIdShort()).getValue());

		cSm.getSubmodelElement(referenceElem.getIdShort()).setValue(new Reference(new Key(KeyElements.ASSET, true, "testValue", IdentifierType.IRI)));
		Reference ref = Reference.createAsFacade((Map<String, Object>) cSm.getSubmodelElement(referenceElem.getIdShort()).getValue());
		assertEquals("testValue", ref.getKeys().get(0).getValue());
	}

	private ConnectedSubmodel setupConnectedSubmodel() {
		Submodel sm = new Submodel("sm1", new CustomId("sm"));
		ISubmodelAPI api = new VABSubmodelAPIFactory().create(sm);

		JSONProvider<IModelProvider> provider = new JSONProvider<IModelProvider>(new SubmodelProvider(api));

		JSONConnector connector = new JSONConnector(new IBasyxConnectorFacade<IModelProvider>(provider));

		return new ConnectedSubmodel(new VABElementProxy("/submodel", connector));
	}
}
