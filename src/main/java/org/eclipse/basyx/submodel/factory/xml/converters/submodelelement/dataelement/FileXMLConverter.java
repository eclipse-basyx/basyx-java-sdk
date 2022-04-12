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
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IFile;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses &lt;aas:file&gt; and builds the File object from it <br>
 * Builds &lt;aas:file&gt; from a given File object
 * 
 * @author conradi
 *
 */
public class FileXMLConverter extends SubmodelElementXMLConverter{

	public static final String FILE = "aas:file";
	
	
	/**
	 * Parses a Map containing the content of XML tag &lt;aas:file&gt;
	 * 
	 * @param xmlObject the Map with the content of XML tag &lt;aas:file&gt;
	 * @return the parsed File
	 */
	public static File parseFile(Map<String, Object> xmlObject) {
		String mimeType = XMLHelper.getString(xmlObject.get(MIME_TYPE));
		String value = XMLHelper.getString(xmlObject.get(VALUE));
		File file = new File(value, mimeType);
		populateSubmodelElement(xmlObject, file);
		return file;
	}
	
	
	
	
	/**
	 * Builds the &lt;aas:file&gt; XML tag for a File
	 * 
	 * @param document the XML document
	 * @param file the IFile to build the XML for
	 * @return the &lt;aas:file&gt; XML tag for the given File
	 */
	public static Element buildFile(Document document, IFile file) {
		Element fileRoot = document.createElement(FILE);
		
		populateSubmodelElement(document, fileRoot, file);
		
		String mimeType = file.getMimeType();
		String value = file.getValue();
		if(mimeType != null) {
			Element mimeTypeRoot = document.createElement(MIME_TYPE);
			mimeTypeRoot.appendChild(document.createTextNode(mimeType));
			fileRoot.appendChild(mimeTypeRoot);
		}
		if(value != null) {
			Element valueRoot = document.createElement(VALUE);
			valueRoot.appendChild(document.createTextNode(value));
			fileRoot.appendChild(valueRoot);
		}
		
		return fileRoot;
	}
}
