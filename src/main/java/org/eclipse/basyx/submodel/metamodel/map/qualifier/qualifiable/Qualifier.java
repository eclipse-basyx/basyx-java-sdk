/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IQualifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueTypeHelper;

/**
 * Qualifier class
 * 
 * @author kuhn
 *
 */
public class Qualifier extends Constraint implements IQualifier {
	public static final String QUALIFIER = "qualifier";

	public static final String TYPE = "type";

	public static final String VALUE = "value";

	public static final String VALUEID = "valueId";

	public static final String VALUETYPE = "valueType";

	public static final String MODELTYPE = "Qualifier";

	/**
	 * Constructor
	 */
	public Qualifier() {
		// Add model type
		putAll(new ModelType(MODELTYPE));
	}
	
	/**
	 * Constructor accepting mandatory attributes
	 * @param type
	 * @param valueType
	 */
	public Qualifier(String type, String valueType) {
		this(type, null, valueType, null);
	}

	public Qualifier(String type, String value, String valueType, Reference valueId) {
		put(TYPE,type);
		put(VALUE, ValueTypeHelper.prepareForSerialization(value));
		put(VALUEID, valueId);
		put(VALUETYPE, valueType);
		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * Creates a Qualifier object from a map
	 * 
	 * @param map
	 *            a Qualifier object as raw map
	 * @return a Qualifier object, that behaves like a facade for the given map
	 */
	public static Qualifier createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		
		if (!isValid(map)) {
			throw new MetamodelConstructionException(Qualifier.class, map);
		}
		
		Qualifier ret = new Qualifier();
		ret.setMap(map);
		return ret;
	}
	
	/**
	 * Check whether all mandatory elements for the metamodel
	 * exist in a map
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> map) {
		return map != null &&
				map.containsKey(TYPE) &&
				map.containsKey(VALUETYPE);
	}

	public void setType(String obj) {
		put(Qualifier.TYPE, obj);
	}

	@Override
	public String getType() {
		return (String) get(Qualifier.TYPE);
	}

	public void setValue(Object obj) {
		put(Qualifier.VALUE, ValueTypeHelper.prepareForSerialization(obj));
		// Value type is only set if it is not set before
		if(getValueType() == null) {
			put(Qualifier.VALUETYPE, ValueTypeHelper.getType(obj).toString());
		}
	}

	@Override
	public Object getValue() {
		Object value = get(Qualifier.VALUE);
		if(value instanceof String) {
			return ValueTypeHelper.getJavaObject(value, getValueType());
		}else {
			return value;
		}
	}

	public void setValueId(IReference obj) {
		put(Qualifier.VALUEID, obj);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IReference getValueId() {
		return Reference.createAsFacade((Map<String, Object>) get(Qualifier.VALUEID));
	}

	public void setValueType(ValueType obj) {
		put(Qualifier.VALUETYPE, obj.toString());
	}
	
	@Override
	public ValueType getValueType() {
		return ValueTypeHelper.readTypeDef(get(Qualifier.VALUETYPE));
	}

	@Override
	public IReference getSemanticId() {
		return HasSemantics.createAsFacade(this).getSemanticId();
	}

	public void setSemanticId(IReference ref) {
		HasSemantics.createAsFacade(this).setSemanticId(ref);
	}
}
