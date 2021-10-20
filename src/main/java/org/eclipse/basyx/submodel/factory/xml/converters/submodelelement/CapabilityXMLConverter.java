/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.factory.xml.converters.submodelelement;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ICapability;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.Capability;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses &lt;aas:capability&gt; and builds the Capability object form it <br>
 * Builds &lt;aas:capability&gt; from a given Capability object
 * 
 * @author espen, fischer
 *
 */
public class CapabilityXMLConverter extends SubmodelElementXMLConverter {

	public static final String CAPABILITY = "aas:capability";

	/**
	 * Parses a Map containing the content of XML tag &lt;aas:capability&gt;
	 * 
	 * @param xmlObject
	 *            the Map with the content of XML tag &lt;aas:capability&gt;
	 * @return the parsed Capability
	 */
	public static Capability parseCapability(Map<String, Object> xmlObject) {
		Capability capabilityMap = new Capability();
		populateSubmodelElement(xmlObject, capabilityMap);
		return capabilityMap;
	}

	/**
	 * Builds the &lt;aas:capability&gt; XML tag for a Capability
	 * 
	 * @param document
	 *            the XML document
	 * @param capability
	 *            the ICapability to build the XML for
	 * @return the &lt;aas:capability&gt; XML tag for the given Capability
	 */
	public static Element buildCapability(Document document, ICapability capability) {
		Element xmlCapabilityRoot = document.createElement(CAPABILITY);

		populateSubmodelElement(document, xmlCapabilityRoot, capability);

		return xmlCapabilityRoot;
	}
}
