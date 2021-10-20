/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.dataelement;

import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.SubmodelElementXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IBlob;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.Blob;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses &lt;aas:blob&gt; and builds the Blob object from it <br>
 * Builds &lt;aas:blob&gt; from a given Blob object
 * 
 * @author conradi
 *
 */
public class BlobXMLConverter extends SubmodelElementXMLConverter {

	public static final String BLOB = "aas:blob";
	
	
	/**
	 * Parses a Map containing the content of XML tag &lt;aas:blob&gt;
	 * 
	 * @param xmlObject the Map with the content of XML tag &lt;aas:blob&gt;
	 * @return the parsed Blob
	 */
	public static Blob parseBlob(Map<String, Object> xmlObject) {
		String mimeType = XMLHelper.getString(xmlObject.get(MIME_TYPE));
		String value = XMLHelper.getString(xmlObject.get(VALUE));
		Blob blob = new Blob();
		blob.setMimeType(mimeType);
		blob.setValue(value);
		populateSubmodelElement(xmlObject, blob);
		return blob;
	}

	
	
	
	/**
	 * Builds the &lt;aas:blob&gt; XML tag for a Blob
	 * 
	 * @param document the XML document
	 * @param blob the IBlob to build the XML for
	 * @return the &lt;aas:blob&gt; XML tag for the given Blob
	 */
	public static Element buildBlob(Document document, IBlob blob) {
		Element blobRoot = document.createElement(BLOB);
		
		populateSubmodelElement(document, blobRoot, blob);
		
		// Base64 encoded string
		String value = blob.getValue();
		String mimeType = blob.getMimeType();
		
		if(value != null) {
			Element valueRoot = document.createElement(VALUE);
			valueRoot.appendChild(document.createTextNode(value));
			blobRoot.appendChild(valueRoot);
		}
		if(mimeType != null) {
			Element mimeTypeRoot = document.createElement(MIME_TYPE);
			mimeTypeRoot.appendChild(document.createTextNode(mimeType));
			blobRoot.appendChild(mimeTypeRoot);
		}
		
		return blobRoot;
	}
}
