/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.registry.restapi;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.registry.api.IRegistry;
import org.eclipse.basyx.registry.descriptor.AASDescriptor;
import org.eclipse.basyx.registry.descriptor.ModelUrn;
import org.eclipse.basyx.registry.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.registry.memory.InMemoryRegistry;
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
public class RegistryModelProvider implements IModelProvider {

	IRegistry registry;

	public RegistryModelProvider(IRegistry registry) {
		this.registry = registry;
	}

	public RegistryModelProvider() {
		this(new InMemoryRegistry());
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
		if (!(value instanceof Map)) {
			throw new MalformedRequestException("Given newValue is not a Map");
		}

		Map<String, Object> map = (Map<String, Object>) value;

		String type = ModelType.createAsFacade(map).getName();

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
	 *            the Shell Map object
	 * @return an AASDescriptor
	 * @throws MalformedRequestException
	 */
	private AASDescriptor createShellDescriptorFromMap(Object value) throws MalformedRequestException {
		Map<String, Object> map = checkModelType(AASDescriptor.MODELTYPE, value);
		return new AASDescriptor(map);
	}

	/**
	 * Creates a new SubmodelDescriptor with the content of the given Map.<br>
	 * Makes sure, that given Object is an SubmodelDescriptor by checking its
	 * ModelType.
	 *
	 * @param value
	 *            the Submodel Map object
	 * @return a SubmodelDescriptor
	 * @throws MalformedRequestException
	 */
	private SubmodelDescriptor createSubmodelDescriptorFromMap(Object value) throws MalformedRequestException {
		Map<String, Object> map = checkModelType(SubmodelDescriptor.MODELTYPE, value);
		return new SubmodelDescriptor(map);
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		RegistryServerAPIHelper registryServerAPIHelper = new RegistryServerAPIHelper(path);

		if (registryServerAPIHelper.isAllShellDescriptorsPath()) {
			return getAllShellDescriptors();
		} else if (registryServerAPIHelper.isSingleShellDescriptorPath()) {
			return getSingleShellDescriptor(registryServerAPIHelper.getFirstDescriptorId());
		} else if (registryServerAPIHelper.isSingleShellDescriptorAllSubmodelDescriptorsPath()) {
			return getAllSubmodelDescriptorsForShellDescriptor(registryServerAPIHelper.getFirstDescriptorId());
		} else if (registryServerAPIHelper.isAllSubmodelDescriptorsPath()) {
			return getAllSubmodelDescriptors();
		} else if (registryServerAPIHelper.isSingleSubmodelDescriptorPath()) {
			return getSingleSubmodelDescriptor(registryServerAPIHelper.getFirstDescriptorId());
		} else {
			return getSingleSubmodelDescriptorForShellDescriptor(registryServerAPIHelper.getFirstDescriptorId(), registryServerAPIHelper.getSecondDescriptorId());
		}
	}

	private List<AASDescriptor> getAllShellDescriptors() {
		return registry.lookupAllShells();
	}

	private List<SubmodelDescriptor> getAllSubmodelDescriptors() {
		return registry.lookupAllSubmodels();
	}

	private Object getSingleShellDescriptor(String shellIdentifier) {
		AASDescriptor shellDescriptor = registry.lookupShell(new ModelUrn(shellIdentifier));

		if (shellDescriptor == null) {
			throw new ResourceNotFoundException("Specified shellIdentifier '" + shellIdentifier + "' does not exist.");
		}

		return shellDescriptor;
	}

	private Object getSingleSubmodelDescriptor(String submodelIdentifier) {
		SubmodelDescriptor submodelDescriptor = registry.lookupSubmodel(new ModelUrn(submodelIdentifier));

		if (submodelDescriptor == null) {
			throw new ResourceNotFoundException("Specified submodelIdentifier '" + submodelIdentifier + "' does not exist.");
		}

		return submodelDescriptor;
	}

	/**
	 * Gets all SubmodelDescriptor objects form a shell. Throws
	 * ResourceNotFoundException if AAS does not exist.
	 *
	 * @param shellId
	 *            id of the shell
	 * @return Set of contained SubmodelDescriptor objects
	 * @throws ResourceNotFoundException
	 *             if the AAS does not exist
	 */
	private Collection<SubmodelDescriptor> getAllSubmodelDescriptorsForShellDescriptor(String shellId) throws ResourceNotFoundException {
		ModelUrn shellIdentifier = new ModelUrn(shellId);
		AASDescriptor shellDescriptor = registry.lookupShell(shellIdentifier);

		if (shellDescriptor == null) {
			throw new ResourceNotFoundException("Specified shellId '" + shellIdentifier.getId() + "' does not exist.");
		}

		return shellDescriptor.getSubmodelDescriptors();
	}

	private Object getSingleSubmodelDescriptorForShellDescriptor(String shellId, String submodelId) {
		ModelUrn shellIdentifier = new ModelUrn(shellId);
		ModelUrn submodelIdentifier = new ModelUrn(submodelId);
		return getSubmodelDescriptor(shellIdentifier, submodelIdentifier);
	}

	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		RegistryServerAPIHelper registryServerAPIHelper = new RegistryServerAPIHelper(path);

		if (registryServerAPIHelper.isSingleShellDescriptorPath()) {
			updateShellDescriptor(newValue, registryServerAPIHelper.getFirstDescriptorId());
		} else if (registryServerAPIHelper.isSingleSubmodelDescriptorPath()) {
			updateSubmodelDescriptor(newValue, registryServerAPIHelper.getFirstDescriptorId());
		} else if (registryServerAPIHelper.isSingleShellDescriptorSingleSubmodelDescriptorPath()) {
			updateSubmodelDescriptorForShellDescriptor(newValue, registryServerAPIHelper.getFirstDescriptorId());
		} else {
			throw new MalformedRequestException("Unknown path " + path);
		}
	}

