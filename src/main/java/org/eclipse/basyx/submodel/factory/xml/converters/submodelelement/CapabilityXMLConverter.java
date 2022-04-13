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
