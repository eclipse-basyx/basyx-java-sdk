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
package org.eclipse.basyx.aas.restapi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.aas.restapi.vab.VABAASAPIFactory;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;

/**
 * Provider class that implements the AssetAdministrationShellServices <br>
 * This provider supports operations on multiple sub models that are selected by
 * path<br>
 * <br>
 * Supported API:<br>
 * - getValue<br>
 * <i>/aas</i> Returns the Asset Administration Shell<br>
 * <i>/aas/submodels</i> Retrieves all Submodels from the current Asset
 * Administration Shell<br>
 * <i>/aas/submodels/{subModelIdShort}/submodel</i> Retrieves a specific
 * Submodel from a specific Asset Administration Shell<br>
 * <i>/aas/submodels/{subModelIdShort}/submodel/submodelElements</i> Retrieves
 * all SubmodelElements from the current Submodel<br>
 * <i>/aas/submodels/{subModelIdShort}/submodel/submodelElements/{submodelElementIdShort}</i>
 * Retrieves a specific SubmodelElement from the AAS's Submodel<br>
 * <i>/aas/submodels/{subModelIdShort}/submodel/submodelElements/{submodelElementIdShort}/value</i>
 * Retrieves the value of a specific SubmodelElement from the AAS's Submodel<br>
 * <br>
 * - setValue <br>
 * <i>/aas/submodels/{subModelIdShort}</i> Adds a new Submodel to an existing
 * Asset Administration Shell <br>
 * <i>/aas/submodels/{subModelIdShort}/submodel/submodelElements/{submodelElementIdShort}</i>
 * Adds a new SubmodelElement to the AAS's submodel <br>
 * <i>/aas/submodels/{subModelIdShort}/submodel/submodelElements/{submodelElementIdShort}</i>
 * Sets the value of a specific SubmodelElement from the AAS's Submodel<br>
 * <br>
 * - invokeOperation<br>
 * <i>/aas/submodels/{subModelIdShort}/submodel/submodelElements/{submodelElementIdShort}</i>
 * Invokes a specific operation from the AAS' submodel with a list of input
 * parameters <br>
 * <br>
 * - deleteValue<br>
 * <i>/aas/submodels/{subModelId}</i> Deletes a specific Submodel from a
 * specific Asset Administration Shell <br>
 * <i>/aas/submodels/{subModelIdShort}/submodel/submodelElements/{submodelElementIdShort}</i>
 * Deletes a specific submodelElement from the AAS's Submodel<br>
 *
 *
 * @author kuhn, pschorn
 *
 */
public class MultiSubmodelProvider implements IModelProvider {

	public static final String AAS = "aas";
	public static final String SUBMODELS_PREFIX = VABPathTools.concatenatePaths(AAS, AssetAdministrationShell.SUBMODELS);

	/**
	 * Store aas providers
	 */
	protected AASModelProvider aas_provider = null;

	/**
	 * Store aasId
	 */
	protected IIdentifier aasId = null;

	/*
	 * Store AAS Registry
	 */
	protected IAASRegistry registry = null;

	/**
	 * Store HTTP Connector
	 */
	protected IConnectorFactory connectorFactory = null;

	/**
	 * Store AAS API Provider. By default, uses the VAB API Provider
	 */
	protected IAASAPIFactory aasApiProvider;

	private ISubmodelAggregator smAggregator;

	/**
	 * Constructor with empty default aas and default VAB APIs
	 */
	public MultiSubmodelProvider() {
		this.setAasApiProvider(new VABAASAPIFactory());
		this.setSmAggregator(new SubmodelAggregator());
		IAASAPI aasApi = getAasApiProvider().create(new AssetAdministrationShell());
		setAssetAdministrationShell(new AASModelProvider(aasApi));
	}

	/**
	 * Constructor for using custom APIs
	 */
	public MultiSubmodelProvider(AASModelProvider contentProvider, IAASAPIFactory aasApiProvider, ISubmodelAPIFactory smApiProvider) {
		this.setAasApiProvider(aasApiProvider);
		this.setSmAggregator(new SubmodelAggregator(smApiProvider));
		setAssetAdministrationShell(contentProvider);
	}

	/**
	 * Constructor that accepts an AAS
	 */
	public MultiSubmodelProvider(AASModelProvider contentProvider) {
		this.setAasApiProvider(new VABAASAPIFactory());
		this.setSmAggregator(new SubmodelAggregator());
		// Store content provider
		setAssetAdministrationShell(contentProvider);
	}

	/**
	 * Constructor that accepts Submodel
	 */
	public MultiSubmodelProvider(SubmodelProvider contentProvider) {
		this();
		// Store content provider
		addSubmodel(contentProvider);
	}

