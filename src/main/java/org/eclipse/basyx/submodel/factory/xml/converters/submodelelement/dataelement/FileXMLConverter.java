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
