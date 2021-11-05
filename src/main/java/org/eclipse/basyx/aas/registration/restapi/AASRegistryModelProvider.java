/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.registration.restapi;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Connects an arbitrary IRegistryService implementation to the VAB
 *
 * @author schnicke, conradi, fischer
 *
 */
public class AASRegistryModelProvider implements IModelProvider {

	IAASRegistry registry;

	public AASRegistryModelProvider(IAASRegistry registry) {
		this.registry = registry;
	}

	public AASRegistryModelProvider() {
		this(new InMemoryRegistry());
	}

	private BaSyxRegistryPath preparePath(String path) throws MalformedRequestException {
		try {
			return new BaSyxRegistryPath(path);
		} catch (UnsupportedEncodingException e) {
			// Malformed request because of unsupported encoding
			throw new MalformedRequestException("Path has to be encoded as UTF-8 string.");
		}
	}

	/**
	 * Checks if a given Object is a Map and checks if it has the correct modelType.
	 *
	 * @param expectedModelType
	 *            the modelType the Object is expected to have
	 * @param value
	 *            the Object to be checked and casted
	 * @return the object casted to a Map
	 * @throws MalformedRequestException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> checkModelType(String expectedModelType, Object value) throws MalformedRequestException {
		// check if the given value is a Map
		if (!(value instanceof Map)) {
			throw new MalformedRequestException("Given newValue is not a Map");
		}

		Map<String, Object> map = (Map<String, Object>) value;

		// check if the given Map contains an AAS
		String type = ModelType.createAsFacade(map).getName();

		// have to accept Objects without modeltype information,
		// as modeltype is not part of the public metamodel
		if (!expectedModelType.equals(type) && type != null) {
			throw new MalformedRequestException("Given newValue map has not the correct ModelType");
		}

		return map;
	}

	/**
	 * Creates a new AASDescriptor with the content of the given Map.<br>
	 * Makes sure, that given Object is an AASDescriptor by checking its ModelType.
	 *
	 * @param value
	 *            the AAS Map object
	 * @return an AAS
	 * @throws MalformedRequestException
	 */
	private AASDescriptor createAASDescriptorFromMap(Object value) throws MalformedRequestException {
		Map<String, Object> map = checkModelType(AASDescriptor.MODELTYPE, value);
		return new AASDescriptor(map);
	}

	/**
	 * Creates a new SubmodelDescriptor with the content of the given Map.<br>
	 * Makes sure, that given Object is an SubmodelDescriptor by checking its
	 * ModelType.
	 *
	 * @param value
	 *            the AAS Map object
	 * @return an AAS
	 * @throws MalformedRequestException
	 */
	private SubmodelDescriptor createSubmodelDescriptorFromMap(Object value) throws MalformedRequestException {
		Map<String, Object> map = checkModelType(SubmodelDescriptor.MODELTYPE, value);
		return new SubmodelDescriptor(map);
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		BaSyxRegistryPath registryPath = preparePath(path);

		if (registryPath.isAllAASDescriptorsPath()) {
			return getAllAASDescriptors();
		} else if (registryPath.isSingleAASDescriptorPath()) {
			return getSingleAASDescriptor(registryPath.getFirstDescriptorId());
		} else if (registryPath.isSingleAASDescriptorAllSubmodelDescriptorsPath()) {
			return getAllSubmodelDescriptorsForAASDescriptor(registryPath.getFirstDescriptorId());
		} else {
			return getSingleSubmodelDescriptorForAASDescriptor(registryPath.getFirstDescriptorId(), registryPath.getSecondDescriptorId());
		}
	}

	private List<AASDescriptor> getAllAASDescriptors() {
		return registry.lookupAll();
	}

	private Object getSingleAASDescriptor(String aasIdentifier) {
		AASDescriptor aasDescriptor = registry.lookupAAS(new ModelUrn(aasIdentifier));

		if (aasDescriptor == null) {
			throw new ResourceNotFoundException("Specified aasIdentifier '" + aasIdentifier + "' does not exist.");
		}

		return aasDescriptor;
	}

	/**
	 * Gets all SubmodelDescriptor objects form an aas. Throws
	 * ResourceNotFoundException if AAS does not exist.
	 *
	 * @param aasId
	 *            id of the aas
	 * @return Set of contained SubmodelDescriptor objects
	 * @throws ResourceNotFoundException
	 *             if the AAS does not exist
	 */
	private Collection<SubmodelDescriptor> getAllSubmodelDescriptorsForAASDescriptor(String aasId) throws ResourceNotFoundException {
		ModelUrn aasIdentifier = new ModelUrn(aasId);
		AASDescriptor aasDescriptor = registry.lookupAAS(aasIdentifier);

		if (aasDescriptor == null) {
			throw new ResourceNotFoundException("Specified AASid '" + aasIdentifier.getId() + "' does not exist.");
		}

		return aasDescriptor.getSubmodelDescriptors();
	}

	private Object getSingleSubmodelDescriptorForAASDescriptor(String aasId, String submodelId) {
		return getSubmodelDescriptor(new ModelUrn(aasId), submodelId);
	}

	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		BaSyxRegistryPath registryPath = preparePath(path);

