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
package org.eclipse.basyx.submodel.factory.xml.converters.qualifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.factory.xml.api.dataspecification.DataSpecificationIEC61360XMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.reference.ReferenceXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IDataSpecificationContent;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IDataSpecificationIEC61360Content;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IEmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IHasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.DataSpecificationContent;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.DataSpecificationIEC61360Content;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.EmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handles the conversion between an IHasDataSpecification object and the XML tag<br>
 * &lt;aas:embeddedDataSpecification&gt; in both directions
 * 
 * @author conradi, espen
 *
 */
public class HasDataSpecificationXMLConverter {
	public static final String EMBEDDED_DATA_SPECIFICATION = "aas:embeddedDataSpecification";
	public static final String DATA_SPECIFICATION_IEC61360 = "aas:dataSpecificationIEC61360";
	public static final String DATA_SPECIFICATION_REFERENCE = "aas:dataSpecification";
	public static final String DATA_SPECIFICATION_CONTENT = "aas:dataSpecificationContent";

	/**
	 * Populates a given IHasDataSpecification object with the data form the given XML
	 * 
	 * @param xmlObject the XML map containing the &lt;aas:embeddedDataSpecification&gt; tag
	 * @param hasDataSpecification the IHasDataSpecification object to be populated -treated as Map here-
	 */
	public static void populateHasDataSpecification(Map<String, Object> xmlObject,
			HasDataSpecification hasDataSpecification) {
		if (xmlObject == null || hasDataSpecification == null) return;
		
		Object xmlDataSpecObj = xmlObject.get(EMBEDDED_DATA_SPECIFICATION);
		if (xmlDataSpecObj != null ) {
			List<IEmbeddedDataSpecification> embeddedSpecList = new ArrayList<>();
			List<Map<String, Object>> xmlSpecList = XMLHelper.getList(xmlDataSpecObj);
			for (Map<String, Object> xmlSpec : xmlSpecList) {
				IReference ref = parseReference(xmlSpec);
				IDataSpecificationContent content = parseContent(xmlSpec);
				EmbeddedDataSpecification spec = new EmbeddedDataSpecification();
				spec.setDataSpecificationTemplate(ref);
				// TODO: Also support other templates
				spec.setContent((DataSpecificationIEC61360Content) content);
				embeddedSpecList.add(spec);
			}
			hasDataSpecification.setEmbeddedDataSpecifications(embeddedSpecList);	
		}
		

		// Note: DataSpecificationReferences are not serialized in XML
		// "http://admin-shell.io/DataSpecificationTemplates/DataSpecificationIEC61360/2/0" could always be added here,
		// as the serialization only supports this template
	}
	
	/**
	 * Parses a Reference Object from the &lt;aas:embeddedDataSpecification&gt; XML tag
	 * 
	 * @param xmlObject the XML map containing the &lt;aas:embeddedDataSpecification&gt; XML tag
	 * @return the parsed Reference object
	 */
	@SuppressWarnings("unchecked")
	private static Reference parseReference(Map<String, Object> xmlSpec) {
		Map<String, Object> refMap = (Map<String, Object>) xmlSpec.get(DATA_SPECIFICATION_REFERENCE);
		if (refMap == null) {
			return null;
		}
		return ReferenceXMLConverter.parseReference(refMap);
	}

	/**
	 * Parses a DataSpecificationContent Object from the &lt;aas:embeddedDataSpecification&gt; XML tag
	 * 
	 * @param xmlObject the XML map containing the &lt;aas:embeddedDataSpecification&gt; XML tag
	 * @return the parsed Content object
	 */
	@SuppressWarnings("unchecked")
	private static IDataSpecificationContent parseContent(Map<String, Object> xmlSpec) {
		Map<String, Object> contentMap = (Map<String, Object>) xmlSpec.get(DATA_SPECIFICATION_CONTENT);
		if (contentMap == null) {
			return null;
		}

		// Only supports parsing the IEC61360 content
		if (contentMap.containsKey(DATA_SPECIFICATION_IEC61360)) {
			Map<String, Object> iec61360ContentMap = (Map<String, Object>) contentMap.get(DATA_SPECIFICATION_IEC61360);
			return DataSpecificationIEC61360XMLConverter.parseDataSpecificationContent(iec61360ContentMap);
		} else {
			return new DataSpecificationContent();
		}
	}

	/**
	 * Populates a given XML map with the data from a given IHasDataSpecification object<br>
	 * Creates the &lt;aas:embeddedDataSpecification&gt; tag in the given root
	 * 
	 * @param document the XML document
	 * @param root the XML root Element to be populated
	 * @param hasDataSpecification the IHasDataSpecification object to be converted to XML
	 */
	@SuppressWarnings("unchecked")
	public static void populateHasDataSpecificationXML(Document document, Element root, IHasDataSpecification hasDataSpecification) {
		// Ignore dataSpecifications as they are not serialized in XML currently

		Collection<IEmbeddedDataSpecification> specs = hasDataSpecification.getEmbeddedDataSpecifications();
		for (IEmbeddedDataSpecification spec : specs) {
			Element embeddedDataSpecRoot = document.createElement(EMBEDDED_DATA_SPECIFICATION);
			// Add content
			IDataSpecificationContent content = spec.getContent();
			Element dataSpecContentRoot = document.createElement(DATA_SPECIFICATION_CONTENT);
			embeddedDataSpecRoot.appendChild(dataSpecContentRoot);
			if (content instanceof Map<?, ?>) {
				// Assume its an IEC61360Content (only type of content supported)
				Element dataSpecIEC61360Root = document.createElement(DATA_SPECIFICATION_IEC61360);
				dataSpecContentRoot.appendChild(dataSpecIEC61360Root);
				populateContent(document, dataSpecIEC61360Root,
						DataSpecificationIEC61360Content.createAsFacade((Map<String, Object>) content));
			}
			// Add template reference
			IReference dataSpecTemplate = spec.getDataSpecificationTemplate();
			Element dataSpecTemplateRoot = document.createElement(DATA_SPECIFICATION_REFERENCE);
			embeddedDataSpecRoot.appendChild(dataSpecTemplateRoot);
			dataSpecTemplateRoot.appendChild(ReferenceXMLConverter.buildReferenceXML(document, dataSpecTemplate));
			root.appendChild(embeddedDataSpecRoot);
		}
	}
		

	/**
	 * Populates a DataSpecificationContent XML from the IDataSpecificationContent object
	 * 
	 * @param document    the XML document
	 * @param contentRoot the XML root Element to be populated
	 * @param content     the IDataSpecification object to be converted to XML
	 */
	private static void populateContent(Document document, Element contentRoot, IDataSpecificationContent content) {
		// Currently, the XML-Schema only supports this data specification -
		// for the future, this method will also need to support additionaly data specification templates
		DataSpecificationIEC61360XMLConverter.populateIEC61360ContentXML(document, contentRoot,
				(IDataSpecificationIEC61360Content) content);
	}
}
