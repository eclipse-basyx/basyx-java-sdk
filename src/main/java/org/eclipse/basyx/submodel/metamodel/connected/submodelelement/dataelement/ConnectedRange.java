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
 * 
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
		if (RangeValue.isRangeValue(value)) {
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

		RangeValue prepared = new RangeValue(ValueTypeHelper.prepareForSerialization(minRaw), ValueTypeHelper.prepareForSerialization(maxRaw));

		getProxy().setValue(MultiSubmodelElementProvider.VALUE, prepared);
	}
}
