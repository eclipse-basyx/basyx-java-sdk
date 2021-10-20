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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.basyx.vab.factory.xml.XmlParser;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TestXmlParser {

	private Map<String, Object> rootObj = new HashMap<>();

	private static final String TAGS = "tags";
	private static final String ATTR_1 = "attr_1";
	private static final String ATTR_2 = "attr_2";
	private static final String NAME = "name";
	private static final String VALUE = "value";
	private static final String NESTED_TAG = "nestedTag";
	private static final String NESTED_TAGS = "nestedTags";
	private static final String DEEPLY_NESTED_PARENT = "deeplyNestedTagParent";
	private static final String DEEPLY_NESTED_TAGS_CHILD = "deeplyNestedTagsChild";
	private static final String DEEPLY_NESTED_LEAF = "deeplyNestedTagsLeaf";
	private static final String SOME_TAG = "someTag";
	static final String TEXT = "#text";
	static final String SOME_NAME = "Some name";
	static final String VALUEV = "0.34242";
	static final String ATTRNT = "attrnt";
	static final String ATTRDN = "attrdn";
	static final String DN_TEXT_1 = "deeply nested text 1";
	static final String DN_TEXT_2 = "deeply nested text 2";
	static final String ATTR_1_VAL = "some attr 1";
	static final String ATTR_2_VAL = "some attr 2";
	static final String ATTRNT_1_VAL = "1232";
	static final String NESTED_TAG_1 = "nested text 1";
	static final String NESTED_TAG_2 = "nested text 2";
	static final String NESTED_TAG_3 = "nested text 3";
	static final String SOME_TEXT_1 = "Some text 1";
	static final String SOME_TEXT_2 = "Some text 2";
	static final String SOME_TEXT_3 = "Some text 3";
	static final String xmlTestContent = "<tags attr_1=\"some attr 1\" attr_2=\"some attr 2\">			                     "
			+ "	<name>Some name</name>                                                    	                     "
			+ "	<value>0.34242</value>                                                    	                     "
			+ "	<nestedTags attrnt=\"1232\">                                                                     "
			+ "		<nestedTag>nested text 1</nestedTag>                                                         "
			+ "		<nestedTag>nested text 2</nestedTag>                                                         "
			+ "		<nestedTag>nested text 3</nestedTag>                                                         "
			+ "	</nestedTags>                                                             	                     "
			+ "	<deeplyNestedTagParent>                                                   	                     "
			+ "		<deeplyNestedTagsChild>                                               	                     "
			+ "			<deeplyNestedTagsLeaf attrdn=\"some attr 1\">deeply nested text 1</deeplyNestedTagsLeaf> "
			+ "			<deeplyNestedTagsLeaf attrdn=\"some attr 2\">deeply nested text 2</deeplyNestedTagsLeaf> "
			+ "		</deeplyNestedTagsChild>                                              	                     "
			+ "	</deeplyNestedTagParent>                                                  	                     "
			+ "	<someTag>Some text 1</someTag>                                            	                     "
			+ "	<someTag>Some text 2</someTag>                                            	                     "
			+ "	<someTag>Some text 3</someTag>                                            	                     "
			+ "</tags> ";

	@Before
	public void TestBuildXmlMap() throws Exception {
		rootObj.putAll(XmlParser.buildXmlMap(xmlTestContent));
	}

	@SuppressWarnings("unchecked")
	@Test
	/**
	 * Checks whether the respective keys are present in the nested Map.
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void testContainsAllKeys() throws ParserConfigurationException, SAXException, IOException {

		Map<String, Object> tags = new HashMap<>();
		Map<String, Object> nestedTags = new HashMap<>();
		Map<String, Object> deeplyNestedTagParent = new HashMap<>();
		Map<String, Object> deeplyNestedTagsChild = new HashMap<>();
		List<Map<String, String>> deeplyNestedTagsLeaf = new ArrayList<>();

		assertTrue(rootObj.containsKey(TAGS));
		assertTrue(rootObj.containsKey(ATTR_1));
		assertTrue(rootObj.containsKey(ATTR_2));

		tags = (Map<String, Object>) rootObj.get(TAGS);
		assertTrue(tags.containsKey(NAME));
		assertTrue(tags.containsKey(VALUE));
		assertTrue(tags.containsKey(NESTED_TAGS));
		assertTrue(tags.containsKey(DEEPLY_NESTED_PARENT));
		assertTrue(tags.containsKey(SOME_TAG));

		nestedTags = (Map<String, Object>) tags.get(NESTED_TAGS);
		assertTrue(nestedTags.containsKey(ATTRNT));
		assertTrue(nestedTags.containsKey(NESTED_TAG));

		deeplyNestedTagParent = (Map<String, Object>) tags.get(DEEPLY_NESTED_PARENT);
		assertTrue(deeplyNestedTagParent.containsKey(DEEPLY_NESTED_TAGS_CHILD));

		deeplyNestedTagsChild = (Map<String, Object>) deeplyNestedTagParent.get(DEEPLY_NESTED_TAGS_CHILD);
		assertTrue(deeplyNestedTagsChild.containsKey(DEEPLY_NESTED_LEAF));

		deeplyNestedTagsLeaf = (List<Map<String, String>>) deeplyNestedTagsChild.get(DEEPLY_NESTED_LEAF);

		assertTrue(deeplyNestedTagsLeaf.get(0).containsKey(TEXT));
		assertTrue(deeplyNestedTagsLeaf.get(0).containsKey(ATTRDN));
		assertTrue(deeplyNestedTagsLeaf.get(1).containsKey(TEXT));
		assertTrue(deeplyNestedTagsLeaf.get(1).containsKey(ATTRDN));
	}

	@Test
	/**
	 * Checks elements of the root node
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void testRootNode() throws ParserConfigurationException, SAXException, IOException {

		// Check Elements
		assertNotEquals(null, rootObj.get(TAGS));
		// Check attributes
		assertNotEquals(null, rootObj.get(ATTR_1));
		assertNotEquals(null, rootObj.get(ATTR_2));
		// Check the objects are created of desired types
		assertEquals(ATTR_1_VAL, rootObj.get(ATTR_1));
		assertEquals(ATTR_2_VAL, rootObj.get(ATTR_2));

	}

	@SuppressWarnings("unchecked")
	@Test
	/**
	 * Checks the elements of "nestedTags" tag.
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void testNestedElements() throws ParserConfigurationException, SAXException, IOException {

		Map<String, Object> tags = new HashMap<>();
		Map<String, Object> nestedTags = new HashMap<>();
		List<String> nestedTag = new ArrayList<>();

		assertNotEquals(null, rootObj.get(TAGS));
		tags = (Map<String, Object>) rootObj.get(TAGS);
		assertNotEquals(null, tags.get(NESTED_TAGS));
		nestedTags = (Map<String, Object>) tags.get(NESTED_TAGS);
		// Attributes
		assertEquals(ATTRNT_1_VAL, nestedTags.get(ATTRNT));
		// Multiple tags are stored as list
		assertNotEquals(null, tags.get(NESTED_TAGS));
		nestedTag = (List<String>) nestedTags.get(NESTED_TAG);
		assertEquals(NESTED_TAG_1, nestedTag.get(0));
		assertEquals(NESTED_TAG_2, nestedTag.get(1));
		assertEquals(NESTED_TAG_3, nestedTag.get(2));
	}

	@SuppressWarnings("unchecked")
	@Test
	/**
	 * Checks the elements of "deeplyNestedTagParent" tag. This covers case when
	 * tags are nested inside tags.
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void testDeeplyNestedElements() throws ParserConfigurationException, SAXException, IOException {

		Map<String, Object> tags = new HashMap<>();
		Map<String, Object> deeplyNestedTagParent = new HashMap<>();
		Map<String, Object> deeplyNestedTagsChild = new HashMap<>();
		List<Map<String, String>> deeplyNestedTagsLeaf = new ArrayList<>();

		assertNotEquals(null, rootObj.get(TAGS));
		tags = (Map<String, Object>) rootObj.get(TAGS);

		assertNotEquals(null, tags.get(DEEPLY_NESTED_PARENT));
		deeplyNestedTagParent = (Map<String, Object>) tags.get(DEEPLY_NESTED_PARENT);
		deeplyNestedTagsChild = (Map<String, Object>) deeplyNestedTagParent.get(DEEPLY_NESTED_TAGS_CHILD);
		deeplyNestedTagsLeaf = (List<Map<String, String>>) deeplyNestedTagsChild.get(DEEPLY_NESTED_LEAF);

		// Check if the desired objects are created
		assertNotEquals(null, deeplyNestedTagsLeaf.get(0).get(TEXT));
		assertNotEquals(null, deeplyNestedTagsLeaf.get(0).get(ATTRDN));
		assertNotEquals(null, deeplyNestedTagsLeaf.get(1).get(TEXT));
		assertNotEquals(null, deeplyNestedTagsLeaf.get(1).get(ATTRDN));
		// Check the objects are created of desired types
		assertTrue(rootObj instanceof HashMap);
		assertTrue(rootObj.get(TAGS) instanceof HashMap);
		assertTrue(deeplyNestedTagsChild.get(DEEPLY_NESTED_LEAF) instanceof ArrayList);
		assertTrue(deeplyNestedTagParent.get(DEEPLY_NESTED_TAGS_CHILD) instanceof HashMap);
		assertTrue(tags.get(DEEPLY_NESTED_PARENT) instanceof HashMap);
		assertTrue(tags instanceof HashMap);
		// Check the texts
		assertEquals(DN_TEXT_1, deeplyNestedTagsLeaf.get(0).get(TEXT));
		assertEquals(ATTR_1_VAL, deeplyNestedTagsLeaf.get(0).get(ATTRDN));
		assertEquals(DN_TEXT_2, deeplyNestedTagsLeaf.get(1).get(TEXT));
		assertEquals(ATTR_2_VAL, deeplyNestedTagsLeaf.get(1).get(ATTRDN));

	}

	@SuppressWarnings("unchecked")
	@Test
	/**
	 * Checks whether Parent node(child of root node) multiple tags with the same
	 * node. Tag "someTag" is checked.
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void testTextMulitpleTextNodeInParent() throws ParserConfigurationException, SAXException, IOException {

		Map<String, Object> tags = new HashMap<>();
		List<String> someTag = new ArrayList<>();

		tags = (Map<String, Object>) rootObj.get(TAGS);
		someTag = (List<String>) tags.get(SOME_TAG);
		// Check if the desired objects are created
		assertNotEquals(null, rootObj);
		assertNotEquals(null, tags);
		assertNotEquals(null, someTag);
		// Check the objects are created of desired types
		assertTrue(tags.get(SOME_TAG) instanceof ArrayList);
		assertEquals(SOME_TEXT_1, someTag.get(0));
		assertEquals(SOME_TEXT_2, someTag.get(1));
		assertEquals(SOME_TEXT_3, someTag.get(2));
	}
}
