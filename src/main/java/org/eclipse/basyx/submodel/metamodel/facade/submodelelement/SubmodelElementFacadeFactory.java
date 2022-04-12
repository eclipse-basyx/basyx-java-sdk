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
package org.eclipse.basyx.submodel.metamodel.facade.submodelelement;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.Capability;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.Blob;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.ReferenceElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.entity.Entity;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.event.BasicEvent;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.AnnotatedRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;

/**
 * Facade factory for SubmodelElement
 * 
 * @author conradi
 * 
 */
public class SubmodelElementFacadeFactory {

	/**
	 * Takes a Map and creates the corresponding SubmodelElement as facade
	 * 
	 * @param submodelElement a Map containing the information of a SubmodelElement
	 * @return the actual of the given SubmodelElement map created as facade
	 */
	public static ISubmodelElement createSubmodelElement(Map<String, Object> submodelElement) {
		if (Property.isProperty(submodelElement)) {
			return Property.createAsFacade(submodelElement);
		} else if (Blob.isBlob(submodelElement)) {
			return Blob.createAsFacade(submodelElement);
		} else if (File.isFile(submodelElement)) {
			return File.createAsFacade(submodelElement);
		} else if (SubmodelElementCollection.isSubmodelElementCollection(submodelElement)) {
			return SubmodelElementCollection.createAsFacade(submodelElement);
		} else if (MultiLanguageProperty.isMultiLanguageProperty(submodelElement)) {
			return MultiLanguageProperty.createAsFacade(submodelElement);
		} else if (Entity.isEntity(submodelElement)) {
			return Entity.createAsFacade(submodelElement);
		} else if (Range.isRange(submodelElement)) {
			return Range.createAsFacade(submodelElement);
		} else if (ReferenceElement.isReferenceElement(submodelElement)) {
			return ReferenceElement.createAsFacade(submodelElement);
		} else if (RelationshipElement.isRelationshipElement(submodelElement)) {
			return RelationshipElement.createAsFacade(submodelElement);
		} else if (AnnotatedRelationshipElement.isAnnotatedRelationshipElement(submodelElement)) {
			return AnnotatedRelationshipElement.createAsFacade(submodelElement);
		} else if (Operation.isOperation(submodelElement)) {
			return Operation.createAsFacade(submodelElement);
		} else if (BasicEvent.isBasicEvent(submodelElement)) {
			return BasicEvent.createAsFacade(submodelElement);
		} else if (Capability.isCapability(submodelElement)) {
			return Capability.createAsFacade(submodelElement);
		} else {
			throw new RuntimeException("Can not create a submodel element from given map: " + submodelElement);
		}
	}

	
}
