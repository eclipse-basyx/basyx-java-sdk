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

import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;

/**
 * A property that has a multi language value.
 * 
 * @author schnicke
 *
 */
public interface IMultiLanguageProperty extends IDataElement {

	/**
	 * Gets the {@link LangStrings} value of the property instance
	 * 
	 * @return
	 */
	@Override
	LangStrings getValue();

	/**
	 * Sets the {@link LangStrings} value of the property instance
	 * 
	 * @param value
	 */
	void setValue(LangStrings value);

	/**
	 * Gets the reference to the global unique id of a coded value.
	 * 
	 * @return
	 */
	IReference getValueId();
}
