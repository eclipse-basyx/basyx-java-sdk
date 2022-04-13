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
package org.eclipse.basyx.submodel.metamodel.connected.submodelelement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * "Connected" implementation of SubmodelElementCollection
 * 
 * @author rajashek
 *
 */
public class ConnectedSubmodelElementCollection extends ConnectedSubmodelElement implements ISubmodelElementCollection {
	public ConnectedSubmodelElementCollection(VABElementProxy proxy) {
		super(proxy);
	}

	@Override
	public List<ISubmodelElement> getValue() {
		return Collections.unmodifiableList(new ArrayList<>(getSubmodelElements().values()));
	}

	@Override
	public boolean isOrdered() {
		return (boolean) getElem().getPath(SubmodelElementCollection.ORDERED);
	}

	@Override
	public boolean isAllowDuplicates() {
		return (boolean) getElem().getPath(SubmodelElementCollection.ALLOWDUPLICATES);
	}

	@Override
	public Map<String, ISubmodelElement> getSubmodelElements() {
		return ConnectedSubmodelElementFactory.getConnectedSubmodelElements(getProxy(), Property.VALUE, "");
	}

	@Override
	public Map<String, IProperty> getProperties() {
		return ConnectedSubmodelElementFactory.getProperties(getProxy(), Property.VALUE, "");
	}

	@Override
	public Map<String, IOperation> getOperations() {
		return ConnectedSubmodelElementFactory.getOperations(getProxy(), Property.VALUE, "");
	}

	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.SUBMODELELEMENTCOLLECTION;
	}

	/**
	 * Get submodel element by given id
	 * 
	 * @param id
	 * @return specific submodel element
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ISubmodelElement getSubmodelElement(String id) {
		Map<String, Object> node = (Map<String, Object>) getProxy().getValue(id);
		ISubmodelElement element = ConnectedSubmodelElementFactory.getConnectedSubmodelElement(getProxy(), "", id, node);
		return element;
	}

	/**
	 * Delete a submodel element by given id
	 * 
	 * @param id
	 */
	@Override
	public void deleteSubmodelElement(String id) {
		getProxy().deleteValue(id);
	}

	/**
	 * adds a submodel element to the collection
	 * 
	 * @param element
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addSubmodelElement(ISubmodelElement element) {
		if (element instanceof SubmodelElement) {
			((SubmodelElement) element).setParent(getReference());

			// Convert "value" in SubmodelElementCollection from Map to Collection
			if (element instanceof SubmodelElementCollection) {
				Map<String, Object> converted = SubmodelElementMapCollectionConverter.smElementToMap((Map<String, Object>) element);
				getProxy().setValue(element.getIdShort(), converted);
				return;
			}
		}

		getProxy().setValue(element.getIdShort(), element);
	}

	@Override
	public SubmodelElementCollection getLocalCopy() {
		return SubmodelElementCollection.createAsFacade(getElem()).getLocalCopy();
	}

	@Override
	public Map<String, Object> getValues() {
		return SubmodelElementCollection.createAsFacade(getElemLive()).getValues();
	}
}
