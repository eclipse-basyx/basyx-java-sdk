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
	 * @param xmlObject
	 *            the Map with the content of XML tag &lt;aas:blob&gt;
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
	 * @param document
	 *            the XML document
	 * @param blob
	 *            the IBlob to build the XML for
	 * @return the &lt;aas:blob&gt; XML tag for the given Blob
	 */
	public static Element buildBlob(Document document, IBlob blob) {
		Element blobRoot = document.createElement(BLOB);

		populateSubmodelElement(document, blobRoot, blob);

		// Base64 encoded string
		String value = blob.getValue();
		String mimeType = blob.getMimeType();

		if (value != null) {
			Element valueRoot = document.createElement(VALUE);
			valueRoot.appendChild(document.createTextNode(value));
			blobRoot.appendChild(valueRoot);
		}
		if (mimeType != null) {
			Element mimeTypeRoot = document.createElement(MIME_TYPE);
			mimeTypeRoot.appendChild(document.createTextNode(mimeType));
			blobRoot.appendChild(mimeTypeRoot);
		}

		return blobRoot;
	}
}
