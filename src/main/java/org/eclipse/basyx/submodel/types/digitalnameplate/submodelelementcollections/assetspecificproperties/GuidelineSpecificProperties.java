/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.assetspecificproperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;

/**
 * GuidelineSpecificProperties as defined in the AAS Digital Nameplate Template document <br>
 * It is a submodel element collection which contains Asset specific nameplate 
 * information required by guideline, stipulation or legislation.
 * 
 * @author haque
 *
 */
public class GuidelineSpecificProperties extends SubmodelElementCollection {
	public static final String GUIDELINEFORCONFORMITYDECLARATIONID = "GuidelineForConformityDeclaration";
	public static final Reference SEMANTICID = new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://adminshell.io/zvei/nameplate/1/0/Nameplate/AssetSpecificProperties/GuidelineSpecificProperties", KeyType.IRI));
	
	private GuidelineSpecificProperties() {
	}
	
	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 * @param declaration
	 * @param arbitrary
	 */
	public GuidelineSpecificProperties(String idShort, Property declaration, List<Property> arbitrary) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setGuidelineForConformityDeclaration(declaration);
		setArbitrary(arbitrary);
	}
	
	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 * @param declaration
	 * @param arbitrary
	 */
	public GuidelineSpecificProperties(String idShort, String declaration, List<Property> arbitrary) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setGuidelineForConformityDeclaration(declaration);
		setArbitrary(arbitrary);
	}
	
	/**
	 * Creates a GuidelineSpecificProperties SMC object from a map
	 * 
	 * @param obj a GuidelineSpecificProperties SMC object as raw map
	 * @return a GuidelineSpecificProperties SMC object, that behaves like a facade for the given map
	 */
	public static GuidelineSpecificProperties createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(GuidelineSpecificProperties.class, obj);
		}
		
		GuidelineSpecificProperties guidelineSpecificProperties = new GuidelineSpecificProperties();
		guidelineSpecificProperties.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return guidelineSpecificProperties;
	}
	
	/**
	 * Creates a GuidelineSpecificProperties SMC object from a map without validation
	 * 
	 * @param obj a GuidelineSpecificProperties SMC object as raw map
	 * @return a GuidelineSpecificProperties SMC object, that behaves like a facade for the given map
	 */
	private static GuidelineSpecificProperties createAsFacadeNonStrict(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		GuidelineSpecificProperties guidelineSpecificProperties = new GuidelineSpecificProperties();
		guidelineSpecificProperties.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return guidelineSpecificProperties;
	}
	
	/**
	 * Check whether all mandatory elements for GuidelineSpecificProperties SMC
	 * exist in the map
	 * 
	 * @param obj
	 * 
	 * @return true/false
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValid(Map<String, Object> obj) {
		GuidelineSpecificProperties props = createAsFacadeNonStrict(obj);
		
		if (SubmodelElementCollection.isValid(obj)
				&& Property.isValid((Map<String, Object>) props.getGuidelineForConformityDeclaration())
				&& props.getArbitrary() != null
				&& props.getArbitrary().size() > 0) {
			for (IProperty arbitrary: props.getArbitrary()) {
				if (!Property.isValid((Map<String, Object>) arbitrary)) {
					return false;
				}	
			}
			return true;
		}
		else {
			return false;	
		}
	}
	
	/**
	 * Sets guideline, stipulation or legislation used for determining conformity
	 * 
	 * @param declaration {@link Property}
	 */
	public void setGuidelineForConformityDeclaration(Property declaration) {
		addSubmodelElement(declaration);
	}
	
	/**
	 * Sets guideline, stipulation or legislation used for determining conformity
	 * 
	 * @param declaration {@link String}
	 */
	public void setGuidelineForConformityDeclaration(String declaration) {
		Property declarationProp = new Property(GUIDELINEFORCONFORMITYDECLARATIONID, ValueType.String);
		declarationProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO856#002", IdentifierType.IRDI)));
		declarationProp.setValue(declaration);
		setGuidelineForConformityDeclaration(declarationProp);
	}
	
	/**
	 * Gets guideline, stipulation or legislation used for determining conformity
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IProperty getGuidelineForConformityDeclaration() {
		return Property.createAsFacade((Map<String, Object>) getSubmodelElement(GUIDELINEFORCONFORMITYDECLARATIONID));
	}
	
	/**
	 * Gets arbitrary, representing information required by further standards
	 * @return
	 */
	public List<IProperty> getArbitrary() {
		List<IProperty> ret = new ArrayList<IProperty>();
		Map<String, ISubmodelElement> elemMap = getSubmodelElements();
		if (elemMap != null && elemMap.size() > 0) {
			for (Map.Entry<String, ISubmodelElement> singleElement: elemMap.entrySet()) {
				if (!singleElement.getKey().equals(GUIDELINEFORCONFORMITYDECLARATIONID)) {
					ret.add((IProperty) singleElement.getValue());
				}
			}
		}
		return ret;
	}

	/**
	 * Sets arbitrary, representing information required by further standards
	 * @param arbitraries
	 */
	public void setArbitrary(List<Property> arbitraries) {
		if (arbitraries != null & arbitraries.size() > 0) {
			for (Property prop : arbitraries) {
				addSubmodelElement(prop);
			}
		}
	}
}
