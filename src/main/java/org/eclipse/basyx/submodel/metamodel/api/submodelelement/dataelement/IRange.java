/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.RangeValue;

/**
 * A range data element is a data element that defines a range with min and max.
 * 
 * @author schnicke
 *
 */
public interface IRange extends IDataElement {
	/**
	 * Returns data type of the min und max
	 * 
	 * @return
	 */
	ValueType getValueType();

	/**
	 * Returns the minimum value of the range. <br />
	 * <br />
	 * If the min value is missing then the value is assumed to be negative
	 * infinite.
	 * 
	 * @return
	 */
	Object getMin();

	/**
	 * The maximum value of the range. <br />
	 * <br />
	 * If the max value is missing then the value is assumed to be positive
	 * infinite.
	 */
	Object getMax();

	@Override
	RangeValue getValue();

	/**
	 * Sets the value of the range represented by the Range submodel element
	 * 
	 * @param value
	 *            the range
	 */
	void setValue(RangeValue value);
}
