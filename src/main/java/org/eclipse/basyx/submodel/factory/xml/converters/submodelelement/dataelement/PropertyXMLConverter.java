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
package org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.dataelement;

import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.factory.xml.converters.reference.ReferenceXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.SubmodelElementXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses &lt;aas:property&gt; and builds the Property object from it <br>
 * Builds &lt;aas:property&gt; from a given Property object
 * 
 * @author conradi, jungjan
 *
 */
public class PropertyXMLConverter extends SubmodelElementXMLConverter {
	
	private static Logger logger = LoggerFactory.getLogger(PropertyXMLConverter.class);
	
	public static final String PROPERTY = "aas:property";
	

	/**
	 * Parses a Map containing the content of XML tag &lt;aas:property&gt;
	 * 
	 * @param xmlObject the Map with the content of XML tag &lt;aas:property&gt;
	 * @return the parsed Property
	 */
	@SuppressWarnings("unchecked")
	public static Property parseProperty(Map<String, Object> xmlObject) {
		
		Property property = new Property();
		
		populateSubmodelElement(xmlObject, property);
		
		String valueType = XMLHelper.getString(xmlObject.get(SubmodelElementXMLConverter.VALUE_TYPE));
		String xmlStringValue = XMLHelper.getString(xmlObject.get(SubmodelElementXMLConverter.VALUE));

		ValueType propertyType = XMLHelper.convertAASXValueTypeToLocal(valueType);
		Object propertyValue = XMLHelper.convertAASXValueToLocal(xmlStringValue, propertyType);
		
		Map<String, Object> xmlValueId = (Map<String, Object>) xmlObject.get(VALUE_ID);
		Reference valueId = ReferenceXMLConverter.parseReference(xmlValueId);
		
		property.set(propertyValue, propertyType);
		
		if(valueId != null) {
			property.setValueId(valueId);
		}
		
		return property;
	}
	
	
	
	
	/**
	 * Builds the &lt;aas:property&gt; XML tag for a Property
	 * 
	 * @param document the XML document
	 * @param prop the ISingleProperty to build the XML for
	 * @return the &lt;aas:property&gt; XML tag for the given Property
	 */
	public static Element buildProperty(Document document, IProperty prop) {
		Element propertyRoot = document.createElement(PROPERTY);
		
		populateSubmodelElement(document, propertyRoot, prop);

		String value = null;
		
		IReference valueId = prop.getValueId();
		if(valueId != null) {
			Element valueIdRoot = document.createElement(VALUE_ID);
			valueIdRoot.appendChild(ReferenceXMLConverter.buildReferenceXML(document, valueId)); 
			propertyRoot.appendChild(valueIdRoot);
		}	
		
		//for some reason, get() in ISingleProperty might throw an Exception
		try {
			Object valueObj = prop.getValue();
			value = valueObj == null ? null : valueObj.toString();
		} catch (Exception e) {
			logger.error("Exeption in buildProperty!", e);
		}
		
		if (value != null) {
			Element valueEle = document.createElement(SubmodelElementXMLConverter.VALUE);
			valueEle.appendChild(document.createTextNode(value));
			propertyRoot.appendChild(valueEle);
		}
		
		String valueType = prop.getValueType().toString();
		if (valueType != null) {
			Element valueTypeElem = document.createElement(SubmodelElementXMLConverter.VALUE_TYPE);
			valueTypeElem.appendChild(document.createTextNode(valueType));
			propertyRoot.appendChild(valueTypeElem);
		}
		
		return propertyRoot;
	}
}
