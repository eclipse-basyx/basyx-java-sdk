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
package org.eclipse.basyx.submodel.metamodel.map.submodelelement.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.entity.EntityType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.entity.IEntity;
import org.eclipse.basyx.submodel.metamodel.facade.submodelelement.SubmodelElementFacadeFactory;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;

/**
 * A entity element as defined in DAAS document
 * 
 * @author conradi
 *
 */
public class Entity extends SubmodelElement implements IEntity {

	public static final String MODELTYPE = "Entity";
	public static final String STATEMENT = "statements";
	public static final String ENTITY_TYPE = "entityType";
	public static final String ASSET = "asset";

	public Entity() {
		// Add model type
		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * Constructor accepting only mandatory attribute
	 * 
	 * @param idShort
	 * @param entityType
	 */
	public Entity(String idShort, EntityType entityType) {
		super(idShort);
		setEntityType(entityType);
		setStatements(new HashSet<>());

		// Add model type
		putAll(new ModelType(MODELTYPE));
	}

	public Entity(EntityType entityType, Collection<ISubmodelElement> statements, IReference asset) {
		this();
		setEntityType(entityType);
		setStatements(statements);
		setAsset(asset);
	}

	public void setStatements(Collection<ISubmodelElement> statements) {
		put(STATEMENT, statements);
	}

	public void setAsset(IReference asset) {
		put(ASSET, asset);
	}

	public void setEntityType(EntityType entityType) {
		put(ENTITY_TYPE, entityType.toString());
	}

	public static boolean isEntity(Map<String, Object> map) {
		String modelType = ModelType.createAsFacade(map).getName();
		// Either model type is set or the element type specific attributes are
		// contained (fallback)
		return MODELTYPE.equals(modelType) || (modelType == null && map.containsKey(Entity.STATEMENT));
	}

	/**
	 * Creates an Entity object from a map
	 * 
	 * @param obj
	 *            an Entity object as raw map
	 * @return an Entity object, that behaves like a facade for the given map
	 */
	public static Entity createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		if (!isValid(obj)) {
			throw new MetamodelConstructionException(Entity.class, obj);
		}

		Entity facade = new Entity();
		facade.setMap(obj);
		return facade;
	}

	/**
	 * Check whether all mandatory elements for the metamodel exist in a map
	 * 
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		return SubmodelElement.isValid(obj) && obj.containsKey(ENTITY_TYPE);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<ISubmodelElement> getStatements() {
		Collection<ISubmodelElement> ret = new ArrayList<>();
		Collection<Object> smElems = (Collection<Object>) get(STATEMENT);
		for (Object smElemO : smElems) {
			Map<String, Object> smElem = (Map<String, Object>) smElemO;
			ret.add(SubmodelElementFacadeFactory.createSubmodelElement(smElem));
		}
		return ret;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.fromString((String) get(ENTITY_TYPE));
	}

	@Override
	@SuppressWarnings("unchecked")
	public IReference getAsset() {
		return Reference.createAsFacade((Map<String, Object>) get(ASSET));
	}

	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.ENTITY;
	}

	@Override
	public EntityValue getValue() {
		return new EntityValue(getStatements(), getAsset());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		if (EntityValue.isEntityValue(value)) {
			EntityValue ev = EntityValue.createAsFacade((Map<String, Object>) value);
			put(Entity.STATEMENT, ev.getStatement());
			put(Entity.ASSET, ev.getAsset());
		} else {
			throw new IllegalArgumentException("Given Object is not an EntityValue");
		}
	}

	@Override
	public Entity getLocalCopy() {
		// Return a shallow copy
		Entity copy = new Entity();
		copy.putAll(this);
		return copy;
	}
}
