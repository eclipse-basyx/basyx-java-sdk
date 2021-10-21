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
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.types.helper.SubmodelElementRetrievalHelper;

/**
 * AssetSpecificProperties as defined in the AAS Digital Nameplate Template document <br>
 * It is a submodel element collection which contains collection of guideline specific properties
 * 
 * @author haque
 *
 */
public class AssetSpecificProperties extends SubmodelElementCollection {
	public static final String ASSETSPECIFICPROPERTIESID = "AssetSpecificProperties";
	public static final String GUIDELINESPECIFICPROPERTYPREFIX = "GuidelineSpecificProperties";
	public static final Reference SEMANTICID = new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/zvei/nameplate/1/0/Nameplate/AssetSpecificProperties", KeyType.IRI));
	
	private AssetSpecificProperties() {
	}
	
	/**
	 * Constructor with default idShort
	 * @param guidelineSpecificProperties
	 */
	public AssetSpecificProperties(List<GuidelineSpecificProperties> guidelineSpecificProperties) {
		this (ASSETSPECIFICPROPERTIESID, guidelineSpecificProperties);
	}
	
	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 * @param guidelineSpecificProperties
	 */
	public AssetSpecificProperties(String idShort, List<GuidelineSpecificProperties> guidelineSpecificProperties) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setGuidelineSpecificProperties(guidelineSpecificProperties);
	}
	
	/**
	 * Creates a AssetSpecificProperties SMC object from a map
	 * 
	 * @param obj a AssetSpecificProperties SMC object as raw map
	 * @return a AssetSpecificProperties SMC object, that behaves like a facade for the given map
	 */
	public static AssetSpecificProperties createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(AssetSpecificProperties.class, obj);
		}
		
		AssetSpecificProperties assetSpecificProperties = new AssetSpecificProperties();
		assetSpecificProperties.setMap((Map<String, Object>)SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return assetSpecificProperties;
	}
	
	/**
	 * Creates a AssetSpecificProperties SMC object from a map without validation
	 * 
	 * @param obj a AssetSpecificProperties SMC object as raw map
	 * @return a AssetSpecificProperties SMC object, that behaves like a facade for the given map
	 */
	private static AssetSpecificProperties createAsFacadeNonStrict(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		AssetSpecificProperties assetSpecificProperties = new AssetSpecificProperties();
		assetSpecificProperties.setMap((Map<String, Object>)SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return assetSpecificProperties;
	}
	
	/**
	 * Check whether all mandatory elements for AssetSpecificProperties SMC
	 * exist in the map
	 * 
	 * @param obj
	 * 
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		AssetSpecificProperties props = createAsFacadeNonStrict(obj);
		
		if (SubmodelElementCollection.isValid(obj)
				&& props.getGuidelineSpecificProperties() != null
				&& props.getGuidelineSpecificProperties().size() > 0) {
			for (GuidelineSpecificProperties prop : props.getGuidelineSpecificProperties()) {
				if (!GuidelineSpecificProperties.isValid((Map<String, Object>) prop)) {
					return false;
				}
			}
			return true;
		} else {
			return false;	
		}
	}
	
	public void setGuidelineSpecificProperties(List<GuidelineSpecificProperties> properties) {
		if (properties != null & properties.size() > 0) {
			for (GuidelineSpecificProperties prop : properties) {
				addSubmodelElement(prop);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<GuidelineSpecificProperties> getGuidelineSpecificProperties() {
		List<GuidelineSpecificProperties> ret = new ArrayList<GuidelineSpecificProperties>();
		List<ISubmodelElement> elements = SubmodelElementRetrievalHelper.getSubmodelElementsByIdPrefix(GUIDELINESPECIFICPROPERTYPREFIX, getSubmodelElements());
		
		for (ISubmodelElement element: elements) {
			ret.add(GuidelineSpecificProperties.createAsFacade((Map<String, Object>) element));
		}
		return ret;
	}
}
