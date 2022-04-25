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
package org.eclipse.basyx.submodel.metamodel.map.submodelelement;

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IEmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.SubmodelElementIdShortBlacklist;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.haskind.HasKind;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifiable;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * SubmodelElement as defined in "Details of the Asset Administration Shell"
 * 
 * @author schnicke
 *
 */
public class SubmodelElement extends VABModelMap<Object> implements ISubmodelElement {
	public static final String MODELTYPE = "SubmodelElement";

	protected SubmodelElement() {
		// Add model type
		putAll(new ModelType(MODELTYPE));
		setModelingKind(ModelingKind.INSTANCE);
	}

	/**
	 * Constructor with only mandatory attribute
	 * 
	 * @param idShort
	 */
	protected SubmodelElement(String idShort) {
		this();
		setIdShort(idShort);
	}

	/**
	 * Creates a SubmodelElement object from a map
	 * 
	 * @param obj
	 *            a SubmodelElement object as raw map
	 * @return a SubmodelElement object, that behaves like a facade for the given
	 *         map
	 */
	public static SubmodelElement createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		if (!isValid(obj)) {
			throw new MetamodelConstructionException(SubmodelElement.class, obj);
		}

		SubmodelElement ret = new SubmodelElement();
		ret.setMap(obj);
		return ret;
	}

	/**
	 * Check whether all mandatory elements for the metamodel exist in a map
	 * 
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		return Referable.isValid(obj);
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

	@Override
	public String getIdShort() {
		return Referable.createAsFacade(this, getKeyElement()).getIdShort();
	}

	protected KeyElements getKeyElement() {
		return KeyElements.SUBMODELELEMENT;
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

	public void setIdShort(String idShort) {
		if (SubmodelElementIdShortBlacklist.isBlacklisted(idShort)) {
			throw new RuntimeException("The passed idShort " + idShort + " is blacklisted.");
		}

		Referable.createAsFacadeNonStrict(this, getKeyElement()).setIdShort(idShort);
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

	public void setQualifiers(Collection<IConstraint> qualifiers) {
		Qualifiable.createAsFacade(this).setQualifiers(qualifiers);
	}

	@Override
	public Collection<IConstraint> getQualifiers() {
		return Qualifiable.createAsFacade(this).getQualifiers();
	}

	@Override
	public IReference getSemanticId() {
		return HasSemantics.createAsFacade(this).getSemanticId();
	}

	public void setSemanticId(IReference ref) {
		HasSemantics.createAsFacade(this).setSemanticId(ref);
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
	@SuppressWarnings("unchecked")
	public String getModelType() {
		return (String) ((Map<String, Object>) get(ModelType.MODELTYPE)).get(ModelType.NAME);
	}

	@Override
	public IReference getReference() {
		return Referable.createAsFacade(this, getKeyElement()).getReference();
	}

	@Override
	public Object getValue() {
		throw new UnsupportedOperationException("getValue is only possible in specific Element");
	}

	@Override
	public void setValue(Object value) {
		throw new UnsupportedOperationException("setValue is only possible in specific Element");
	}

	public SubmodelElement getLocalCopy() {
		// Return a shallow copy
		SubmodelElement copy = new SubmodelElement();
		copy.putAll(this);
		return copy;
	}
}
