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
package org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.facade.submodelelement.SubmodelElementFacadeFactory;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.haskind.HasKind;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.vab.model.VABModelMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OperationVariable as described by DAAS document An operation variable is a
 * submodel element that is used as input or output variable of an operation.
 * 
 * @author schnicke
 *
 */
public class OperationVariable extends VABModelMap<Object> implements IOperationVariable {
	public static final Logger logger = LoggerFactory.getLogger(OperationVariable.class);
	
	public static final String MODELTYPE = "OperationVariable";

	/**
	 * 
	 * @param value
	 *            Describes the needed argument for an operation via a submodel
	 *            element of kind=Type
	 */
	public OperationVariable(SubmodelElement value) {
		// Add model type
		putAll(new ModelType(MODELTYPE));
		
		setValue(value);
	}

	public OperationVariable() {
		// Add model type
		putAll(new ModelType(MODELTYPE));
	}
	
	/**
	 * Creates an OperationVariable object from a map
	 * 
	 * @param obj an OperationVariable object as raw map
	 * @return an OperationVariable object, that behaves like a facade for the given map
	 */
	public static OperationVariable createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(OperationVariable.class, obj);	
		}
		
		OperationVariable facade = new OperationVariable();
		facade.setMap(obj);
		return facade;
	}
	
	/**
	 * Check whether all mandatory elements for the metamodel
	 * exist in a map
	 * @return true/false
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValid(Map<String, Object> obj) {
		return obj != null &&
				obj.containsKey(Property.VALUE) && 
				SubmodelElement.isValid((Map<String, Object>) obj.get(Property.VALUE));
	}

	/**
	 * Sets value of operation variable
	 *
	 * @param value
	 * @throws RuntimeException if modelingkind of the value is not of modelingkind.template
	 */
	@SuppressWarnings("unchecked")
	public void setValue(ISubmodelElement value) {
		if (value.getModelingKind() != ModelingKind.TEMPLATE) {
			// TODO: Change with 1.0 Release
			logger.warn("Modeling kind of Operation variable was wrong and automatically changed to ModelingKind.TEMPLATE");
			HasKind.createAsFacade((Map<String, Object>) value).setModelingKind(ModelingKind.TEMPLATE);
		}
		put(Property.VALUE, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ISubmodelElement getValue() {
		return SubmodelElementFacadeFactory.createSubmodelElement((Map<String, Object>) get(Property.VALUE));
	}

}
