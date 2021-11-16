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

import java.util.Collection;
import java.util.Map;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IMultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * "Connected" implementation of IMultiLanguageProperty
 * @author conradi
 *
 */
public class ConnectedMultiLanguageProperty extends ConnectedDataElement implements IMultiLanguageProperty {

	public ConnectedMultiLanguageProperty(VABElementProxy proxy) {
		super(proxy);
	}

	@SuppressWarnings("unchecked")
	@Override
	public LangStrings getValue() {
		return LangStrings.createAsFacade((Collection<Map<String, Object>>) super.getValue()) ;
	}

	@Override
	@SuppressWarnings("unchecked")
	public IReference getValueId() {
		return Reference.createAsFacade((Map<String, Object>) getElem().getPath(MultiLanguageProperty.VALUEID));
	}

	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.MULTILANGUAGEPROPERTY;
	}

	@Override
	public MultiLanguageProperty getLocalCopy() {
		return MultiLanguageProperty.createAsFacade(getElem()).getLocalCopy();
	}

	@Override
	public void setValue(LangStrings value) {
		setValue((Object) value);
	}
}
