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
package org.eclipse.basyx.submodel.metamodel.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.IElementContainer;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IEmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IAdministrativeInformation;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.facade.ElementContainerValuesHelper;
import org.eclipse.basyx.submodel.metamodel.map.helper.ElementContainerHelper;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.AdministrativeInformation;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.haskind.HasKind;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifiable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * A submodel defines a specific aspect of the asset represented by the AAS.
 * <br>
 * <br>
 * A submodel is used to structure the digital representation and technical
 * functionality of an Administration Shell into distinguishable parts. Each
 * submodel refers to a well-defined domain or subject matter. Submodels can
 * become standardized and thus become submodels types. Submodels can have
 * different life-cycles.
 * 
 * @author kuhn, schnicke
 *
 *
 */
public class Submodel extends VABModelMap<Object> implements IElementContainer, ISubmodel {

	public static final String SUBMODELELEMENT = "submodelElements";
	public static final String MODELTYPE = "Submodel";

	/**
	 * Constructor
	 */
	public Submodel() {
		// Add qualifiers
		putAll(new Identifiable());
		putAll(new HasDataSpecification());

		// Add model type
		putAll(new ModelType(MODELTYPE));
		setModelingKind(ModelingKind.INSTANCE);

		put(SUBMODELELEMENT, new LinkedHashMap<String, ISubmodelElement>());

	}

	/**
	 * Constructor accepting only mandatory attribute
	 * 
	 * @param idShort
	 * @param identification
	 */
	public Submodel(String idShort, IIdentifier identification) {
		this();
		setIdentification(identification);
		setIdShort(idShort);
	}

	/**
	 * Constructor
	 */
	public Submodel(HasSemantics semantics, Identifiable identifiable, Qualifiable qualifiable, HasDataSpecification specification, HasKind hasKind) {
		this();
		// Add qualifiers
		putAll(semantics);
		putAll(identifiable);
		putAll(qualifiable);
		putAll(specification);
		putAll(hasKind);

		// Attributes
		put(SUBMODELELEMENT, new LinkedHashMap<String, ISubmodelElement>());
	}

	/**
	 * Constructor
	 */
	public Submodel(List<Property> properties) {
		this();
		properties.forEach(this::addSubmodelElement);
	}

	/**
	 * Constructor
	 */
	public Submodel(List<Property> properties, List<Operation> operations) {
		this();
		properties.forEach(this::addSubmodelElement);
		operations.forEach(this::addSubmodelElement);
	}

	public static Submodel createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		if (!isValid(map)) {
			throw new MetamodelConstructionException(Submodel.class, map);
		}

		if (!map.containsKey(SUBMODELELEMENT)) {
			map.put(SUBMODELELEMENT, new ArrayList<>());
		}

