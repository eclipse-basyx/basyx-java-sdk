/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IRange;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueTypeHelper;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.RangeValue;
import org.eclipse.basyx.submodel.restapi.MultiSubmodelElementProvider;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * "Connected" implementation of IRange
 * @author conradi
 *
 */
public class ConnectedRange extends ConnectedDataElement implements IRange {

	public ConnectedRange(VABElementProxy proxy) {
		super(proxy);
	}

	@Override
	public ValueType getValueType() {
		return ValueTypeHelper.readTypeDef(getElem().getPath(Range.VALUETYPE));
	}

	@Override
	public Object getMin() {
		Object min = getElemLive().getPath(Range.MIN);
		return ValueTypeHelper.getJavaObject(min, getValueType());
	}

	@Override
	public Object getMax() {
		Object max = getElemLive().getPath(Range.MAX);
		return ValueTypeHelper.getJavaObject(max, getValueType());
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
			RangeValue rangeValue = RangeValue.createAsFacade((Map<String, Object>) value);
			setValue(rangeValue);
		} else {
			throw new IllegalArgumentException("Given object " + value + " is not a RangeValue");
		}
	}

	@Override
	public Range getLocalCopy() {
		return Range.createAsFacade(getElem()).getLocalCopy();
	}

	@Override
	public void setValue(RangeValue rangeValue) {
		Object minRaw = rangeValue.getMin();
		Object maxRaw = rangeValue.getMax();

		RangeValue prepared = new RangeValue(
				ValueTypeHelper.prepareForSerialization(minRaw),
				ValueTypeHelper.prepareForSerialization(maxRaw)
			);
				
		getProxy().setValue(MultiSubmodelElementProvider.VALUE, prepared);
	}
}
