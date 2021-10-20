/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.vab.factory.xml;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.basyx.vab.factory.xml.VABXmlProviderFactory;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.junit.Test;

public class TestVABXmlProviderFactory {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testResources() throws Exception {

		VABXmlProviderFactory factory = new VABXmlProviderFactory();
		VABMapProvider provider = factory.createVABElements(TestXmlParser.xmlTestContent);

		Map<String, String> map;

		assertEquals(provider.getValue("tags/name/"), TestXmlParser.SOME_NAME);
		assertEquals(provider.getValue("tags/value/"), TestXmlParser.VALUEV);
		assertEquals(provider.getValue("tags/nestedTags/attrnt"), TestXmlParser.ATTRNT_1_VAL);
		Iterator<Object> iterator = ((Collection) provider.getValue("tags/nestedTags/nestedTag/"))
				.iterator();
		assertEquals(TestXmlParser.NESTED_TAG_1, iterator.next());
		assertEquals(TestXmlParser.NESTED_TAG_2, iterator.next());
		assertEquals(TestXmlParser.NESTED_TAG_3, iterator.next());

		iterator = ((Collection) provider
				.getValue("tags/deeplyNestedTagParent/deeplyNestedTagsChild/deeplyNestedTagsLeaf/"))
						.iterator();
		map = (Map<String, String>) (iterator.next());
		assertEquals(map.get(TestXmlParser.TEXT), TestXmlParser.DN_TEXT_1);
		assertEquals(map.get(TestXmlParser.ATTRDN), TestXmlParser.ATTR_1_VAL);

		map = (Map<String, String>) iterator.next(); // get 2nd entry in collecion
		assertEquals(map.get(TestXmlParser.TEXT), TestXmlParser.DN_TEXT_2);
		assertEquals(map.get(TestXmlParser.ATTRDN), TestXmlParser.ATTR_2_VAL);

		iterator = ((Collection) provider.getValue("tags/someTag/")).iterator();
		assertEquals(TestXmlParser.SOME_TEXT_1, iterator.next());
		assertEquals(TestXmlParser.SOME_TEXT_2, iterator.next());
		assertEquals(TestXmlParser.SOME_TEXT_3, iterator.next());

	}

}