		return SubmodelElementMapCollectionConverter.mapToSM(map);
	}

	/**
	 * Check whether all mandatory elements for the metamodel exist in a map
	 * 
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> map) {
		return Identifiable.isValid(map);
	}

	@Override
	public IReference getSemanticId() {
		return HasSemantics.createAsFacade(this).getSemanticId();
	}

	public void setSemanticId(IReference ref) {
		HasSemantics.createAsFacade(this).setSemanticId(ref);
	}

	@Override
	public IAdministrativeInformation getAdministration() {
		return Identifiable.createAsFacade(this, getKeyElement()).getAdministration();
	}

	@Override
	public IIdentifier getIdentification() {
		return Identifiable.createAsFacade(this, getKeyElement()).getIdentification();
	}

	public void setAdministration(AdministrativeInformation information) {
		Identifiable.createAsFacade(this, getKeyElement()).setAdministration(information);
	}

	public void setIdentification(IIdentifier id) {
		setIdentification(id.getIdType(), id.getId());
	}

	public void setIdentification(IdentifierType idType, String id) {
		Identifiable.createAsFacadeNonStrict(this, getKeyElement()).setIdentification(idType, id);
	}

	@Override
	public Collection<IReference> getDataSpecificationReferences() {
		return HasDataSpecification.createAsFacade(this).getDataSpecificationReferences();
	}

	public void setDataSpecificationReferences(Collection<IReference> ref) {
		HasDataSpecification.createAsFacade(this).setDataSpecificationReferences(ref);
	}

	@Override
	public Collection<IEmbeddedDataSpecification> getEmbeddedDataSpecifications() {
		return HasDataSpecification.createAsFacade(this).getEmbeddedDataSpecifications();
	}

	public void setEmbeddedDataSpecifications(Collection<IEmbeddedDataSpecification> embeddedDataSpecifications) {
		HasDataSpecification.createAsFacade(this).setEmbeddedDataSpecifications(embeddedDataSpecifications);
	}

	/**
	 * @deprecated Please use {@link #getKind()} instead.
	 */
	@Override
	public ModelingKind getModelingKind() {
		return this.getKind();
	}

	/**
	 * @deprecated Please use {@link #setKind(ModelingKind)} instead.
	 */
	public void setModelingKind(ModelingKind kind) {
		this.setKind(kind);
	}

	@Override
	public ModelingKind getKind() {
		return HasKind.createAsFacade(this).getKind();
	}

	public void setKind(ModelingKind kind) {
		HasKind.createAsFacade(this).setKind(kind);
	}

	@Override
	public String getIdShort() {
		return Referable.createAsFacade(this, getKeyElement()).getIdShort();
	}

	public void setIdShort(String id) {
		Referable.createAsFacadeNonStrict(this, getKeyElement()).setIdShort(id);
	}

	public void setProperties(Map<String, IProperty> properties) {
		// first, remove all properties
		Set<Entry<String, ISubmodelElement>> elementSet = getSubmodelElements().entrySet();
		for (Iterator<Entry<String, ISubmodelElement>> iterator = elementSet.iterator(); iterator.hasNext();) {
			Entry<String, ISubmodelElement> entry = iterator.next();
			if (entry.getValue() instanceof IProperty) {
				iterator.remove();
			}
		}
		// then add all given data properties
		properties.values().forEach(this::addSubmodelElement);
	}

	public void setOperations(Map<String, IOperation> operations) {
		// first, remove all operations
		Set<Entry<String, ISubmodelElement>> elementSet = getSubmodelElements().entrySet();
		for (Iterator<Entry<String, ISubmodelElement>> iterator = elementSet.iterator(); iterator.hasNext();) {
			Entry<String, ISubmodelElement> entry = iterator.next();
			if (entry.getValue() instanceof IOperation) {
				iterator.remove();
			}
		}
		// then add all given operations
		operations.values().forEach(this::addSubmodelElement);
	}

	@Override
	public String getCategory() {
		return Referable.createAsFacade(this, getKeyElement()).getCategory();
	}

	@Override
	public LangStrings getDescription() {
		return Referable.createAsFacade(this, getKeyElement()).getDescription();
	}

	@Override
	public IReference getParent() {
		return Referable.createAsFacade(this, getKeyElement()).getParent();
	}

	public void setCategory(String category) {
		Referable.createAsFacade(this, getKeyElement()).setCategory(category);
	}

	public void setDescription(LangStrings description) {
		Referable.createAsFacade(this, getKeyElement()).setDescription(description);
	}

	public void setParent(IReference obj) {
		Referable.createAsFacade(this, getKeyElement()).setParent(obj);
	}

	private KeyElements getKeyElement() {
		return KeyElements.SUBMODEL;
	}

	@Override
	public void addSubmodelElement(ISubmodelElement element) {
		if (element instanceof SubmodelElement) {
			((SubmodelElement) element).setParent(getReference());
		}
		getSubmodelElements().put(element.getIdShort(), element);
	}

	@Override
	public Map<String, IProperty> getProperties() {
		Map<String, IProperty> properties = new LinkedHashMap<>();
		getSubmodelElements().values().forEach(e -> {
			if (e instanceof IProperty) {
				properties.put(e.getIdShort(), (IProperty) e);
			}
		});
		return properties;
	}

	@Override
	public Map<String, IOperation> getOperations() {
		Map<String, IOperation> operations = new LinkedHashMap<>();
		getSubmodelElements().values().forEach(e -> {
			if (e instanceof IOperation) {
				operations.put(e.getIdShort(), (IOperation) e);
			}
		});
		return operations;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ISubmodelElement> getSubmodelElements() {
		return (Map<String, ISubmodelElement>) get(SUBMODELELEMENT);
	}

	@Override
	public Map<String, Object> getValues() {
		return ElementContainerValuesHelper.getSubmodelValue(this);
	}

	@Override
	public Collection<IConstraint> getQualifiers() {
		return Qualifiable.createAsFacade(this).getQualifiers();
	}

	public void setQualifiers(Collection<IConstraint> qualifiers) {
		Qualifiable.createAsFacade(this).setQualifiers(qualifiers);
	}

	@Override
	public IReference getReference() {
		return Identifiable.createAsFacade(this, getKeyElement()).getReference();
	}

	/**
	 * Retrieves an element from element collection
	 * 
	 * @param id
	 * @return retrieved element
	 */
	@Override
	public ISubmodelElement getSubmodelElement(String id) {
		Map<String, ISubmodelElement> submodelElems = getSubmodelElements();
		return ElementContainerHelper.getElementById(submodelElems, id);
	}

	/**
	 * Deletes an element from element collection
	 * 
	 * @param id
	 */
	@Override
	public void deleteSubmodelElement(String id) {
		Map<String, ISubmodelElement> submodelElems = getSubmodelElements();
		ElementContainerHelper.removeElementById(submodelElems, id);
	}
}