	/**
	 * Constructor that accepts a registry and a connection provider
	 *
	 * @param registry
	 * @param provider
	 */
	public MultiSubmodelProvider(IAASRegistry registry, IConnectorFactory provider) {
		this();
		this.registry = registry;
		this.setConnectorFactory(provider);
	}

	/**
	 * Constructor that accepts a registry, a connection provider, and API providers
	 */
	public MultiSubmodelProvider(AASModelProvider contentProvider, IAASRegistry registry, IConnectorFactory connectorFactory, ISubmodelAPIFactory smApiProvider, IAASAPIFactory aasApiProvider) {
		this(contentProvider, aasApiProvider, smApiProvider);
		this.registry = registry;
		this.setConnectorFactory(connectorFactory);
	}

	/**
	 * Constructor that accepts a registry, a connection provider, API providers,
	 * and submodelAggregator
	 */
	public MultiSubmodelProvider(AASModelProvider contentProvider, IAASRegistry registry, IConnectorFactory connectorFactory, IAASAPIFactory aasApiProvider, ISubmodelAggregator submodelAggregator) {
		this.setAasApiProvider(aasApiProvider);
		setAssetAdministrationShell(contentProvider);
		this.setSmAggregator(submodelAggregator);
		this.registry = registry;
		this.setConnectorFactory(connectorFactory);
	}

	/**
	 * Constructor that accepts a aas provider, a registry, and a connection
	 * provider
	 *
	 * @param contentProvider
	 * @param registry
	 * @param provider
	 */
	public MultiSubmodelProvider(AASModelProvider contentProvider, IAASRegistry registry, HTTPConnectorFactory provider) {
		this(contentProvider);
		this.registry = registry;
		this.setConnectorFactory(provider);
	}

	/**
	 * Set an AAS for this provider
	 *
	 * @param modelContentProvider
	 *            Model content provider
	 */
	@SuppressWarnings("unchecked")
	public void setAssetAdministrationShell(AASModelProvider modelContentProvider) {
		// Add model provider
		aas_provider = modelContentProvider;
		aasId = AssetAdministrationShell.createAsFacade((Map<String, Object>) modelContentProvider.getValue("")).getIdentification();
	}

	@SuppressWarnings("unchecked")
	public void addSubmodel(SubmodelProvider modelContentProvider) {
		Submodel sm = Submodel.createAsFacade((Map<String, Object>) modelContentProvider.getValue("/submodel"));
		aas_provider.createValue("/submodels", sm);
		getSmAggregator().createSubmodel(modelContentProvider.getAPI());
	}

	@SuppressWarnings("unchecked")
	private void createSubmodel(Object newValue) {
		Map<String, Object> newSubmodelMap = (Map<String, Object>) newValue;
		Submodel submodel = Submodel.createAsFacade(newSubmodelMap);
		getSmAggregator().createSubmodel(submodel);
		aas_provider.createValue("/submodels", submodel);
	}

	/**
	 * Remove a provider
	 *
	 * @param elementIdShort
	 *            Element ID
	 */
	public void removeProvider(String elementIdShort) {
		getSmAggregator().deleteSubmodelByIdShort(elementIdShort);
	}

	/**
	 * Get the value of an element
	 */
	@Override
	public Object getValue(String path) throws ProviderException {
		VABPathTools.checkPathForNull(path);
		path = VABPathTools.stripSlashes(path);
		String[] pathElements = VABPathTools.splitPath(path);
		if (pathElements.length > 0 && pathElements[0].equals(AAS)) {
			if (pathElements.length == 1) {
				return aas_provider.getValue("");
			}
			if (pathElements[1].equals(AssetAdministrationShell.SUBMODELS)) {
				if (pathElements.length == 2) {
					return retrieveSubmodels();
				} else {
					String smIdShort = pathElements[2];
					String remainingPath = VABPathTools.buildPath(pathElements, 3);
					return handleSingleSubmodelRequest(smIdShort, remainingPath);
				}
			} else {
				String remainingPath = VABPathTools.buildPath(pathElements, 1);
				return aas_provider.getValue(remainingPath);
			}
		} else {
			throw new MalformedRequestException("The request " + path + " is not allowed for this endpoint");
		}
	}

	private Object handleSingleSubmodelRequest(String smIdShort, String remainingPath) {
		IModelProvider provider = retrieveSubmodelProvider(smIdShort);
		return provider.getValue(remainingPath);
	}

	private IModelProvider retrieveSubmodelProvider(String smIdShort) {
		IModelProvider smProvider;
		try {
			ISubmodelAPI smAPI = getSmAggregator().getSubmodelAPIByIdShort(smIdShort);
			smProvider = new SubmodelProvider(smAPI);
		} catch (ResourceNotFoundException exception) {
			// Get a model provider for the submodel in the registry
			smProvider = getRemoteSubmodelProvider(smIdShort);
		}
		return smProvider;
	}

