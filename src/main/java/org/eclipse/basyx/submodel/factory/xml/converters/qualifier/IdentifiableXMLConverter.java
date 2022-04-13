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

import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.AdministrativeInformation;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

/**
 * Handles the conversion between an IIdentifiable object and the XML tags<br>
 * &lt;aas:administration&gt; and &lt;aas:identification&gt; in both directions
 * 
 * @author conradi
 *
 */
public class IdentifiableXMLConverter {
	private static final Logger logger = LoggerFactory.getLogger(IdentifiableXMLConverter.class);

	public static final String ADMINISTRATION = "aas:administration";
	public static final String VERSION = "aas:version";
	public static final String REVISION = "aas:revision";
	public static final String IDENTIFICATION = "aas:identification";
	public static final String IDTYPE = "idType";

	/**
	 * Populates a given Identifiable object with the data form the given XML
	 * 
	 * @param xmlObject
	 *            the XML map containing the &lt;aas:administration&gt; and
	 *            &lt;aas:identification&gt; tags
	 * @param identifiable
	 *            the Identifiable object to be populated
	 */
	@SuppressWarnings("unchecked")
	public static void populateIdentifiable(Map<String, Object> xmlObject, Identifiable identifiable) {
		ReferableXMLConverter.populateReferable(xmlObject, identifiable);

		Map<String, Object> identierFromXML = (Map<String, Object>) xmlObject.get(IDENTIFICATION);
		if (identierFromXML == null) {
			throw createInvalidIdentifierException(xmlObject);
		}
		String id = XMLHelper.getString(identierFromXML.get(XMLHelper.TEXT));
		String idType = XMLHelper.getString(identierFromXML.get(IDTYPE));
		if (Strings.isNullOrEmpty(id)) {
			// Warns without exception to enable parsing external aasx-files without id
			logger.warn("Invalid XML of Identifiable. No valid identification is present. " + xmlObject.toString());
		}

		if (Strings.isNullOrEmpty(idType)) {
			// Warns without exception to enable parsing external aasx-files without idType
			logger.warn("Invalid XML of Identifiable. empty identifierType changed to default identifierType Custom. " + xmlObject.toString());
			idType = IdentifierType.CUSTOM.toString();
		}

		// Enables parsing external aasx-files with URI instead of IRI
		if (idType.equalsIgnoreCase("URI")) {
			idType = IdentifierType.IRI.toString();
		}

		identifiable.setIdentification(IdentifierType.fromString(idType), id);

		Map<String, Object> administrationFromXML = (Map<String, Object>) xmlObject.get(ADMINISTRATION);
		if (administrationFromXML != null) {
			String version = XMLHelper.getString(administrationFromXML.get(VERSION));
			String revision = XMLHelper.getString(administrationFromXML.get(REVISION));

			// Enables parsing external aasx-files with revision and empty version
			if (!Strings.isNullOrEmpty(revision) && Strings.isNullOrEmpty(version)) {
				version = "0.0.1";
			}

			identifiable.setAdministration(new AdministrativeInformation(version, revision));
		}
	}

	/**
	 * Populates a given XML map with the data from a given IIdentifiable object<br>
	 * Creates the &lt;aas:administration&gt; and &lt;aas:identification&gt; tags in
	 * the given root
	 * 
	 * @param document
	 *            the XML document
	 * @param root
	 *            the XML root Element to be populated
	 * @param identifiable
	 *            the IIdentifiable object to be converted to XML
	 */
	public static void populateIdentifiableXML(Document document, Element root, IIdentifiable identifiable) {
		ReferableXMLConverter.populateReferableXML(document, root, identifiable);

		// Build the identification if present
		if (identifiable.getIdentification() != null) {
			String id = identifiable.getIdentification().getId();
			Element identificationRoot = document.createElement(IDENTIFICATION);
			identificationRoot.appendChild(document.createTextNode(id));
			if (identifiable.getIdentification().getIdType() != null) {
				IdentifierType idType = identifiable.getIdentification().getIdType();
				identificationRoot.setAttribute(IDTYPE, idType.toString());
			}
			root.appendChild(identificationRoot);
		}

		// Build the administration if present
		if (identifiable.getAdministration() != null) {
			Element version = null;
			Element revision = null;

			String versionString = identifiable.getAdministration().getVersion();
			if (versionString != null && !versionString.isEmpty()) {
				version = document.createElement(VERSION);
				version.appendChild(document.createTextNode(versionString));

			}

			String revisionString = identifiable.getAdministration().getRevision();
			if (revisionString != null && !revisionString.isEmpty()) {
				revision = document.createElement(REVISION);
				revision.appendChild(document.createTextNode(revisionString));
			}

			// If one at least one f the elements exists, create the aas:administration
			// element
			if (version != null || revision != null) {
				Element administrationRoot = document.createElement(ADMINISTRATION);
				if (version != null) {
					administrationRoot.appendChild(version);
				}
				if (revision != null) {
					administrationRoot.appendChild(revision);
				}
				root.appendChild(administrationRoot);
			}
		}
	}

	private static RuntimeException createInvalidIdentifierException(Map<String, Object> xmlObject) {
		return new RuntimeException("Invalid XML of Identifiable. No valid identification is present. " + xmlObject.toString());
	}
}
