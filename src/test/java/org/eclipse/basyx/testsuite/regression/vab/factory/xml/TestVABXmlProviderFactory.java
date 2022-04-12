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
