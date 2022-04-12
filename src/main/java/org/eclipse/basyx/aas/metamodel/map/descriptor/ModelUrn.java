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
package org.eclipse.basyx.aas.metamodel.map.descriptor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create URNs with the format urn: {@literal <legalEntity>:<subUnit>:<subModel>:<version>:<revision>:<elementID>#<elementInstance>}
 * 
 * @author kuhn
 *
 */
public class ModelUrn extends Identifier {

	private static Logger logger = LoggerFactory.getLogger(ModelUrn.class);

	private ModelUrn() {
		setIdType(IdentifierType.IRI);
	}
	
	/**
	 * Constructor that accepts a single, raw URN
	 */
	public ModelUrn(String rawURN) {
		this();
		setId(rawURN);
	}
	
	
	/**
	 * Constructor that build a URN
	 */
	public ModelUrn(String legalEntity, String subUnit, String subModel, String version, String revision, String elementId, String elementInstance) {
		this();
		// Goal is: urn:<legalEntity>:<subUnit>:<subModel>:<version>:<revision>:<elementID>#<elementInstance>
		StringBuffer urnBuilder = new StringBuffer();
		
		// Start with header
		urnBuilder.append("urn:");
		// - Add URN components until instance
		if (legalEntity != null) urnBuilder.append(legalEntity); urnBuilder.append(":");
		if (subUnit != null)     urnBuilder.append(subUnit);     urnBuilder.append(":");
		if (subModel != null)    urnBuilder.append(subModel);    urnBuilder.append(":");
		if (version != null)     urnBuilder.append(version);     urnBuilder.append(":");
		if (revision != null)    urnBuilder.append(revision);    urnBuilder.append(":");
		if (elementId != null)   urnBuilder.append(elementId); 
		// - Add element instance, prefix with '#'
		if (elementInstance != null) urnBuilder.append("#"+elementInstance);
		
		// Build URN
		setId(urnBuilder.toString());
	}
	
	
	
	/**
	 * Get URN as string
	 */
	public String getURN() {
		return getId();
	}
	
	
	/**
	 * Get URL encoded URN as string
	 */
	public String getEncodedURN() {
		try {
			// Try to encode urn string
			return URLEncoder.encode(getId(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// Catch block
			logger.error("Exception in getEncodedURN", e);
			return null;
		}
	}
	
	/**
	 * Create a new ModelUrn by appending a String to the URN string, e.g. to create a new element instance
	 */
	public ModelUrn append(String suffix) {
		// Append suffix
		return new ModelUrn(getId() + suffix);
	}
}