	@Override
	public void createValue(String path, Object newValue) throws ProviderException {
		RegistryServerAPIHelper registryServerAPIHelper = new RegistryServerAPIHelper(path);

		if (registryServerAPIHelper.isAllShellDescriptorsPath()) {
			registerShellDescriptor(newValue);
		} else if (registryServerAPIHelper.isAllSubmodelDescriptorsPath()) {
			registerSubmodelDescriptor(newValue);
		} else if (registryServerAPIHelper.isSingleShellDescriptorAllSubmodelDescriptorsPath()) {
			registerSubmodelDescriptorForShellDescriptor(newValue, registryServerAPIHelper.getFirstDescriptorId());
		} else {
			throw new MalformedRequestException("Unknown path " + path);
		}
	}

	private void registerShellDescriptor(Object newValue) {
		AASDescriptor shellDescriptor = createShellDescriptorFromMap(newValue);

		registry.register(shellDescriptor);
	}

	private void registerSubmodelDescriptor(Object newValue) {
		SubmodelDescriptor submodelDescriptor = createSubmodelDescriptorFromMap(newValue);

		registry.register(submodelDescriptor);
	}

	private void updateShellDescriptor(Object newValue, String urlId) {
		AASDescriptor shellDescriptor = createShellDescriptorFromMap(newValue);

		String shellDescriptorId = shellDescriptor.getIdentifier().getId();

		if (shellDescriptorId.equals(urlId)) {
			registry.updateShell(shellDescriptor.getIdentifier(), shellDescriptor);
		} else {
			throw new MalformedRequestException("The shellId " + shellDescriptorId + " in the descriptor does not match the URL with id " + urlId);
		}
	}

	private void updateSubmodelDescriptor(Object newValue, String urlId) {
		SubmodelDescriptor submodelDescriptor = createSubmodelDescriptorFromMap(newValue);

		String submodelDescriptorId = submodelDescriptor.getIdentifier().getId();

		if (submodelDescriptorId.equals(urlId)) {
			registry.updateSubmodel(submodelDescriptor.getIdentifier(), submodelDescriptor);
		} else {
			throw new MalformedRequestException("The submodelId " + submodelDescriptorId + " in the descriptor does not match the URL with id " + urlId);
		}
	}

