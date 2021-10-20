/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
 * A blob element as defined in DAAS document <br/>
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
