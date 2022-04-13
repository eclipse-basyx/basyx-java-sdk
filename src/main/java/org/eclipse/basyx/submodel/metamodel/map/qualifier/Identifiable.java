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
package org.eclipse.basyx.submodel.metamodel.map.qualifier;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IAdministrativeInformation;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;

/**
 * Identifiable class
 * 
 * @author kuhn, schnicke
 *
 */
public class Identifiable extends Referable implements IIdentifiable {
	public static final String ADMINISTRATION = "administration";

	public static final String IDENTIFICATION = "identification";

	/**
	 * Default constructor
	 */
	public Identifiable() {
		// Add qualifier
		putAll(new Referable());
		// The globally unique identification of an element. (Identificator)
		put(IDENTIFICATION, new Identifier());
	}

	/**
	 * Constructor with mandatory attribute
	 * 
	 * @param idShort
	 * @param identification
	 */
	public Identifiable(String idShort, IIdentifier identification) {
		super(idShort);
		setIdentification(identification.getIdType(), identification.getId());
	}

	/**
	 * Constructor that accepts values for most relevant properties
	 */
	public Identifiable(String version, String revision, String idShort, String category, LangStrings description, IdentifierType idType, String id) {
		// Add qualifier
		putAll(new Referable(idShort, category, description));

		// Create administrative information of an element. (AdministrativeInformation)
		put(ADMINISTRATION, new AdministrativeInformation(version, revision));
		// The globally unique identification of an element. (Identificator)
		put(IDENTIFICATION, new Identifier(idType, id));
	}

	/**
	 * Creates a Identifiable object from a map
	 * 
	 * @param map
	 *            a Identifiable object as raw map
	 * @return a Identifiable object, that behaves like a facade for the given map
	 */
	public static Identifiable createAsFacade(Map<String, Object> map, KeyElements type) {
		if (map == null) {
			return null;
		}

		if (!isValid(map)) {
			throw new MetamodelConstructionException(Identifiable.class, map);
		}

		Identifiable ret = new Identifiable();
		ret.setMap(map);
		ret.setElementType(type);
		return ret;
	}

	/**
	 * Check whether all mandatory elements for the metamodel exist in a map
	 * 
	 * @return true/false
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValid(Map<String, Object> map) {
		return Referable.isValid(map) && map.containsKey(Identifiable.IDENTIFICATION) && Identifier.isValid((Map<String, Object>) map.get(Identifiable.IDENTIFICATION));
	}

	/**
	 * Creates an Identifiable object from a map Without checking mandatory
	 * attributes present
	 * 
	 * @param map
	 * @param type
	 * @return
	 */
	public static Identifiable createAsFacadeNonStrict(Map<String, Object> map, KeyElements type) {
		if (map == null) {
			return null;
		}

		Identifiable ret = new Identifiable();
		ret.setMap(map);
		ret.setElementType(type);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IAdministrativeInformation getAdministration() {
		return AdministrativeInformation.createAsFacade((Map<String, Object>) get(Identifiable.ADMINISTRATION));
	}

	@SuppressWarnings("unchecked")
	@Override
	public IIdentifier getIdentification() {
		return Identifier.createAsFacade((Map<String, Object>) get(Identifiable.IDENTIFICATION));
	}

	public void setAdministration(AdministrativeInformation info) {
		put(Identifiable.ADMINISTRATION, info);
	}

	public void setIdentification(IdentifierType idType, String id) {
		put(Identifiable.IDENTIFICATION, new Identifier(idType, id));
	}

	@Override
	protected String getId() {
		return getIdentification().getId();
	}

	@Override
	protected KeyType getKeyType() {
		// KeyType and IdentifierType are virtually the same, so convert them here
		return KeyType.fromString(getIdentification().getIdType().toString());
	}

	@Override
	protected boolean isLocal() {
		return true;
	}

}
