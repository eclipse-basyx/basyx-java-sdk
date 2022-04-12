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
package org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IBlob;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;

/**
 * A blob element as defined in DAAS document <br>
 * 
 * @author pschorn, schnicke
 *
 */
public class Blob extends DataElement implements IBlob {
	public static final String MIMETYPE = "mimeType";
	public static final String MODELTYPE = "Blob";
	
	/**
	 * Creates an empty Blob object
	 */
	public Blob() {
		// Add model type
		putAll(new ModelType(MODELTYPE));
	}
	
	/**
	 * Constructor accepting only mandatory attribute
	 * @param idShort
	 * @param mimeType
	 */
	public Blob(String idShort, String mimeType) {
		super(idShort);
		putAll(new ModelType(MODELTYPE));
		setMimeType(mimeType);
	}

	/**
	 * Has to have a MimeType
	 * 
	 * @param value
	 *            the value of the BLOB instance of a blob data element
	 * @param mimeType
	 *            states which file extension the file has; Valid values are defined
	 *            in <i>RFC2046</i>, e.g. <i>image/jpg</i>
	 */
	public Blob(byte[] value, String mimeType) {
		// Add model type
		putAll(new ModelType(MODELTYPE));

		setByteArrayValue(value);
		setMimeType(mimeType);
	}
	
	/**
	 * Creates a Blob object from a map
	 * 
	 * @param obj a Blob object as raw map
	 * @return a Blob object, that behaves like a facade for the given map
	 */
	public static Blob createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(Blob.class, obj);
		}
		
		Blob facade = new Blob();
		facade.setMap(obj);
		return facade;
	}
	
	/**
	 * Check whether all mandatory elements for the metamodel
	 * exist in a map
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		return DataElement.isValid(obj) && obj.containsKey(Blob.MIMETYPE);
	}
	
	/**
	 * Returns true if the given submodel element map is recognized as a blob
	 */
	public static boolean isBlob(Map<String, Object> map) {
		String modelType = ModelType.createAsFacade(map).getName();
		// Either model type is set or the element type specific attributes are contained (fallback)
		// Note: Fallback is ambiguous - File has exactly the same attributes
		// => would need value parsing in order to be able to differentiate
		return MODELTYPE.equals(modelType)
				|| (modelType == null && (map.containsKey(Property.VALUE) && map.containsKey(MIMETYPE)));
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof String) {
			// Assume a Base64 encoded String
			setValue((String) value);
		} else {
			throw new IllegalArgumentException("Given Object is not a String");
		}
	}

	@Override
	public String getValue() {
		if (!containsKey(Property.VALUE)) {
			return null;
		}
		return (String) get(Property.VALUE);
	}
	
	@Override
	public byte[] getByteArrayValue() {
		String value = getValue();
		if ( value != null ) {
			return Base64.getDecoder().decode(value);
		} else {
			return null;
		}
	}

	@Override
	public String getUTF8String() {
		byte[] value = getByteArrayValue();
		return new String(value, StandardCharsets.UTF_8);
	}

	@Override
	public void setUTF8String(String text) {
		setByteArrayValue(text.getBytes(StandardCharsets.UTF_8));
	}

	public void setMimeType(String mimeType) {
		put(Blob.MIMETYPE, mimeType);
	}

	@Override
	public String getMimeType() {
		return (String) get(Blob.MIMETYPE);
	}
	
	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.BLOB;
	}

	@Override
	public Blob getLocalCopy() {
		// Return a shallow copy
		Blob copy = new Blob();
		copy.putAll(this);
		return copy;
	}

	@Override
	public void setByteArrayValue(byte[] value) {
		if (value != null) {
			setValue(Base64.getEncoder().encodeToString(value));
		} else {
			setValue(null);
		}
	}

	@Override
	public void setValue(String value) {
		put(Property.VALUE, value);
	}
}
