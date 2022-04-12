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

package org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.technicalproperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.types.helper.SubmodelElementRetrievalHelper;

/**
 * TechnicalProperties as described in the Submodel Template AAS Technical Data Document
 * 
 * It is a submodel element collection which contains Individual characteristics that describe the product (industrial equipment) and its technical properties.
 * 
 * @author haque
 *
 */
public class TechnicalProperties extends SubmodelElementCollection {
	public static final String IDSHORT = "TechnicalProperties";
	public static final String MAINSECTIONPREFIX = "MainSection";
	public static final String SUBSECTIONPREFIX = "SubSection";
	public static final String SMENOTDESCRIBEDID = "https://admin-shell.io/SemanticIdNotAvailable/1/1";
	public static final String MAINSECTIONID = "https://admin-shell.io/ZVEI/TechnicalData/MainSection/1/1";
	public static final String SUBSECTIONID = "https://admin-shell.io/ZVEI/TechnicalData/SubSection/1/1";
	public static final Reference SEMANTICID = new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/ZVEI/TechnicalData/TechnicalProperties/1/1", KeyType.IRI));

	/**
	 * Constructor with default idShort
	 */
	public TechnicalProperties() {
		this(IDSHORT);
	}
	
	/**
	 * Constructor with mandatory attributes
	 * @param idShort
	 */
	public TechnicalProperties(String idShort) {
		super(idShort);
	}
	
	/**
	 * Creates a TechnicalProperties SMC object from a map
	 * 
	 * @param obj a TechnicalProperties SMC object as raw map
	 * @return a TechnicalProperties SMC object, that behaves like a facade for the given map
	 */
	public static TechnicalProperties createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(TechnicalProperties.class, obj);
		}
		
		TechnicalProperties technicalProperties = new TechnicalProperties();
		technicalProperties.setMap((Map<String, Object>)SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return technicalProperties;
	}
	
	/**
	 * Check whether all mandatory elements for TechnicalProperties SMC
	 * exist in the map
	 * 
	 * @param obj
	 * 
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		return SubmodelElementCollection.isValid(obj);
	}
	
	/**
	 * Sets arbitrary semanticId but defined in a classification system
	 * 
	 * Arbitrary SubmodelElement with semanticId possibly referring to a ConceptDescription can be used within the Technical Properties.
	 * 
	 * @param elements
	 */
	public void setArbitrary(List<SubmodelElement> elements) {
		if (elements != null && elements.size() > 0) {
			for (SubmodelElement elem: elements) {
				addSubmodelElement(elem);	
			}
		}
	}
	
	/**
	 * Gets arbitrary semanticId but defined in a classification system
	 * 
	 * Arbitrary SubmodelElement with semanticId possibly referring to a ConceptDescription can be used within the Technical Properties.
	 *
	 * @return
	 */
	public List<ISubmodelElement> getArbitrary() {
		return getSubmodelElements()
				.values().stream()
				.filter(x -> {
					String id = x.getSemanticId().getKeys().get(0).getValue();
					if (id.equals(MAINSECTIONID) || id.equals(SUBSECTIONID) || id.equals(SMENOTDESCRIBEDID)) {
						return false;
					} else {
						return true;
					}
				})
				.collect(Collectors.toList());
	}
	
	/**
	 * Set arbitrary
	 * 
	 * Represents a SubmodelElement that is not described using a common classification system, a consortium specification, an open community standard, a published manufacturer specification or such.
	 * 
	 * Note: The idShort of the SubmodelElement can be named accordingly. Constraints concerning the usable characters for idShort shall be respected.
	 * Note: Only perceivable by human understanding.
	 * Note: The special case of SME being a SMC is accepted, will be rendered as MainSection/ SubSection accordingly.
	 * @param elements
	 */
	public void setSMENotDescribedBySemanticId(List<SubmodelElement> elements) {
		if (elements != null && elements.size() > 0) {
			for (SubmodelElement elem: elements) {
				addSubmodelElement(elem);	
			}
		}
	}
	
	/**
	 * Get arbitrary
	 * Represents a SubmodelElement that is not described using a common classification system, a consortium specification, an open community standard, a published manufacturer specification or such.
	 * 
	 * Note: The idShort of the SubmodelElement can be named accordingly. Constraints concerning the usable characters for idShort shall be respected.
	 * Note: Only perceivable by human understanding.
	 * Note: The special case of SME being a SMC is accepted, will be rendered as MainSection/ SubSection accordingly.
	 * @return
	 */
	public List<ISubmodelElement> getSMENotDescribedBySemanticId() {
		return getSubmodelElements()
				.values().stream()
				.filter(x -> x.getSemanticId().getKeys().get(0).getValue()
						.equals(SMENOTDESCRIBEDID))
				.collect(Collectors.toList());
	}
	
	/**
	 * Sets main subdivision possibility for properties.
	 * 
	 * Note: Each Main Section SMC may contain arbitray sets of SubmodelElements, SemanticIdNotAvailable, SubSection.
	 * @param mainSections
	 */
	public void setMainSections(List<SubmodelElementCollection> mainSections) {
		if (mainSections != null && mainSections.size() > 0) {
			for (SubmodelElementCollection section: mainSections) {
				addSubmodelElement(section);	
			}
		}
	}
	
	/**
	 * Gets main subdivision possibility for properties.
	 * 
	 * Note: Each Main Section SMC may contain arbitray sets of SubmodelElements, SemanticIdNotAvailable, SubSection.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ISubmodelElementCollection> getMainSections() {
		List<ISubmodelElementCollection> ret = new ArrayList<ISubmodelElementCollection>();
		List<ISubmodelElement> elements = SubmodelElementRetrievalHelper.getSubmodelElementsByIdPrefix(MAINSECTIONPREFIX, getSubmodelElements());
		
		for (ISubmodelElement element: elements) {
			ret.add(SubmodelElementCollection.createAsFacade((Map<String, Object>) element));
		}
		return ret;
	}
	
	/**
	 * Sets subordinate subdivision possibility for properties.
	 * 
	 * Note: Each Sub Section SMC may contain arbitray sets of SubmodelElements, SemanticIdNotAvailable, SubSection.
	 * @param subSections
	 */
	public void setSubSections(List<SubmodelElementCollection> subSections) {
		if (subSections != null && subSections.size() > 0) {
			for (SubmodelElementCollection section: subSections) {
				addSubmodelElement(section);	
			}
		}
	}
	
	/**
	 * Gets subordinate subdivision possibility for properties.
	 * 
	 * Note: Each Sub Section SMC may contain arbitray sets of SubmodelElements, SemanticIdNotAvailable, SubSection.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ISubmodelElementCollection> getSubSections() {
		List<ISubmodelElementCollection> ret = new ArrayList<ISubmodelElementCollection>();
		List<ISubmodelElement> elements = SubmodelElementRetrievalHelper.getSubmodelElementsByIdPrefix(SUBSECTIONPREFIX, getSubmodelElements());
		
		for (ISubmodelElement element: elements) {
			ret.add(SubmodelElementCollection.createAsFacade((Map<String, Object>) element));
		}
		return ret;
	}
}
