/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.furtherinformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IMultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.types.helper.SubmodelElementRetrievalHelper;

/**
 * FurtherInformation as described in the Submodel Template AAS Technical Data Document
 * It contains Further information on the product, the validity of the information provided and this data record.
 * 
 * @author haque
 *
 */
public class FurtherInformation extends SubmodelElementCollection {
	public static final String IDSHORT = "FurtherInformation";
	public static final Reference SEMANTICID = new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/ZVEI/TechnicalData/FurtherInformation/1/1", KeyType.IRI));
	public static final String TEXTSTATEMENTPREFIX = "TextStatement";
	public static final String VALIDDATEID = "ValidDate";
	
	private FurtherInformation() {
	}
	
	/**
	 * Constructor with default idShort
	 * @param validDate
	 */
	public FurtherInformation(Property validDate) {
		this(IDSHORT, validDate);
	}
	
	/**
	 * Constructor with default idShort
	 * @param validDate
	 */
	public FurtherInformation(XMLGregorianCalendar validDate) {
		this(IDSHORT, validDate);
	}
	
	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 * @param validDate
	 */
	public FurtherInformation(String idShort, Property validDate) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setValidDate(validDate);
		setTextStatements(new ArrayList<MultiLanguageProperty>());
	}
	
	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 * @param validDate
	 */
	public FurtherInformation(String idShort, XMLGregorianCalendar validDate) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setValidDate(validDate);
		setTextStatements(new ArrayList<MultiLanguageProperty>());
	}
	
	/**
	 * Creates a FurtherInformation SMC object from a map
	 * 
	 * @param obj a FurtherInformation SMC object as raw map
	 * @return a FurtherInformation SMC object, that behaves like a facade for the given map
	 */
	public static FurtherInformation createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(FurtherInformation.class, obj);
		}
		
		FurtherInformation furtherInformation = new FurtherInformation();
		furtherInformation.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return furtherInformation;
	}
	
	/**
	 * Creates a FurtherInformation SMC object from a map without validation
	 * 
	 * @param obj a FurtherInformation SMC object as raw map
	 * @return a FurtherInformation SMC object, that behaves like a facade for the given map
	 */
	private static FurtherInformation createAsFacadeNonStrict(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		FurtherInformation furtherInformation = new FurtherInformation();
		furtherInformation.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return furtherInformation;
	}
	
	/**
	 * Check whether all mandatory elements for FurtherInformation SMC
	 * exist in the map
	 * 
	 * @param obj
	 * 
	 * @return true/false
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValid(Map<String, Object> obj) {
		FurtherInformation furtherInformation = createAsFacadeNonStrict(obj);
		
		return SubmodelElementCollection.isValid(obj)
				&& Property.isValid((Map<String, Object>) furtherInformation.getValidDate());
	}
	
	/**
	 * Sets statement by the manufacturer in text form, 
	 * e.g. scope of validity of the statements, scopes of application, 
	 * conditions of operation.
	 * 
	 * Note: Whenever possible, a multi-language definition is preferred.
	 * 
	 * @param statements
	 */
	public void setTextStatements(List<MultiLanguageProperty> statements) {
		if (statements != null && statements.size() > 0) {
			for (MultiLanguageProperty statement: statements) {
				addSubmodelElement(statement);
			}
		}
	}
	
	/**
	 * Gets statement by the manufacturer in text form, 
	 * e.g. scope of validity of the statements, scopes of application, 
	 * conditions of operation.
	 * 
	 * Note: Whenever possible, a multi-language definition is preferred.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<IMultiLanguageProperty> getStatements() {
		List<IMultiLanguageProperty> ret = new ArrayList<IMultiLanguageProperty>();
		List<ISubmodelElement> elements = SubmodelElementRetrievalHelper.getSubmodelElementsByIdPrefix(TEXTSTATEMENTPREFIX, getSubmodelElements());
		
		for (ISubmodelElement element: elements) {
			ret.add(MultiLanguageProperty.createAsFacade((Map<String, Object>) element));
		}
		return ret;
	}
	
	/**
	 * Sets a date on which the data specified in the Submodel was valid from for the associated asset.
	 * 
	 * Note: Often this date will be the date of the last update of the 
	 * corresponding data, that are the source for the technical properties 
	 * section in the master data system.
	 * 
	 * @param validDate
	 */
	public void setValidDate(Property validDate) {
		addSubmodelElement(validDate);
	}
	
	/**
	 * Sets a date on which the data specified in the Submodel was valid from for the associated asset.
	 * 
	 * Note: Often this date will be the date of the last update of the 
	 * corresponding data, that are the source for the technical properties 
	 * section in the master data system.
	 * 
	 * @param validDate
	 */
	public void setValidDate(XMLGregorianCalendar validDate) {
		Property validDateProp = new Property(VALIDDATEID, ValueType.DateTime);
		validDateProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/ZVEI/TechnicalData/ValidDate/1/1", IdentifierType.IRI)));
		validDateProp.setValue(validDate);
		setValidDate(validDateProp);
	}
	
	/**
	 * Gets a date on which the data specified in the Submodel was valid from for the associated asset.
	 * 
	 * Note: Often this date will be the date of the last update of the 
	 * corresponding data, that are the source for the technical properties 
	 * section in the master data system.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IProperty getValidDate() {
		return Property.createAsFacade((Map<String, Object>) getSubmodelElement(VALIDDATEID));
	}
}
