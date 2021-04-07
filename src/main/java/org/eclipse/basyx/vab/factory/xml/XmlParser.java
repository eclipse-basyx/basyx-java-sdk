/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.factory.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/***
 * A Generic XML Parser which transforms the given XML data to nested
 * {@literal Map<String, Object>}.<br>
 * <br>
 * 
 * Examples:<br>
 * <br>
 * -Text Element <br>
 * <code>{@literal <a>v</a> => {a = v}}<br></code><br>
 * 
 * -Nested Element <br>
 * <code> {@literal <a><b>v</b></a> => {a={b=v}}}<br></code><br>
 * 
 * -Text Node with Attributes<br>
 * <code> {@literal <a b="v1" c="v2">v3</a> => {ele :{#text:v3,b:v1, c:v2}}}</code> <br>
 * <br>
 * 
 * -Multiple Text Nodes<br>
 * <code>{@literal <a><b>v1</b><b>v2</b></a> => {a={b=[v1,v2]}}}<br></code><br>
 * 
 * -Multiple Text Nodes with Attributes <br>
 * <code> {@literal <a><b d="v3" e="v5">v1</b><b d="v4" e="v6">v2</b></a> => {a={b=[{#text=v1, d=v3, e=v5}, {#text=v2, d=v4, e=v6}]}}}</code><br>
 * <br>
 * 
 * -Element Node Attributes<br>
 * <code> {@literal <a c="v1" d="v2"><b>v3</b></a> => {a={b=v3}, c=v1, d=v2}}</code><br>
 * <br>
 * 
 * @author kannoth
 *
 */
public class XmlParser {

	private static final String TEXT = "#text";

	/**
	 * Parses the XML string content and returns the nested HashMap
	 * 
	 * @param xmlContent - String content of the xml file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Map<String, Object> buildXmlMap(String xmlContent)
			throws ParserConfigurationException, SAXException, IOException {

		Map<String, Object> retMap = new HashMap<>();
		// Getting rid of the white spaces between the tags in order to avoid creation
		// of
		// unwanted nodes with no content. Applying the regex has no implications
		// on xml data content.
		xmlContent = xmlContent.replaceAll(">\\s*<", "><");
		// Parse the xml content
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setIgnoringElementContentWhitespace(true);
		dbFactory.setIgnoringComments(true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputSource iSrc = new InputSource(new StringReader(xmlContent));
		Document doc = dBuilder.parse(iSrc);
		doc.getDocumentElement().normalize();
		// Extract the root node
		Node rootNode = doc.getDocumentElement();
		// Create the root element of the Map to be created
		retMap.put(rootNode.getNodeName(), traverseDomTree(rootNode));
		// If the root element has got attributes, append then to the Map
		if (rootNode.hasAttributes()) {
			retMap = makeAttrMapForEleNode(retMap, rootNode);
		}
		return retMap;
	}

	/**
	 * Traverse through the given DOM Node and build nested map representing the DOM
	 * tree.
	 * 
	 * @param parentNode - Node from which the traversal should be initiated.
	 * @return
	 */
	private static Object traverseDomTree(Node parentNode) {

		Map<String, Object> retMap = new HashMap<>();
		Object leafNode = null;
		NodeList childNodes = parentNode.getChildNodes();

		for (Node node : iterableNodeMap(childNodes)) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				leafNode = handleElementNode(node);
			} else if (node.getNodeType() == Node.TEXT_NODE) {
				return handleTextNode(node);
			}
			updateNestedMap(retMap, node.getNodeName(), leafNode);
		}
		return retMap;
	}

	/**
	 * Handle ELEMENT node type.
	 * 
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Object handleElementNode(Node node) {

		Object retNode = traverseDomTree(node);
		if (node.hasAttributes()) {
			retNode = makeAttrMapForEleNode((Map<String, Object>) retNode, node);
		}
		return retNode;
	}

	/**
	 * Handle TEXT Node type.
	 * 
	 * @param node
	 * @return
	 */
	private static Object handleTextNode(Node node) {

		// If the text node has got attributes, collect them, pack
		// and append to the current hierarchy, otherwise just return
		// the text content.
		if (node.getParentNode().hasAttributes()) {
			return makeAttrMapForTxtNode(node.getParentNode());
		} else {
			return node.getTextContent();
		}
	}

	/**
	 * Creates a Map of with element's text and attributes combined
	 * 
	 * @param nodeAttrs - Text node of interest
	 * @return
	 */
	private static Map<String, Object> makeAttrMapForTxtNode(Node node) {
		// Get all attributes of the node
		NamedNodeMap nodeAttrs = node.getAttributes();
		// Create a special key for text of the element
		Map<String, Object> ret = new HashMap<>();
		ret.put(TEXT, node.getTextContent());
		// Collect all attributes of the Text node and append
		// to the text map contents.
		iterableNamedNodeMap(nodeAttrs).forEach(attr -> {
			ret.put(attr.getNodeName(), attr.getTextContent());
		});

		return ret;
	}

	/**
	 * Appends the attributes of the Element node to the given map. Useful for
	 * nested Element nodes.
	 * 
	 * @param eleMap - Nested map corresponding to an element node where the
	 *               attributes needs to be appended.
	 * @param node   - Element node of interest
	 * @return
	 */
	private static Map<String, Object> makeAttrMapForEleNode(Map<String, Object> eleMap, Node node) {

		Map<String, Object> ret = new HashMap<>();
		ret = eleMap;
		// Collect all attributes of the Element node and append to
		// nested map contents.
		for (Node attr : iterableNamedNodeMap(node.getAttributes())) {
			ret.put(attr.getNodeName(), attr.getTextContent().trim());
		}
		return ret;
	}

	/**
	 * Updates the nested Map until created with new element.
	 * 
	 * @param map      - Map which is created until
	 * @param nodeName - Node name of interest
	 * @param leafNode - Leaf object that to be added
	 */
	@SuppressWarnings("unchecked")
	private static void updateNestedMap(Map<String, Object> map, String nodeName, Object leafNode) {
		// If the map already contains the element, then change it a Map
		// pointing to a List. This happens for XML elements with multiple
		// tags with same name
		if (map.containsKey(nodeName)) {
			Object nestedObj = map.get(nodeName);
			if (nestedObj instanceof List) {
				((List<Object>) nestedObj).add(leafNode);
			} else {
				// Ignore the Text nodes to avoid creating List for all text
				// elements.
				if (!(nodeName.equals(TEXT))) {

					List<Object> nestedObjList = new ArrayList<>();
					nestedObjList.add(nestedObj);
					nestedObjList.add(leafNode);
					map.put(nodeName, nestedObjList);
				}
			}
		} else {
			map.put(nodeName, leafNode);
		}
	}

	/**
	 * Makes the given NodeList iterable
	 * 
	 * @param nodeList - NodeList to be made iterable
	 * @return
	 */
	private static Iterable<Node> iterableNodeMap(final NodeList nodeList) {

		return () -> new Iterator<Node>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < nodeList.getLength();
			}

			@Override
			public Node next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return nodeList.item(index++);
			}
		};
	}

	/**
	 * Makes the given NamedNodeMap iterable
	 * 
	 * @param namedNodeMap - NamedNodeMap to be made iterable
	 * @return
	 */
	private static Iterable<Node> iterableNamedNodeMap(final NamedNodeMap namedNodeMap) {

		return () -> new Iterator<Node>() {
			private int index;

			@Override
			public boolean hasNext() {
				return index < namedNodeMap.getLength();
			}

			@Override
			public Node next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return namedNodeMap.item(index++);
			}
		};
	}
}
