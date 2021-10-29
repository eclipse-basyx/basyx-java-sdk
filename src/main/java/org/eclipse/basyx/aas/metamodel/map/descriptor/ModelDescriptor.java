/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.metamodel.map.descriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.endpoint.Endpoint;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * Abstract class for a model descriptor that contains: - a short id - an
 * identifier - endpoints
 *
 * @author espen
 *
 */
public abstract class ModelDescriptor extends VABModelMap<Object> {
	public static final String ENDPOINTS = "endpoints";

	protected ModelDescriptor() {
		putAll(new ModelType(getModelType()));
	}

	/**
	 * Create descriptor from existing hash map
	 */
	public ModelDescriptor(Map<String, Object> map) {
		this();
		// Put all elements from map into this descriptor
		this.putAll(map);
	}

	/**
	 * Create a new descriptor with minimal information
	 */
	public ModelDescriptor(String idShort, IIdentifier id, Endpoint endpoint) {
		this();

		// Set idShort
		put(Referable.IDSHORT, idShort);

		// Set Identifier, make sure the stored data structure is a map
		Identifier identifierMap = new Identifier(id.getIdType(), id.getId());
		put(Identifiable.IDENTIFICATION, identifierMap);

		// Set Endpoints
		setEndpoints(Arrays.asList(endpoint));
	}

	/**
	 * Return AAS ID
	 */
	@SuppressWarnings("unchecked")
	public IIdentifier getIdentifier() {
		Map<String, Object> identifierModel = (Map<String, Object>) get(Identifiable.IDENTIFICATION);
		return Identifier.createAsFacade(identifierModel);
	}

	public String getIdShort() {
		// Passing null in KeyElement type since it doesn't matter while only retrieving
		// idShort
		return Referable.createAsFacade(this, null).getIdShort();
	}

	/**
	 * Adds an endpoint
	 *
	 * @param endpoint
	 */
	public void addEndpoint(Endpoint endpoint) {
		Collection<Endpoint> endpointsCollection = getEndpoints();

		// Map<String, Object> endpointWrapper = convertEndpointToMap(endpoint, "http");
		endpointsCollection.add(endpoint);
		setEndpoints(endpointsCollection);
	}

	public void removeEndpoint(String endpoint) {
		Collection<Endpoint> endpointsCollection = getEndpoints();

		Iterator<Endpoint> iterator = endpointsCollection.iterator();
		while (iterator.hasNext()) {
			Endpoint curEndpoint = iterator.next();
			if (curEndpoint.getProtocolInformation().getEndpointAddress().equalsIgnoreCase(endpoint)) {
				iterator.remove();
				break;
			}
		}
		setEndpoints(endpointsCollection);
	}

	/**
	 * Return first AAS endpoint
	 */
	@SuppressWarnings("unchecked")
	public Endpoint getFirstEndpoint() {
		Object endpoints = get(ENDPOINTS);
		if (endpoints instanceof Collection<?>) {
			Collection<Map<String, Object>> endpointCollection = (Collection<Map<String, Object>>) endpoints;
			Map<String, Object> endpointMap = endpointCollection.iterator().next();

			return Endpoint.createAsFacade(endpointMap);
		} else {
			return null;
		}
	}

	/**
	 * Return all AAS endpoints
	 */
	@SuppressWarnings("unchecked")
	public Collection<Endpoint> getEndpoints() {
		Object endpoints = get(ENDPOINTS);
		// Extract String from endpoint for set or list representations of the endpoint
		// wrappers
		if (endpoints instanceof Collection<?>) {
			// Create a new return list and insert all endpoints. If the endpoints are
			// created using Arrays.asList() which is immutable, this can be solved
			Collection<Endpoint> ret = new ArrayList<Endpoint>();
			for (Map<String, Object> endpointMap : (Collection<Map<String, Object>>) endpoints) {
				ret.add(Endpoint.createAsFacade(endpointMap));
			}
			return ret;
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * Validates a model descriptor by checking whether idShort, identification and
	 * endpoints key is present in the given map
	 *
	 * @param map
	 */
	protected void validate(Map<String, Object> map) {
		if (!map.containsKey(Referable.IDSHORT) || !(map.get(Referable.IDSHORT) instanceof String))
			throw new MalformedRequestException(getModelType() + " is missing idShort entry");
		if (!map.containsKey(Identifiable.IDENTIFICATION) || !(map.get(Identifiable.IDENTIFICATION) instanceof Map<?, ?>))
			throw new MalformedRequestException(getModelType() + " is missing identification entry");
		if (!map.containsKey(ENDPOINTS) || !(map.get(ENDPOINTS) instanceof Collection<?>))
			throw new MalformedRequestException(getModelType() + " is missing endpoints entry");
	}

	protected abstract String getModelType();

	private void setEndpoints(Collection<Endpoint> endpointsCollection) {
		put(ENDPOINTS, endpointsCollection);
	}
}