	private void registerSubmodelDescriptorForShellDescriptor(Object newValue, String shellId) {
		ModelUrn shellIdentifier = new ModelUrn(shellId);
		SubmodelDescriptor submodelDescriptor = createSubmodelDescriptorFromMap(newValue);

		registry.registerSubmodelForShell(shellIdentifier, submodelDescriptor);
	}

	private void updateSubmodelDescriptorForShellDescriptor(Object newValue, String shellId) {
		ModelUrn shellIdentifier = new ModelUrn(shellId);
		SubmodelDescriptor submodelDescriptor = createSubmodelDescriptorFromMap(newValue);

		registry.registerSubmodelForShell(shellIdentifier, submodelDescriptor);
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		RegistryServerAPIHelper registryPath = new RegistryServerAPIHelper(path);

		if (registryPath.isAllShellDescriptorsPath() || registryPath.isAllSubmodelDescriptorsPath() || registryPath.isSingleShellDescriptorAllSubmodelDescriptorsPath()) {
			throw new MalformedRequestException("Delete with empty path is not supported by registry");
		}

		if (registryPath.isSingleShellDescriptorSingleSubmodelDescriptorPath()) {
			deleteSubmodelDescriptorForShellDescriptor(registryPath.getFirstDescriptorId(), registryPath.getSecondDescriptorId());
		} else if (registryPath.isSingleShellDescriptorPath()) {
			deleteShellDescriptor(registryPath.getFirstDescriptorId());
		} else if (registryPath.isSingleSubmodelDescriptorPath()) {
			deleteSubmodelDescriptor(registryPath.getFirstDescriptorId());
		}

	}

	private void deleteSubmodelDescriptorForShellDescriptor(String shellId, String submodelId) {
		ModelUrn shellIdentifier = new ModelUrn(shellId);
		ModelUrn submodelIdentifier = new ModelUrn(submodelId);
		SubmodelDescriptor submodelDescriptor = getSubmodelDescriptor(shellIdentifier, submodelIdentifier);

		registry.deleteSubmodelFromShell(shellIdentifier, submodelDescriptor.getIdentifier());
	}

	private void deleteShellDescriptor(String shellIdentifier) {
		ModelUrn shellId = new ModelUrn(shellIdentifier);

		if (registry.lookupShell(shellId) == null) {
			throw new ResourceNotFoundException("Shell '" + shellId + "' to be deleted does not exist.");
		}

		registry.deleteModel(shellId);
	}

	private void deleteSubmodelDescriptor(String submodelIdentifier) {
		ModelUrn submodelId = new ModelUrn(submodelIdentifier);

		if (registry.lookupSubmodel(submodelId) == null) {
			throw new ResourceNotFoundException("Submodel '" + submodelId + "' to be deleted does not exist.");
		}

		registry.deleteSubmodel(submodelId);
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
	 * @param shellIdentifier
	 *            identifier of the shell
	 * @param submodelIdentifier
	 *            identifier of the submodel
	 * @return the SubmodelDescriptor with the given id
	 * @throws ResourceNotFoundException
	 *             if aasId does not exist
	 */
	private SubmodelDescriptor getSubmodelDescriptor(IIdentifier shellIdentifier, IIdentifier submodelIdentifier) throws ResourceNotFoundException {
		AASDescriptor shellDescriptor = registry.lookupShell(shellIdentifier);

		if (shellDescriptor == null) {
			throw new ResourceNotFoundException("Specified shellId '" + shellIdentifier.getId() + "' does not exist.");
		}

		SubmodelDescriptor submodelDescriptor = shellDescriptor.getSubmodelDescriptorFromIdentifier(submodelIdentifier);

		if (submodelDescriptor == null) {
			throw new ResourceNotFoundException("Specified SubmodelId '" + submodelIdentifier + "' for Shell '" + shellIdentifier.getId() + "' does not exist.");
		}

		return submodelDescriptor;
	}
}
