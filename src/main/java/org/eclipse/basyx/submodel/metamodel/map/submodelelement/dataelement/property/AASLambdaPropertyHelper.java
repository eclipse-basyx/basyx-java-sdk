/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueTypeHelper;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProviderHelper;

/**
 * Supports using Properties in combination with {@link VABLambdaProvider}
 * 
 * @author schnicke
 *
 */
public class AASLambdaPropertyHelper {
	/**
	 * Sets the correct values in the passed property to use the lambda functions
	 * 
	 * @param property
	 * @param get
	 * @param set
	 * @return the passed property with updated configuration
	 */
	public static Property setLambdaValue(Property property, Supplier<Object> get, Consumer<Object> set) {
		Object newValue = VABLambdaProviderHelper.createSimple(get, set);
		ValueType newType = ValueTypeHelper.getType(get.get());
		property.set(newValue, newType);
		return property;
	}
}
