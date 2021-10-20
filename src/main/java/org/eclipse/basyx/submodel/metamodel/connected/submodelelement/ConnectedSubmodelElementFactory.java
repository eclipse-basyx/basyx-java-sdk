/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.connected.submodelelement;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IDataElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedBlob;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedFile;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedMultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedProperty;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedRange;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedReferenceElement;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.event.ConnectedBasicEvent;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.operation.ConnectedOperation;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.relationship.ConnectedAnnotatedRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.relationship.ConnectedRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.Capability;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.Blob;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.ReferenceElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.event.BasicEvent;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.AnnotatedRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;


/**
 * Factory creating connected ISubmodelElements from a given VABElementProxy
 * 
 * @author conradi, espen
 *
 */
public class ConnectedSubmodelElementFactory {

	/**
	 * Creates connected ISubmodelElements from a VABElementProxy
	 * 
	 * @param rootProxy      proxy for the root element
	 * @param collectionPath path in the proxy for accessing all elements
	 * @param elementPath    path in the proxy for accessing single elements by short ids
	 * @return A Map containing the created connected ISubmodelElements and their IDs
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, ISubmodelElement> getConnectedSubmodelElements(VABElementProxy rootProxy,
			String collectionPath, String elementPath) {
		// Query the whole list of elements
		Collection<Map<String, Object>> mapElemList = (Collection<Map<String, Object>>) rootProxy
				.getValue(collectionPath);
		// Get the type and idShort for each element and create the corresponding connected variant
		Map<String, ISubmodelElement> ret = new HashMap<>();
		for (Map<String, Object> node : mapElemList) {
			String idShort = Referable.createAsFacade(node, KeyElements.SUBMODELELEMENT).getIdShort();
			ISubmodelElement element = getConnectedSubmodelElement(rootProxy, elementPath, idShort, node);
			ret.put(idShort, element);
		}
		return ret;
	}

	/**
	 * Creates a Collection of connected ISubmodelElements from a VABElementProxy
	 * 
	 * @param rootProxy      proxy for the root element
	 * @param collectionPath path in the proxy for accessing all elements
	 * @param elementPath    path in the proxy for accessing single elements by short ids
	 * @return A Collection containing the created connected ISubmodelElements
	 */
	@SuppressWarnings("unchecked")
	public static Collection<ISubmodelElement> getElementCollection(VABElementProxy rootProxy,
			String collectionPath, String elementPath) {
		// Query the whole list of elements
		Collection<Map<String, Object>> mapElemList = (Collection<Map<String, Object>>) rootProxy
				.getValue(collectionPath);
		// Get the type and idShort for each element and create the corresponding connected variant
		Collection<ISubmodelElement> ret = new ArrayList<>();
		for (Map<String, Object> node : mapElemList) {
			String idShort = Referable.createAsFacade(node, KeyElements.SUBMODELELEMENT).getIdShort();
			ISubmodelElement element = getConnectedSubmodelElement(rootProxy, elementPath, idShort, node);
			ret.add(element);
		}
		return ret;
	}
	
	/**
	 * Creates a connected ISubmodelElement by idShort, proxy and map content
	 * 
	 * @param rootProxy      proxy for the root element
	 * @param collectionPath path in the proxy for accessing all elements
	 * @param elementPath    path in the proxy for accessing single elements by short ids
	 * @return The connected variant of the requested submodel element
	 */
	public static ISubmodelElement getConnectedSubmodelElement(VABElementProxy rootProxy,
			String elementPath, String idShort, Map<String, Object> mapContent) {
		String subPath = VABPathTools.concatenatePaths(elementPath, idShort);
		VABElementProxy proxy = rootProxy.getDeepProxy(subPath);
		if (Property.isProperty(mapContent)) {
			return new ConnectedProperty(proxy);
		} else if (Blob.isBlob(mapContent)) {
			return new ConnectedBlob(proxy);
		} else if (File.isFile(mapContent)) {
			return new ConnectedFile(proxy);
		} else if (SubmodelElementCollection.isSubmodelElementCollection(mapContent)) {
			return new ConnectedSubmodelElementCollection(proxy);
		} else if(MultiLanguageProperty.isMultiLanguageProperty(mapContent)) {
			return new ConnectedMultiLanguageProperty(proxy);
		} else if(Range.isRange(mapContent)) {
			return new ConnectedRange(proxy);
		} else if(ReferenceElement.isReferenceElement(mapContent)) {
			return new ConnectedReferenceElement(proxy);
		} else if (AnnotatedRelationshipElement.isAnnotatedRelationshipElement(mapContent)) {
			return new ConnectedAnnotatedRelationshipElement(proxy);
		} else if (RelationshipElement.isRelationshipElement(mapContent)) {
			return new ConnectedRelationshipElement(proxy);
		} else if (Operation.isOperation(mapContent)) {
			return new ConnectedOperation(proxy);
		} else if(BasicEvent.isBasicEvent(mapContent)) {
			return new ConnectedBasicEvent(proxy);
		} else if (Capability.isCapability(mapContent)) {
			return new ConnectedCapability(proxy);
		} else {
			return null;
		}
	}
	
