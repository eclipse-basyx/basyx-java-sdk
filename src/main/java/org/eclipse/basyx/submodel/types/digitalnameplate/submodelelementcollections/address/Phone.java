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
package org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.address;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IMultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.types.digitalnameplate.enums.PhoneType;

/**
 * Phone as defined in the AAS Digital Nameplate Template document <br>
 * It is a submodel element collection which contains a phone number including type
 * 
 * @author haque
 *
 */
public class Phone extends SubmodelElementCollection {
	public static final String TELEPHONENUMBERID = "TelephoneNumber";
	public static final String TYPEOFTELEPHONEID = "TypeOfTelephone";
	public static final String PHONEID = "Phone";
	public static final Reference SEMANTICID = new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAQ833#005", KeyType.IRDI));
	
	private Phone() {}
	
	/**
	 * Constructor with mandatory attributes
	 * @param idShort
	 * @param telephoneNumber
	 */
	public Phone(String idShort, LangString telephoneNumber) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setTelephoneNumber(telephoneNumber);
	}
	
	/**
	 * Constructor with mandatory attributes
	 * @param idShort
	 * @param telephoneNumber
	 */
	public Phone(String idShort, MultiLanguageProperty telephoneNumber) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setTelephoneNumber(telephoneNumber);
	}
	
	/**
	 * Creates a Phone SMC object from a map
	 * 
	 * @param obj a Phone SMC object as raw map
	 * @return a Phone SMC object, that behaves like a facade for the given map
	 */
	public static Phone createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(Phone.class, obj);
		}
		
		Phone phone = new Phone();
		phone.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return phone;
	}
	
	/**
	 * Creates a Phone SMC object from a map without checking validity
	 * 
	 * @param obj a Phone SMC object as raw map
	 * @return a Phone SMC object, that behaves like a facade for the given map
	 */
	private static Phone createAsFacadeNonStrict(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		Phone phone = new Phone();
		phone.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return phone;
	}
	
	/**
	 * Check whether all mandatory elements for Phone SMC
	 * exist in the map
	 * 
	 * @param obj
	 * 
	 * @return true/false
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValid(Map<String, Object> obj) {
		Phone phone = createAsFacadeNonStrict(obj);
		
		return SubmodelElementCollection.isValid(obj)
				&& MultiLanguageProperty.isValid((Map<String, Object>) phone.getTelephoneNumber());
	}
	
	/**
	 * Sets complete telephone number to be called to reach a business partner
	 * @param telephoneNumber {@link MultiLanguageProperty}
	 */
	public void setTelephoneNumber(MultiLanguageProperty telephoneNumber) {
		addSubmodelElement(telephoneNumber);
	}
	
	/**
	 * Sets complete telephone number to be called to reach a business partner
	 * @param telephoneNumber {@link LangString}
	 */
	public void setTelephoneNumber(LangString telephoneNumber) {
		MultiLanguageProperty phoneProp = new MultiLanguageProperty(TELEPHONENUMBERID);
		phoneProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO136#002", IdentifierType.IRDI)));
		phoneProp.setValue(new LangStrings(telephoneNumber));
		setTelephoneNumber(phoneProp);
	}
	
	/**
	 * Sets characterization of a telephone according to its location or usage enumeration
	 * @param type {@link Property}
	 */
	public void setTypeOfTelephone(Property type) {
		addSubmodelElement(type);
	}
	
	/**
	 * Sets characterization of a telephone according to its location or usage enumeration
	 * @param type {@link PhoneType}
	 */
	public void setTypeOfTelephone(PhoneType type) {
		Property phoneTypeProp = new Property(TYPEOFTELEPHONEID, ValueType.String);
		phoneTypeProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO137#003", IdentifierType.IRDI)));
		phoneTypeProp.setValue(type.toString());
		setTypeOfTelephone(phoneTypeProp);
	}
	
	/**
	 * Gets characterization of a telephone according to its location or usage enumeration
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IProperty getTypeOfTelephone() {
		return Property.createAsFacade((Map<String, Object>) getSubmodelElement(TYPEOFTELEPHONEID));
	}
	
	/**
	 * Gets complete telephone number to be called to reach a business partner
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getTelephoneNumber() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(TELEPHONENUMBERID));
	}
}
