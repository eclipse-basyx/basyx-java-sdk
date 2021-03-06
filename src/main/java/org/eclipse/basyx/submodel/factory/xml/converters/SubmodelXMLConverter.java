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
package org.eclipse.basyx.submodel.factory.xml.converters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.HasDataSpecificationXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.HasSemanticsXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.IdentifiableXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.haskind.HasKindXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.qualifiable.QualifiableXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.SubmodelElementXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.haskind.HasKind;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifiable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handles the conversion between ISubmodel objects and the XML tag
 * &lt;aas:submodels&gt; in both directions
 * 
 * @author conradi
 *
 */
public class SubmodelXMLConverter {

	public static final String SUBMODELS = "aas:submodels";
	public static final String SUBMODEL = "aas:submodel";

	/**
	 * Parses &lt;aas:submodels&gt; and builds the Submodel objects from it
	 * 
	 * @param xmlObject
	 *            a Map containing the content of the XML tag &lt;aas:submodels&gt;
	 * @return a List of ISubmodel objects parsed form the given XML Map
	 */
	public static List<ISubmodel> parseSubmodels(Map<String, Object> xmlObject) {
		List<Map<String, Object>> xmlSubmodels = XMLHelper.getList(xmlObject.get(SUBMODEL));
		List<ISubmodel> submodels = new ArrayList<>();

		for (Map<String, Object> xmlSubmodel : xmlSubmodels) {
			Submodel submodel = new Submodel();

			IdentifiableXMLConverter.populateIdentifiable(xmlSubmodel, Identifiable.createAsFacadeNonStrict(submodel, KeyElements.SUBMODEL));
			HasSemanticsXMLConverter.populateHasSemantics(xmlSubmodel, HasSemantics.createAsFacade(submodel));
			HasDataSpecificationXMLConverter.populateHasDataSpecification(xmlSubmodel, HasDataSpecification.createAsFacade(submodel));
			QualifiableXMLConverter.populateQualifiable(xmlSubmodel, Qualifiable.createAsFacade(submodel));
			HasKindXMLConverter.populateHasKind(xmlSubmodel, HasKind.createAsFacade(submodel));

			List<ISubmodelElement> submodelElements = SubmodelElementXMLConverter.parseSubmodelElements(xmlSubmodel);

			for (ISubmodelElement submdoElement : submodelElements) {
				submodel.addSubmodelElement(submdoElement);
			}

			submodels.add(submodel);
		}
		return submodels;
	}

	/**
	 * Builds &lt;aas:submodels&gt; from a given Collection of ISubmodel objects
	 * 
	 * @param document
	 *            the XML document
	 * @param subModels
	 *            a Collection of ISubmodel objects to build the XML for
	 * @return the &lt;aas:submodels&gt; XML tag for the given ISubmodel objects
	 */
	public static Element buildSubmodelsXML(Document document, Collection<ISubmodel> subModels) {
		Element root = document.createElement(SUBMODELS);

		List<Element> xmlSubmodelList = new ArrayList<>();
		for (ISubmodel subModel : subModels) {
			Element subModelRoot = document.createElement(SUBMODEL);

			IdentifiableXMLConverter.populateIdentifiableXML(document, subModelRoot, subModel);
			HasKindXMLConverter.populateHasKindXML(document, subModelRoot, subModel);
			HasSemanticsXMLConverter.populateHasSemanticsXML(document, subModelRoot, subModel);
			QualifiableXMLConverter.populateQualifiableXML(document, subModelRoot, subModel);
			HasDataSpecificationXMLConverter.populateHasDataSpecificationXML(document, subModelRoot, subModel);

			Collection<ISubmodelElement> submodelElements = (Collection<ISubmodelElement>) subModel.getSubmodelElements().values();

			Element xmlSubmodelElements = SubmodelElementXMLConverter.buildSubmodelElementsXML(document, submodelElements);
			subModelRoot.appendChild(xmlSubmodelElements);

			xmlSubmodelList.add(subModelRoot);
		}

		for (Element element : xmlSubmodelList) {
			root.appendChild(element);
		}
		return root;
	}
}
