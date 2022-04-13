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
package org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.SubmodelElementCollectionXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IDataElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.relationship.IAnnotatedRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.AnnotatedRelationshipElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses &lt;aas:annotatedRelationshipElement&gt; and builds an
 * AnnotatedRelationshipElement object from it <br>
 * Builds &lt;aas:annotatedRelationshipElement&gt; from a given
 * AnnotatedRelationshipElement
 * 
 * @author conradi
 *
 */
public class AnnotatedRelationshipElementXMLConverter {
	public static final String ANNOTATED_RELATIONSHIP_ELEMENT = "aas:annotatedRelationshipElement";
	public static final String ANNOTATIONS = "aas:annotations";

	/**
	 * Builds a AnnotatedRelationshipElement object from the given XML
	 * 
	 * @param xmlObject
	 *            the Map with the content of XML tag
	 *            &lt;aas:annotatedRelationshipElement&gt;
	 * @return the parsed AnnotatedRelationshipElement
	 */
	@SuppressWarnings("unchecked")
	public static AnnotatedRelationshipElement parseAnnotatedRelationshipElement(Map<String, Object> xmlObject) {
		AnnotatedRelationshipElement annotatedElement = new AnnotatedRelationshipElement();

		RelationshipElementXMLConverter.populateRelationshipElement(xmlObject, annotatedElement);

		Map<String, Object> xmlAnnotations = (Map<String, Object>) xmlObject.get(ANNOTATIONS);
		List<Map<String, Object>> xmlDataElements = XMLHelper.getList(xmlAnnotations.get(SubmodelElementCollectionXMLConverter.DATA_ELEMENT));

		List<IDataElement> dataElements = new ArrayList<>();
		for (Map<String, Object> element : xmlDataElements) {
			ISubmodelElement smElement = SubmodelElementCollectionXMLConverter.getSubmodelElement(element);

			// Check if all Elements contained in <aas:annotations> is an IDataElement
			if (smElement instanceof IDataElement) {
				dataElements.add((IDataElement) smElement);
			} else {
				throw new RuntimeException("AnnotatedRelationshipElement '" + annotatedElement.getIdShort() + "' can only contain IDataElement as annotation");
			}
		}

		annotatedElement.setAnnotation(dataElements);
		return annotatedElement;
	}

	/**
	 * Builds the &lt;aas:annotatedRelationshipElement&gt; XML tag for a
	 * AnnotatedRelationshipElement
	 * 
	 * @param document
	 *            the XML document
	 * @param annotatedElement
	 *            the IAnnotatedRelationshipElement to build the XML for
	 * @return the &lt;aas:annotatedRelationshipElement&gt; XML tag for the given
	 *         AnnotatedRelationshipElement
	 */
	public static Element buildAnnotatedRelationshipElement(Document document, IAnnotatedRelationshipElement annotatedElement) {
		Element root = document.createElement(ANNOTATED_RELATIONSHIP_ELEMENT);
		RelationshipElementXMLConverter.populateRelationshipElement(document, root, annotatedElement);

		Element annotationsRoot = document.createElement(ANNOTATIONS);
		root.appendChild(annotationsRoot);

		for (IDataElement dataElement : annotatedElement.getValue().getAnnotations()) {
			Element dataElementRoot = document.createElement(SubmodelElementCollectionXMLConverter.DATA_ELEMENT);
			Element dataElementContent = SubmodelElementCollectionXMLConverter.buildSubmodelElement(document, dataElement);
			dataElementRoot.appendChild(dataElementContent);
			annotationsRoot.appendChild(dataElementRoot);
		}

		return root;
	}

}