	/**
	 * Retrieves all submodels of the AAS. If there's a registry, remote Submodels
	 * will also be retrieved.
	 *
	 * @return
	 * @throws ProviderException
	 */
	private Object retrieveSubmodels() throws ProviderException {
		// Make a list and return all local submodels
		Collection<ISubmodel> submodels = getSmAggregator().getSubmodelList();
		addConnectedSubmodels(submodels);
		return submodels.stream().map(sm -> SubmodelElementMapCollectionConverter.smToMap((Submodel) sm)).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private void addConnectedSubmodels(Collection<ISubmodel> submodels) {
		if (registry != null) {
			AASDescriptor desc = registry.lookupAAS(aasId);

			// Get the address of the AAS e.g. http://localhost:8080
			// This address should be equal to the address of this server
			String aasEndpoint = desc.getFirstEndpoint();
			String aasServerURL = getServerURL(aasEndpoint);

			List<String> localIds = submodels.stream().map(sm -> sm.getIdentification().getId()).collect(Collectors.toList());
			List<IIdentifier> missingIds = desc.getSubmodelDescriptors().stream().map(d -> d.getIdentifier()).filter(id -> !localIds.contains(id.getId())).collect(Collectors.toList());

			if (!missingIds.isEmpty()) {
				List<String> missingEndpoints = missingIds.stream().map(id -> desc.getSubmodelDescriptorFromIdentifierId(id.getId())).map(smDesc -> smDesc.getFirstEndpoint()).collect(Collectors.toList());

				// Check if any of the missing Submodels have the same address as the AAS.
				// This would mean, that the Submodel should be present on the same
				// server of the AAS but is not

				// If this error would not be caught here an endless loop would develop
				// as the registry would be asked for this Submodel and then it would be
				// requested
				// from this server again, which would ask the registry about it again

				// Such a situation might originate from a deleted but not unregistered Submodel
				// or from a manually registered but never pushed Submodel
				for (String missingEndpoint : missingEndpoints) {
					if (getServerURL(missingEndpoint).equals(aasServerURL)) {
						throw new ResourceNotFoundException("The Submodel at Endpoint '" + missingEndpoint + "' does not exist on this server. It seems to be registered but not actually present.");
					}
				}

				List<Submodel> remoteSms = missingEndpoints.stream().map(endpoint -> getConnectorFactory().create(endpoint)).map(p -> (Map<String, Object>) p.getValue("")).map(m -> Submodel.createAsFacade(m))
						.collect(Collectors.toList());
				submodels.addAll(remoteSms);
			}
		}
	}

	/**
	 * Change a model property value
	 */
	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		VABPathTools.checkPathForNull(path);
		path = VABPathTools.stripSlashes(path);
		String[] pathElements = VABPathTools.splitPath(path);
		String propertyPath = VABPathTools.buildPath(pathElements, 3);
		if (path.equals(AAS)) {
			createAssetAdministrationShell(newValue);
		} else if (!path.startsWith(SUBMODELS_PREFIX)) {
			throw new MalformedRequestException("Access to MultiSubmodelProvider always has to start with \"" + SUBMODELS_PREFIX + "\", was " + path);
		} else if (propertyPath.isEmpty()) {
			createSubmodel(newValue);
		} else {
			String smIdShort = pathElements[2];
			IModelProvider provider = retrieveSubmodelProvider(smIdShort);
			provider.setValue(propertyPath, newValue);
		}
	}

	@Override
	public void createValue(String path, Object newValue) throws ProviderException {
		throw new MalformedRequestException("Create is not supported by VABMultiSubmodelProvider. Path was: " + path);
	}

	@SuppressWarnings("unchecked")
	private void createAssetAdministrationShell(Object newAAS) {
		Map<String, Object> newAASMap = (Map<String, Object>) newAAS;
		AssetAdministrationShell shell = AssetAdministrationShell.createAsFacade(newAASMap);
		IAASAPI aasApi = getAasApiProvider().create(shell);
		aas_provider = new AASModelProvider(aasApi);
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		VABPathTools.checkPathForNull(path);
		path = VABPathTools.stripSlashes(path);
		if (!path.startsWith(SUBMODELS_PREFIX)) {
			throw new MalformedRequestException("Access to MultiSubmodelProvider always has to start with \"" + SUBMODELS_PREFIX + "\", was " + path);
		}

		String[] pathElements = VABPathTools.splitPath(path);
		String propertyPath = VABPathTools.buildPath(pathElements, 3);
		if (pathElements.length == 3) {
			// Delete Submodel from registered AAS
			String smIdShort = pathElements[2];
			if (!isSubmodelLocal(smIdShort)) {
				return;
			}
			Submodel sm = (Submodel) getSmAggregator().getSubmodelbyIdShort(smIdShort);
			String smId = sm.getIdentification().getId();
			aas_provider.deleteValue(SUBMODELS_PREFIX + "/" + smId);
			getSmAggregator().deleteSubmodelByIdShort(smIdShort);
		} else if (propertyPath.length() > 0) {
			String smIdShort = pathElements[2];
			IModelProvider provider = retrieveSubmodelProvider(smIdShort);
			provider.deleteValue(propertyPath);
		}
	}

	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {
		throw new MalformedRequestException("DeleteValue with a parameter is not supported. Path was: " + path);
	}

