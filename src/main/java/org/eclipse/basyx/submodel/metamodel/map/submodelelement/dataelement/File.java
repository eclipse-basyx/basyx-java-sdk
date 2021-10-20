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

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IFile;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;

/**
 * A blob property as defined in DAAS document <br/>
 * 
 * @author pschorn, schnicke
 *
 */
public class File extends DataElement implements IFile{
	public static final String MIMETYPE="mimeType";
	public static final String MODELTYPE = "File";

	/**
	 * Creates an empty File object
	 */
	public File() {
		// Add model type
		putAll(new ModelType(MODELTYPE));
	}
	
	/**
	 * Constructor accepting only mandatory attribute
	 * @param mimeType
	 */
	public File(String mimeType) {
		this();
		setMimeType(mimeType);
	}
	
	/**
	 * Creates a file data element. It has to have a mimeType <br/>
	 * An absolute path is used in the case that the file exists independently of
	 * the AAS. A relative path, relative to the package root should be used if the
	 * file is part of the serialized package of the AAS.
	 * 
	 * @param value
	 *            path and name of the referenced file (without file extension); can
	 *            be absolute or relative
	 * @param mimeType
	 *            states which file extension the file has; Valid values are defined
	 *            in <i>RFC2046</i>, e.g. <i>image/jpg</i>
	 */
	public File(String value, String mimeType) {
		// Add model type
		putAll(new ModelType(MODELTYPE));

		// Save value
		put(Property.VALUE, value);
		put(MIMETYPE, mimeType);
	}
	
	/**
	 * Creates a File object from a map
	 * 
	 * @param obj a File object as raw map
	 * @return a File object, that behaves like a facade for the given map
	 */
	public static File createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(File.class, obj);
		}
		
		File facade = new File();
		facade.setMap(obj);
		return facade;
	}
	
	/**
	 * Check whether all mandatory elements for the metamodel
	 * exist in a map
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		return DataElement.isValid(obj) && obj.containsKey(File.MIMETYPE);
	}

	/**
	 * Returns true if the given submodel element map is recognized as a fiel
	 */
	public static boolean isFile(Map<String, Object> map) {
		String modelType = ModelType.createAsFacade(map).getName();
		// Either model type is set or the element type specific attributes are contained (fallback)
		// Note: Fallback is ambiguous - Blob has exactly the same attributes
		// => would need value parsing in order to be able to differentiate
		return MODELTYPE.equals(modelType)
				|| (modelType == null && (map.containsKey(Property.VALUE) && map.containsKey(MIMETYPE)));
	}

	@Override
	public void setValue(Object value) {
		if(value instanceof String) {
			setValue((String) value);
		}
		else {
			throw new IllegalArgumentException("Given Object is not a String");
		}
		
	}

	@Override
	public String getValue() {
		return (String) get(Property.VALUE);
	}

	public void setMimeType(String mimeType) {
		put(File.MIMETYPE, mimeType);
	}

	@Override
	public String getMimeType() {
		return (String) get(File.MIMETYPE);
	}
	
	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.FILE;
	}

	@Override
	public File getLocalCopy() {
		// Return a shallow copy
		File copy = new File();
		copy.putAll(this);
		return copy;
	}

	@Override
	public void setValue(String value) {
		put(Property.VALUE, value);
	}
}
