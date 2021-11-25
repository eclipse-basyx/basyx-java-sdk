/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.registry.descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.registry.descriptor.parts.Endpoint;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.AdministrativeInformation;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
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
	 * Creates a descriptor with minimal information
	 * 
	 * @param idShort
	 * @param identifier
	 * @param endpoints
	 */
	public ModelDescriptor(String idShort, IIdentifier identifier, Collection<Endpoint> endpoints) {
		this();

		put(Referable.IDSHORT, idShort);

		Identifier identifierMap = new Identifier(identifier.getIdType(), identifier.getId());
		put(Identifiable.IDENTIFICATION, identifierMap);

		setEndpoints(endpoints);
	}

	// TODO: Constructor / Builder for description and administration

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

	@SuppressWarnings("unchecked")
	public AdministrativeInformation getAdministration() {
		Map<String, Object> administrativeInformation = (Map<String, Object>) get(Identifiable.ADMINISTRATION);
		return AdministrativeInformation.createAsFacade(administrativeInformation);
	}

	public void setAdministration(AdministrativeInformation administration) {
		put(Identifiable.ADMINISTRATION, administration);
	}

	@SuppressWarnings("unchecked")
	public Collection<LangString> getDescriptions() {
		Collection<LangString> descriptionMap = (Collection<LangString>) get(Identifiable.DESCRIPTION);
		return descriptionMap;
	}

	public void addDescription(LangString langString) {
		Collection<LangString> descriptionCollection = getDescriptions();
		if (descriptionCollection == null) {
			descriptionCollection = new ArrayList<LangString>();
		}
		descriptionCollection.add(langString);
		setDescription(descriptionCollection);
	}

	public void setDescription(Collection<LangString> descriptionCollection) {
		put(Identifiable.DESCRIPTION, descriptionCollection);
	}

	public void removeDescription(LangString description) {
		Collection<LangString> descriptionsCollection = getDescriptions();
		if (!descriptionsCollection.contains(description)) {
			throw new ResourceNotFoundException("Description '" + description + "' does not exist.");
		}

		descriptionsCollection.remove(description);
		setDescription(descriptionsCollection);
	}

	public void addEndpoint(Endpoint endpoint) {
		Collection<Endpoint> endpointsCollection = getEndpoints();
		endpointsCollection.add(endpoint);

		setEndpoints(endpointsCollection);
	}

	public void removeEndpoint(Endpoint endpoint) {
		Collection<Endpoint> endpointsCollection = getEndpoints();

		if (!endpointsCollection.contains(endpoint)) {
			throw new ResourceNotFoundException("Endpoint '" + endpoint + "' does not exist.");
		}

		endpointsCollection.remove(endpoint);
		setEndpoints(endpointsCollection);
	}

	public Endpoint getFirstEndpoint() {
		Object endpoints = get(ENDPOINTS);
		if (endpoints instanceof Collection<?>) {
			return getFirstEndpointFromCollection(endpoints);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private Endpoint getFirstEndpointFromCollection(Object endpoints) {
		Collection<Map<String, Object>> endpointCollection = (Collection<Map<String, Object>>) endpoints;
		Map<String, Object> endpointMap = endpointCollection.iterator().next();

		return Endpoint.createAsFacade(endpointMap);
	}

	public Collection<Endpoint> getEndpoints() {
		Object endpoints = get(ENDPOINTS);
		if (endpoints instanceof Collection<?>) {
			return getAllEndpointsFromCollection(endpoints);
		} else {
			return new ArrayList<>();
		}
	}

	@SuppressWarnings("unchecked")
	private Collection<Endpoint> getAllEndpointsFromCollection(Object endpoints) {
		Collection<Endpoint> ret = new ArrayList<Endpoint>();
		for (Map<String, Object> endpointMap : (Collection<Map<String, Object>>) endpoints) {
			ret.add(Endpoint.createAsFacade(endpointMap));
		}
		return ret;
	}

	protected static boolean isValid(Map<String, Object> map) {
		if (!map.containsKey(Referable.IDSHORT) || !(map.get(Referable.IDSHORT) instanceof String)) {
			return false;
		}

		if (!map.containsKey(Identifiable.IDENTIFICATION) || !(map.get(Identifiable.IDENTIFICATION) instanceof Map<?, ?>)) {
			return false;
		}

		if (!map.containsKey(ENDPOINTS) || !(map.get(ENDPOINTS) instanceof Collection<?>)) {
			return false;
		}

		return true;
	}

	protected abstract String getModelType();

	private void setEndpoints(Collection<Endpoint> endpointsCollection) {
		put(ENDPOINTS, endpointsCollection);
	}
}
