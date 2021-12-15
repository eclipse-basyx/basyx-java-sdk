/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.basyx.submodel.types.digitalnameplate.enums.FaxType;

/**
 * Fax as defined in the AAS Digital Nameplate Template document <br>
 * It is a submodel element collection which contains a fax number including type
 * 
 * @author haque
 *
 */
public class Fax extends SubmodelElementCollection {
	public static final String FAXNUMBERID = "FaxNumber";
	public static final String TYPEOFFAXID = "TypeOfFaxNumber";
	public static final Reference SEMANTICID = new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAQ834#005", KeyType.IRDI));
	
	private Fax() {
	}
	
	/**
	 * Constructor with mandatory attributes
	 * @param idShort
	 * @param faxNumber
	 */
	public Fax(String idShort, LangString faxNumber) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setFaxNumber(faxNumber);
	}
	
	/**
	 * Constructor with mandatory attributes
	 * @param idShort
	 * @param faxNumber
	 */
	public Fax(String idShort, MultiLanguageProperty faxNumber) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setFaxNumber(faxNumber);
	}
	
	/**
	 * Creates a Fax SMC object from a map
	 * 
	 * @param obj a Fax SMC object as raw map
	 * @return a Fax SMC object, that behaves like a facade for the given map
	 */
	public static Fax createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(Fax.class, obj);
		}
		
		Fax fax = new Fax();
		fax.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return fax;
	}
	
	/**
	 * Creates a Fax SMC object from a map without validation
	 * 
	 * @param obj a Fax SMC object as raw map
	 * @return a Fax SMC object, that behaves like a facade for the given map
	 */
	private static Fax createAsFacadeNonStrict(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		Fax fax = new Fax();
		fax.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return fax;
	}
	
	/**
	 * Check whether all mandatory elements for Fax SMC
	 * exist in the map
	 * 
	 * @param obj
	 * 
	 * @return true/false
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValid(Map<String, Object> obj) {
		Fax fax = createAsFacadeNonStrict(obj);
		return SubmodelElementCollection.isValid(obj)
				&& MultiLanguageProperty.isValid((Map<String, Object>) fax.getFaxNumber());
	}
	
	/**
	 * Sets complete telephone number to be called to reach a 
	 * business partner's fax machine
	 * @param faxNumber {@link MultiLanguageProperty}
	 */
	public void setFaxNumber(MultiLanguageProperty faxNumber) {
		addSubmodelElement(faxNumber);
	}
	
	/**
	 * Sets complete telephone number to be called to reach a 
	 * business partner's fax machine
	 * @param faxNumber {@link LangString}
	 */
	public void setFaxNumber(LangString faxNumber) {
		MultiLanguageProperty faxProp = new MultiLanguageProperty(FAXNUMBERID);
		faxProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO195#002", IdentifierType.IRDI)));
		faxProp.setValue(new LangStrings(faxNumber));
		setFaxNumber(faxProp);
	}
	
	/**
	 * Sets characterization of the fax according its location or usage
	 * @param type {@link Property}
	 */
	public void setTypeOfFaxNumber(Property type) {
		addSubmodelElement(type);
	}
	
	/**
	 * Sets characterization of the fax according its location or usage
	 * @param type {@link FaxType}
	 */
	public void setTypeOfFaxNumber(FaxType type) {
		Property faxTypeProp = new Property(TYPEOFFAXID, ValueType.String);
		faxTypeProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO196#003", IdentifierType.IRDI)));
		faxTypeProp.setValue(type.toString());
		setTypeOfFaxNumber(faxTypeProp);
	}
	
	/**
	 * Gets characterization of the fax according its location or usage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IProperty getTypeOfFaxNumber() {
		return Property.createAsFacade((Map<String, Object>) getSubmodelElement(TYPEOFFAXID));
	}
	
	/**
	 * Gets complete telephone number to be called to reach a 
	 * business partner's fax machine
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getFaxNumber() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(FAXNUMBERID));
	}
}
