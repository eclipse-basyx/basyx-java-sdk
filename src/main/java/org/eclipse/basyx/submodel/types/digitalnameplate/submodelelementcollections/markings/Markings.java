/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.markings;

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
 * Markings as defined in the AAS Digital Nameplate Template document <br/>
 * It is a submodel element collection which contains a collection of product markings
 * 
 * Note: CE marking is declared as mandatory according to EU Machine Directive 2006/42/EC.
 * 
 * @author haque
 *
 */
public class Markings extends SubmodelElementCollection {
	public static final String IDSHORT = "Markings";
	public static final Reference SEMANTICID = new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/zvei/nameplate/1/0/Nameplate/Markings", KeyType.IRI));
	public static final String MARKINGPREFIX = "Marking";
	
	private Markings() {
	}
	
	/**
	 * Constructor with default idShort
	 * @param markings
	 */
	public Markings(List<Marking> markings) {
		this(IDSHORT, markings);
	}
	
	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 * @param markings
	 */
	public Markings(String idShort, List<Marking> markings) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setMarking(markings);
	}
	
	/**
	 * Creates a Markings SMC object from a map
	 * 
	 * @param obj a Markings SMC object as raw map
	 * @return a Markings SMC object, that behaves like a facade for the given map
	 */
	public static Markings createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(Markings.class, obj);
		}
		
		Markings markings = new Markings();
		markings.setMap((Map<String, Object>)SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return markings;
	}
	
	/**
	 * Creates a Markings SMC object from a map without validation
	 * 
	 * @param obj a Markings SMC object as raw map
	 * @return a Markings SMC object, that behaves like a facade for the given map
	 */
	private static Markings createAsFacadeNonStrict(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		Markings markings = new Markings();
		markings.setMap((Map<String, Object>)SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return markings;
	}
	
	/**
	 * Check whether all mandatory elements for Markings SMC
	 * exist in the map
	 * 
	 * @param obj
	 * 
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		Markings markings = createAsFacadeNonStrict(obj);
		
		if (SubmodelElementCollection.isValid(obj)
				&& markings.getMarking() != null
				&& markings.getMarking().size() > 0) {
			for (Marking marking : markings.getMarking()) {
				if (!Marking.isValid((Map<String, Object>) marking)) {
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
	 * Sets information about the marking labelled on the device

     * Note: CE marking is declared as mandatory according to EU Machine
     * Directive 2006/42/EC.
	 * @param markingName
	 */
	public void setMarking(List<Marking> markings) {
		if (markings != null && markings.size() > 0) {
			for (Marking prop : markings) {
				addSubmodelElement(prop);
			}
		}
	}
	
	/**
	 * Gets information about the marking labelled on the device

     * Note: CE marking is declared as mandatory according to EU Machine
     * Directive 2006/42/EC.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Marking> getMarking() {
		List<Marking> ret = new ArrayList<Marking>();
		List<ISubmodelElement> elements = SubmodelElementRetrievalHelper.getSubmodelElementsByIdPrefix(MARKINGPREFIX, getSubmodelElements());
		
		for (ISubmodelElement element: elements) {
			ret.add(Marking.createAsFacade((Map<String, Object>) element));
		}
		return ret;
	}
}
