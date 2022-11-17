/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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


package org.eclipse.basyx.submodel.restapi.observing;

import java.util.Optional;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IQualifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;

/**
 * Helper class for working with the ObserableSubmodelAPIV2 feature
 * 
 * @author schnicke
 *
 */
public class ObserableSubmodelAPIV2Helper {

	public final static String EMPTYVALUEUPDATE_TYPE = "emptyValueUpdate";

	/**
	 * Creates a qualifier indicating that the annotated element's value should not be propagated via eventing
	 * @return
	 */
	public static Qualifier createSendEmptyValueQualifierEnabled() {
		return createSendEmptyValueQualifier(true);
	}

	/**
	 * Creates a qualifier indicating that the annotated element's value should be
	 * propagated via eventing <br>
	 * <b> Note: This is the default behavior, this annotation is not needed if you
	 * would like to use the feature as-is </b>
	 * 
	 * @return
	 */
	public static Qualifier createSendEmptyValueQualifierDisabled() {
		return createSendEmptyValueQualifier(false);
	}

	private static Qualifier createSendEmptyValueQualifier(boolean sendEmptyValue) {
		Qualifier emptyValueUpdateEvent = new Qualifier(EMPTYVALUEUPDATE_TYPE, ValueType.Boolean);
		emptyValueUpdateEvent.setValue(sendEmptyValue);

		return emptyValueUpdateEvent;
	}

	public static boolean shouldSendEmptyValue(ISubmodelElement submodelElement) {
		Optional<IQualifier> qualifier = submodelElement.getQualifiers().stream().filter(c -> c instanceof IQualifier)
				.map(IQualifier.class::cast).filter(q -> q.getType().equals(EMPTYVALUEUPDATE_TYPE)).findAny();
		if (qualifier.isEmpty())
			return false;

		return (boolean) qualifier.get().getValue();
	}
}