	@Override
	public Object invokeOperation(String path, Object... parameter) throws ProviderException {
		VABPathTools.checkPathForNull(path);
		path = VABPathTools.stripSlashes(path);
		if (!path.startsWith(SUBMODELS_PREFIX)) {
			throw new MalformedRequestException("Access to MultiSubmodelProvider always has to start with \"" + SUBMODELS_PREFIX + "\", was " + path);
		}
		String[] pathElements = VABPathTools.splitPath(path);
		String operationPath = VABPathTools.buildPath(pathElements, 3);
		String smIdShort = pathElements[2];
		IModelProvider provider = retrieveSubmodelProvider(smIdShort);
		return provider.invokeOperation(operationPath, parameter);
	}

	/**
	 * Check whether the given submodel exists in submodel provider
	 *
	 * @param key
	 *            to search the submodel
	 * @return boolean true/false
	 */
	private boolean isSubmodelLocal(String smIdShort) {
		try {
			getSmAggregator().getSubmodelbyIdShort(smIdShort);
			return true;
		} catch (ResourceNotFoundException exception) {
			return false;
		}
	}

	/**
	 * Check whether a registry exists
	 *
	 * @return boolean true/false
	 */
	private boolean doesRegistryExist() {
		return this.registry != null;
	}

	/**
	 * Get submodel descriptor from the registry
	 *
	 * @param submodelId
	 *            to search the submodel
	 * @return a specifi submodel descriptor
	 */
	private SubmodelDescriptor getSubmodelDescriptorFromRegistry(String submodelIdShort) {
		AASDescriptor aasDescriptor = registry.lookupAAS(aasId);
		SubmodelDescriptor desc = aasDescriptor.getSubmodelDescriptorFromIdShort(submodelIdShort);
		if (desc == null) {
			throw new ResourceNotFoundException("Could not resolve Submodel with idShort " + submodelIdShort + " for AAS " + aasId);
		}
		return desc;
	}

	/**
	 * Get a model provider from a submodel descriptor
	 *
	 * @param submodelDescriptor
	 * @return a model provider
	 */
	private IModelProvider getRemoteSubmodelProvider(SubmodelDescriptor submodelDescriptor) {
		String endpoint = submodelDescriptor.getFirstEndpoint();

		// Remove "/submodel" since it will be readded later
		endpoint = endpoint.substring(0, endpoint.length() - SubmodelProvider.SUBMODEL.length() - 1);

		return getConnectorFactory().create(endpoint);
	}

	/**
	 * Get a model provider from a submodel id
	 *
	 * @param submodelId
	 *            to select a specific submodel
	 * @throws ResourceNotFoundException
	 *             if no registry is found
	 * @return a model provider
	 */
	private IModelProvider getRemoteSubmodelProvider(String submodelId) {
		if (!doesRegistryExist()) {
			throw new ResourceNotFoundException("Submodel with id " + submodelId + " cannot be resolved locally, but no registry is passed");
		}

		SubmodelDescriptor submodelDescriptor = getSubmodelDescriptorFromRegistry(submodelId);
		return getRemoteSubmodelProvider(submodelDescriptor);
	}

	/**
	 * Gets the server URL of a given endpoint. e.g.
	 * http://localhost:1234/x/y/z/aas/submodels/Sm1IdShort would return
	 * http://localhost:1234/x/y/z
	 *
	 * @param endpoint
	 * @return the server URL part of the given endpoint
	 */
	public static String getServerURL(String endpoint) {
		int endServerURL = endpoint.indexOf("/aas");
		// if indexOf returned -1 ("/aas" not present in String)
		// return the whole given path
		if (endServerURL < 0) {
			return endpoint;
		}
		return endpoint.substring(0, endServerURL);
	}

	public ISubmodelAggregator getSmAggregator() {
		return smAggregator;
	}

	public void setSmAggregator(ISubmodelAggregator smAggregator) {
		this.smAggregator = smAggregator;
	}

	public IAASAPIFactory getAasApiProvider() {
		return aasApiProvider;
	}

	public void setAasApiProvider(IAASAPIFactory aasApiProvider) {
		this.aasApiProvider = aasApiProvider;
	}

	public IConnectorFactory getConnectorFactory() {
		return connectorFactory;
	}

	public void setConnectorFactory(IConnectorFactory connectorFactory) {
		this.connectorFactory = connectorFactory;
	}
}
