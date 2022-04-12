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
import org.eclipse.basyx.submodel.types.digitalnameplate.enums.MailType;

/**
 * Email as defined in the AAS Digital Nameplate Template document <br>
 * It is a submodel element collection which contains email address and encryption method
 * 
 * @author haque
 *
 */
public class Email extends SubmodelElementCollection {
	public static final String EMAILADDRESSID = "EmailAddress";
	public static final String PUBLICKEYID = "PublicKey";
	public static final String TYPEOFEMAILADDRESSID = "TypeOfEmailAddress";
	public static final String TYPEOFPUBLICKEYID = "TypeOfPublickKey";
	public static final Reference SEMANTICID = new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAQ836#005", KeyType.IRDI));
	
	private Email() {
	}
	
	/**
	 * Constructor with mandatory attributes
	 * @param idShort
	 * @param emailAddress
	 */
	public Email(String idShort, Property emailAddress) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setEmailAddress(emailAddress);
	}
	
	/**
	 * Constructor with mandatory attributes
	 * @param idShort
	 * @param emailAddress
	 */
	public Email(String idShort, String emailAddress) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setEmailAddress(emailAddress);
	}
	
	/**
	 * Creates a Email SMC object from a map
	 * 
	 * @param obj a Email SMC object as raw map
	 * @return a Email SMC object, that behaves like a facade for the given map
	 */
	public static Email createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(Email.class, obj);
		}
		
		Email email = new Email();
		email.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return email;
	}
	
	/**
	 * Creates a Email SMC object from a map without validation
	 * 
	 * @param obj a Email SMC object as raw map
	 * @return a Email SMC object, that behaves like a facade for the given map
	 */
	private static Email createAsFacadeNonStrict(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		Email email = new Email();
		email.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return email;
	}
	
	/**
	 * Check whether all mandatory elements for Email SMC
	 * exist in the map
	 * 
	 * @param obj
	 * 
	 * @return true/false
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValid(Map<String, Object> obj) {
		Email email = createAsFacadeNonStrict(obj);
		
		return SubmodelElementCollection.isValid(obj)
				&& Property.isValid((Map<String, Object>) email.getEmailAddress());
	}
	
	/**
	 * Sets electronic mail address of a business partner
	 * @param emailAddress Property
	 */
	public void setEmailAddress(Property emailAddress) {
		addSubmodelElement(emailAddress);
	}
	
	/**
	 * Sets electronic mail address of a business partner
	 * @param emailAddress String
	 */
	public void setEmailAddress(String emailAddress) {
		Property emailProp = new Property(EMAILADDRESSID, ValueType.String);
		emailProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO198#002", IdentifierType.IRDI)));
		emailProp.setValue(emailAddress);
		setEmailAddress(emailProp);
	}
	
	/**
	 * Gets electronic mail address of a business partner
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IProperty getEmailAddress() {
		return Property.createAsFacade((Map<String, Object>) getSubmodelElement(EMAILADDRESSID));
	}
	
	/**
	 * Sets public part of an unsymmetrical key pair to sign or encrypt text or messages
	 * @param key {@link MultiLanguageProperty}
	 */
	public void setPublicKey(MultiLanguageProperty key) {
		addSubmodelElement(key);
	}
	
	/**
	 * Sets public part of an unsymmetrical key pair to sign or encrypt text or messages
	 * @param key {@link LangString}
	 */
	public void setPublicKey(LangString key) {
		MultiLanguageProperty publicKey = new MultiLanguageProperty(PUBLICKEYID);
		publicKey.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO200#002", IdentifierType.IRDI)));
		publicKey.setValue(new LangStrings(key));
		setPublicKey(publicKey);
	}
	
	/**
	 * Gets public part of an unsymmetrical key pair to sign or encrypt text or messages
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getPublicKey() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(PUBLICKEYID));
	}
	
	/**
	 * Sets characterization of an e-mail address according to its location or usage
	 * enumeration
	 * @param type {@link Property}
	 */
	public void setTypeOfEmailAddress(Property type) {
		addSubmodelElement(type);
	}
	
	/**
	 * Sets characterization of an e-mail address according to its location or usage
	 * enumeration
	 * @param type {@link MailType}
	 */
	public void setTypeOfEmailAddress(MailType type) {
		Property mailTypeProp = new Property(TYPEOFEMAILADDRESSID, ValueType.String);
		mailTypeProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO199#003", IdentifierType.IRDI)));
		mailTypeProp.setValue(type.toString());
		setTypeOfEmailAddress(mailTypeProp);
	}
	
	/**
	 * Gets characterization of an e-mail address according to its location or usage
	 * enumeration
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IProperty getTypeOfEmailAddress() {
		return Property.createAsFacade((Map<String, Object>) getSubmodelElement(TYPEOFEMAILADDRESSID));
	}
	
	/**
	 * Sets characterization of a public key according to its encryption process
	 * @param key {@link MultiLanguageProperty}
	 */
	public void setTypeOfPublicKey(MultiLanguageProperty key) {
		addSubmodelElement(key);
	}
	
	/**
	 * Sets characterization of a public key according to its encryption process
	 * @param key {@link LangString}
	 */
	public void setTypeOfPublicKey(LangString key) {
		MultiLanguageProperty typeOfPublicKey = new MultiLanguageProperty(TYPEOFPUBLICKEYID);
		typeOfPublicKey.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO201#002", IdentifierType.IRDI)));
		typeOfPublicKey.setValue(new LangStrings(key));
		setTypeOfPublicKey(typeOfPublicKey);
	}
	
	/**
	 * Gets characterization of a public key according to its encryption process
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getTypeOfPublicKey() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(TYPEOFPUBLICKEYID));
	}
}