		if (registryPath.isSingleAASDescriptorPath()) {
			updateAASDescriptor(newValue, registryPath.getFirstDescriptorId());
		} else if (registryPath.isSingleAASDescriptorSingleSubmodelDescriptorPath()) {
			updateSubmodelDescriptorForAASDescriptor(newValue, registryPath.getFirstDescriptorId());
		} else {
			throw new MalformedRequestException("Unknown path " + path);
		}
	}

	@Override
	public void createValue(String path, Object newValue) throws ProviderException {
		BaSyxRegistryPath registryPath = preparePath(path);

		if (registryPath.isAllAASDescriptorsPath()) {
			registerAASDescriptor(newValue);
		} else if (registryPath.isSingleAASDescriptorAllSubmodelDescriptorsPath()) {
			registerSubmodelDescriptorForAASDescriptor(newValue, registryPath.getFirstDescriptorId());
		} else {
			throw new MalformedRequestException("Unknown path " + path);
		}
	}

	private void registerAASDescriptor(Object newValue) {
		AASDescriptor aasDescriptor = createAASDescriptorFromMap(newValue);

		registry.register(aasDescriptor);
	}

	private void updateAASDescriptor(Object newValue, String urlId) {
		AASDescriptor aasDescriptor = createAASDescriptorFromMap(newValue);

		String aasDescriptorId = aasDescriptor.getIdentifier().getId();

		if (aasDescriptorId.equals(urlId)) {
			registry.update(aasDescriptor.getIdentifier(), aasDescriptor);
		} else {
			throw new MalformedRequestException("The AASId " + aasDescriptorId + " in the descriptor does not match the URL with id " + urlId);
		}
	}

	private void registerSubmodelDescriptorForAASDescriptor(Object newValue, String aasId) {
		ModelUrn aasIdentifier = new ModelUrn(aasId);
		SubmodelDescriptor submodelDescriptor = createSubmodelDescriptorFromMap(newValue);

		registry.register(aasIdentifier, submodelDescriptor);
	}

	private void updateSubmodelDescriptorForAASDescriptor(Object newValue, String aasId) {
		ModelUrn aasIdentifier = new ModelUrn(aasId);
		SubmodelDescriptor submodelDescriptor = createSubmodelDescriptorFromMap(newValue);

		registry.register(aasIdentifier, submodelDescriptor);
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		BaSyxRegistryPath registryPath = preparePath(path);

		if (registryPath.isAllAASDescriptorsPath() || registryPath.isAllSubmodelDescriptorsPath() || registryPath.isSingleAASDescriptorAllSubmodelDescriptorsPath()) {
			throw new MalformedRequestException("Delete with empty path is not supported by registry");
		}

		if (registryPath.isSingleAASDescriptorSingleSubmodelDescriptorPath()) {
			deleteSubmodelDescriptorForAASDescriptor(registryPath.getFirstDescriptorId(), registryPath.getSecondDescriptorId());
		} else if (registryPath.isSingleAASDescriptorPath()) {
			deleteAASDescriptor(registryPath.getFirstDescriptorId());
		}

	}

	private void deleteSubmodelDescriptorForAASDescriptor(String aasId, String submodelId) {
		ModelUrn aasIdentifier = new ModelUrn(aasId);
		SubmodelDescriptor submodelDescriptor = getSubmodelDescriptor(aasIdentifier, submodelId);

		registry.delete(aasIdentifier, submodelDescriptor.getIdentifier());
	}

	private void deleteAASDescriptor(String aasIdentifier) {
		ModelUrn aasId = new ModelUrn(aasIdentifier);

		if (registry.lookupAAS(aasId) == null) {
			throw new ResourceNotFoundException("AAS '" + aasId + "' to be deleted does not exist.");
		}

		registry.delete(aasId);
	}

	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {
		throw new MalformedRequestException("DeleteValue with parameter not supported by registry");
	}

	@Override
	public Object invokeOperation(String path, Object... parameter) throws ProviderException {
		throw new MalformedRequestException("Invoke not supported by registry");
	}

	/**
	 * Gets a specific SubmodelDescriptor form an aas. Throws
	 * ResourceNotFoundException if aas does not exist.
	 *
	 * @param aasId
	 *            id of the aas
	 * @param submodelId
	 *            id of the submodel
	 * @return the SubmodelDescriptor with the given id
	 * @throws ResourceNotFoundException
	 *             if aasId does not exist
	 */
	private SubmodelDescriptor getSubmodelDescriptor(IIdentifier aasId, String submodelId) throws ResourceNotFoundException {
		AASDescriptor aasDescriptor = registry.lookupAAS(aasId);

		if (aasDescriptor == null) {
			throw new ResourceNotFoundException("Specified AASId '" + aasId.getId() + "' does not exist.");
		}

		SubmodelDescriptor submodelDescriptor = aasDescriptor.getSubmodelDescriptorFromIdentifierId(submodelId);

		if (submodelDescriptor == null) {
			throw new ResourceNotFoundException("Specified SubmodelId '" + submodelId + "' for AAS '" + aasId.getId() + "' does not exist.");
		}

		return submodelDescriptor;
	}
}
