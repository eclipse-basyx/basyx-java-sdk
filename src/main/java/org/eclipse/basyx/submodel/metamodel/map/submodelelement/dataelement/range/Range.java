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
package org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IRange;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.DataElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueTypeHelper;

/**
 * A range element as defined in DAAS document
 * 
 * @author conradi
 *
 */
public class Range extends DataElement implements IRange {

	public static final String MODELTYPE = "Range";
	public static final String VALUETYPE = "valueType";
	public static final String MIN = "min";
	public static final String MAX = "max";
	

	public Range() {
		// Add model type
		putAll(new ModelType(MODELTYPE));
	}
	
	public Range(ValueType valueType) {
		this();
		setValueType(valueType);
	}

	/**
	 * Constructor accepting only mandatory attribute
	 * @param idShort
	 * @param valueType
	 */
	public Range(String idShort, ValueType valueType) {
		super(idShort);
		// Add model type
		putAll(new ModelType(MODELTYPE));
		setValueType(valueType);
	}
	
	public Range(ValueType valueType, Object min, Object max) {
		this(valueType);
		put(MIN, min);
		put(MAX, max);
	}
	
	/**
	 * Creates a Range object from a map
	 * 
	 * @param obj a Range object as raw map
	 * @return a Range object, that behaves like a facade for the given map
	 */
	public static Range createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(Range.class, obj);
		}
		
		Range facade = new Range();
		facade.setMap(obj);
		return facade;
	}
	
	/**
	 * Check whether all mandatory elements for the metamodel
	 * exist in a map
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		return DataElement.isValid(obj) && obj.containsKey(VALUETYPE);
	}
	
	/**
	 * Returns true if the given submodel element map is recognized as a Range element
	 */
	public static boolean isRange(Map<String, Object> map) {
		String modelType = ModelType.createAsFacade(map).getName();
		// Either model type is set or the element type specific attributes are contained (fallback)
		return MODELTYPE.equals(modelType)
				|| (modelType == null && (map.containsKey(MIN) && map.containsKey(MAX) && map.containsKey(VALUETYPE)));
	}

	private void setValueType(ValueType valueType) {
		put(Range.VALUETYPE, valueType.toString());
	}

	@Override
	public ValueType getValueType() {
		return ValueTypeHelper.readTypeDef(get(Range.VALUETYPE));
	}

	@Override
	public Object getMin() {
		Object value = get(MIN);
		if(value instanceof String) {
			return ValueTypeHelper.getJavaObject(value, getValueType());
		}else {
			return value;
		}
	}

	@Override
	public Object getMax() {
		Object value = get(MAX);
		if(value instanceof String) {
			return ValueTypeHelper.getJavaObject(value, getValueType());
		}else {
			return value;
		}
	}
	
	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.RANGE;
	}

	@Override
	public RangeValue getValue() {
		return new RangeValue(getMin(), getMax());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		if(RangeValue.isRangeValue(value)) {
			RangeValue rv = RangeValue.createAsFacade((Map<String, Object>) value);
			setValue(rv);
		} else {
			throw new IllegalArgumentException("Given Object is not a RangeValue");
		}
	}

	@Override
	public void setValue(RangeValue rv) {
		Object minValue = rv.getMin();
		Object maxValue = rv.getMax();

		put(Range.MIN, ValueTypeHelper.prepareForSerialization(minValue));
		put(Range.MAX, ValueTypeHelper.prepareForSerialization(maxValue));
		if (getValueType() == null) {
			setValueType(ValueTypeHelper.getType(minValue));
		}
	}

	@Override
	public Range getLocalCopy() {
		// Return a shallow copy
		Range copy = new Range();
		copy.putAll(this);
		return copy;
	}
}