	/**
	 * Creates connected IOperations from a VABElementProxy
	 * 
	 * @param rootProxy      proxy for the root element
	 * @param collectionPath path in the proxy for accessing all elements
	 * @param elementPath    path in the proxy for accessing single elements by short ids
	 * @return A Map containing the created connected IOperations and their IDs
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, IOperation> getOperations(VABElementProxy rootProxy, String collectionPath,
			String elementPath) {
		// Query the whole list of elements
		Collection<Map<String, Object>> mapElemList = (Collection<Map<String, Object>>) rootProxy
				.getValue(collectionPath);

		// Get the type and idShort for each operation and create the corresponding connected variant
		Map<String, IOperation> ret = new HashMap<>();
		for (Map<String, Object> node : mapElemList) {
			String idShort = Referable.createAsFacade(node, KeyElements.OPERATION).getIdShort();
			String subPath = VABPathTools.concatenatePaths(elementPath, idShort);
			VABElementProxy proxy = rootProxy.getDeepProxy(subPath);
			if (Operation.isOperation(node)) {
				ret.put(idShort, new ConnectedOperation(proxy));
			}
		}
		return ret;
	}
	
	/**
	 * Creates connected IDataElements from a VABElementProxy
	 * 
	 * @param rootProxy      proxy for the root element
	 * @param collectionPath path in the proxy for accessing all elements
	 * @param elementPath    path in the proxy for accessing single elements by short ids
	 * @return A Map containing the created connected IDataElement and their IDs
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, IDataElement> getDataElements(VABElementProxy rootProxy, String collectionPath,
			String elementPath) {
		// Query the whole list of elements
		Collection<Map<String, Object>> mapElemList = (Collection<Map<String, Object>>) rootProxy
				.getValue(collectionPath);

		// Get the type and idShort for each operation and create the corresponding connected variant
		Map<String, IDataElement> ret = new HashMap<>();
		for (Map<String, Object> node : mapElemList) {
			String idShort = Referable.createAsFacade(node, KeyElements.DATAELEMENT).getIdShort();
			String subPath = VABPathTools.concatenatePaths(elementPath, idShort);
			VABElementProxy proxy = rootProxy.getDeepProxy(subPath);
			if (Property.isProperty(node)) {
				ret.put(idShort, new ConnectedProperty(proxy));
			} else if (Blob.isBlob(node)) {
				ret.put(idShort, new ConnectedBlob(proxy));
			} else if (File.isFile(node)) {
				ret.put(idShort, new ConnectedFile(proxy));
			} else if(MultiLanguageProperty.isMultiLanguageProperty(node)) {
				ret.put(idShort, new ConnectedMultiLanguageProperty(proxy));
			} else if(Range.isRange(node)) {
				ret.put(idShort, new ConnectedRange(proxy));
			} else if(ReferenceElement.isReferenceElement(node)) {
				ret.put(idShort, new ConnectedReferenceElement(proxy));
			}
		}
		return ret;
	}
	
	/**
	 * Creates connected IProperty elements from VABElementProxy
	 * 
	 * @param rootProxy
	 * @param collectionPath
	 * @param elementPath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, IProperty> getProperties(VABElementProxy rootProxy, String collectionPath,
			String elementPath) {
		// Query the whole list of elements
		Collection<Map<String, Object>> mapElemList = (Collection<Map<String, Object>>) rootProxy.getValue(collectionPath);

		// Get the type and idShort for each operation and create the corresponding
		// connected variant
		Map<String, IProperty> ret = new HashMap<>();
		for (Map<String, Object> node : mapElemList) {
			String idShort = Referable.createAsFacade(node, KeyElements.DATAELEMENT).getIdShort();
			String subPath = VABPathTools.concatenatePaths(elementPath, idShort);
			VABElementProxy proxy = rootProxy.getDeepProxy(subPath);
			if (Property.isProperty(node)) {
				ret.put(idShort, new ConnectedProperty(proxy));
			}
		}
		return ret;
	}
}
