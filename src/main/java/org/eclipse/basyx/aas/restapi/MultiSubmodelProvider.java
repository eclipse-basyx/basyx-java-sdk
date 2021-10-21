/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.restapi;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
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

	/**
	 * Store submodel providers
	 */
	protected Map<String, SubmodelProvider> submodel_providers = new HashMap<>();
	
	/**
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

	/**
	 * Store Submodel API Provider. By default, uses the VAB Submodel Provider
	 */
	protected ISubmodelAPIFactory smApiProvider;

	/**
	 * Constructor with empty default aas and default VAB APIs
	 */
	public MultiSubmodelProvider() {
		this.aasApiProvider = new VABAASAPIFactory();
		this.smApiProvider = new VABSubmodelAPIFactory();
		IAASAPI aasApi = aasApiProvider.getAASApi(new AssetAdministrationShell());
		setAssetAdministrationShell(new AASModelProvider(aasApi));
	}

	/**
	 * Constructor for using custom APIs
	 */
	public MultiSubmodelProvider(AASModelProvider contentProvider, IAASAPIFactory aasApiProvider,
			ISubmodelAPIFactory smApiProvider) {
		this.aasApiProvider = aasApiProvider;
		this.smApiProvider = smApiProvider;
		setAssetAdministrationShell(contentProvider);
	}

	/**
	 * Constructor that accepts an AAS
	 */
	public MultiSubmodelProvider(AASModelProvider contentProvider) {
		this.aasApiProvider = new VABAASAPIFactory();
		this.smApiProvider = new VABSubmodelAPIFactory();
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
	 * @param registry
	 * @param provider
	 */
	public MultiSubmodelProvider(IAASRegistry registry, IConnectorFactory provider) {
		this();
		this.registry = registry;
		this.connectorFactory = provider;
	}
	
	/**
	 * Constructor that accepts a registry, a connection provider and API providers
	 */
	public MultiSubmodelProvider(AASModelProvider contentProvider, IAASRegistry registry,
			IConnectorFactory connectorFactory, ISubmodelAPIFactory smApiProvider, IAASAPIFactory aasApiProvider) {
		this(contentProvider, aasApiProvider, smApiProvider);
		this.registry = registry;
		this.connectorFactory = connectorFactory;
	}

	/**
	 * Constructor that accepts a aas provider, a registry and a connection provider
	 * 
	 * @param contentProvider
	 * @param registry
	 * @param provider
	 */
	public MultiSubmodelProvider(AASModelProvider contentProvider, IAASRegistry registry, HTTPConnectorFactory provider) {
		this(contentProvider);
		this.registry = registry;
		this.connectorFactory = provider;
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
		addSubmodel(sm, modelContentProvider);
	}

	@SuppressWarnings("unchecked")
	private void createSubmodel(Object newSM) throws ProviderException {
		// Adds a new submodel to the registered AAS
		Submodel sm = Submodel.createAsFacade((Map<String, Object>) newSM);

		ISubmodelAPI smApi = smApiProvider.getSubmodelAPI(sm);
		addSubmodel(sm, new SubmodelProvider(smApi));
	}

	private void addSubmodel(Submodel sm, SubmodelProvider modelContentProvider) {
		String smIdShort = sm.getIdShort();
		submodel_providers.put(smIdShort, modelContentProvider);
		aas_provider.createValue("/submodels", sm);
	}

	/**
	 * Remove a provider
	 * 
	 * @param elementId
	 *            Element ID
	 */
	public void removeProvider(String elementId) {
		// Remove model provider
		submodel_providers.remove(elementId);
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
					IModelProvider provider = submodel_providers.get(pathElements[2]);

					if (provider == null) {
						// Get a model provider for the submodel in the registry
						provider = getModelProvider(pathElements[2]);
					}

					// - Retrieve submodel or property value
					return provider.getValue(VABPathTools.buildPath(pathElements, 3));
				}
			} else {
				// Handle access to AAS
				return aas_provider.getValue(VABPathTools.buildPath(pathElements, 1));
			}
		} else {
			throw new MalformedRequestException("The request " + path + " is not allowed for this endpoint");
		}
	}

	/**
	 * Retrieves all submodels of the AAS. If there's a registry, remote Submodels
	 * will also be retrieved.
	 * 
	 * @return
	 * @throws ProviderException
	 */
	@SuppressWarnings("unchecked")
	private Object retrieveSubmodels() throws ProviderException {
		// Make a list and return all local submodels
		Collection<Submodel> submodels = new HashSet<>();
		for (IModelProvider submodel : submodel_providers.values()) {
			submodels.add(Submodel.createAsFacade((Map<String, Object>) submodel.getValue("/submodel")));
		}

		// Check for remote submodels
		if (registry != null) {
			AASDescriptor desc = registry.lookupAAS(aasId);
			
			// Get the address of the AAS e.g. http://localhost:8080
			// This address should be equal to the address of this server
			String aasEndpoint = desc.getFirstEndpoint();
			String aasServerURL = getServerURL(aasEndpoint);
			
			List<String> localIds = submodels.stream().map(sm -> sm.getIdentification().getId()).collect(Collectors.toList());
			List<IIdentifier> missingIds = desc.getSubmodelDescriptors().stream().map(d -> d.getIdentifier()).
					filter(id -> !localIds.contains(id.getId())).collect(Collectors.toList());
			
			if(!missingIds.isEmpty()) {
				List<String> missingEndpoints = missingIds.stream().map(id -> desc.getSubmodelDescriptorFromIdentifierId(id.getId()))
						.map(smDesc -> smDesc.getFirstEndpoint()).collect(Collectors.toList());
				
				// Check if any of the missing Submodels have the same address as the AAS.
				// This would mean, that the Submodel should be present on the same
				// server of the AAS but is not
				
				// If this error would not be caught here an endless loop would develop
				// as the registry would be asked for this Submodel and then it would be requested
				// from this server again, which would ask the registry about it again
				
				// Such a situation might originate from a deleted but not unregistered Submodel
				// or from a manually registered but never pushed Submodel
				for(String missingEndpoint: missingEndpoints) {
					if(getServerURL(missingEndpoint).equals(aasServerURL)) {
						throw new ResourceNotFoundException("The Submodel at Endpoint '" + missingEndpoint + 
								"' does not exist on this server. It seems to be registered but not actually present.");
					}
				}
				
				List<Submodel> remoteSms = missingEndpoints.stream().map(endpoint -> connectorFactory.getConnector(endpoint)).
						map(p -> (Map<String, Object>) p.getValue("")).map(m -> Submodel.createAsFacade(m)).collect(Collectors.toList());
				submodels.addAll(remoteSms);
			}
		}

		return submodels;
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
			IModelProvider provider;
			if (isSubmodelLocal(pathElements[2])) {
				provider = submodel_providers.get(pathElements[2]);
			} else {
				// Get a model provider for the submodel in the registry
				provider = getModelProvider(pathElements[2]);
			}
			provider.setValue(propertyPath, newValue);
		}
	}

	@Override
	public void createValue(String path, Object newValue) throws ProviderException {
		throw new MalformedRequestException("Create is not supported by VABMultiSubmodelProvider. Path was: " + path);
	}


	@SuppressWarnings("unchecked")
	private void createAssetAdministrationShell(Object newAAS) {
		Map<String, Object> aas = (Map<String, Object>) newAAS;
		AssetAdministrationShell shell = AssetAdministrationShell.createAsFacade(aas);
		IAASAPI aasApi = aasApiProvider.getAASApi(shell);
		aas_provider = new AASModelProvider(aasApi);
	}


	@SuppressWarnings("unchecked")
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

			// Delete submodel reference from aas
			// TODO: This is a hack until the API is further clarified
			Submodel sm = Submodel.createAsFacade((Map<String, Object>) submodel_providers.get(smIdShort).getValue("/" + SubmodelProvider.SUBMODEL));
			aas_provider.deleteValue(SUBMODELS_PREFIX + "/" + sm.getIdentification().getId());

			// Remove submodel provider
			submodel_providers.remove(smIdShort);
		} else if (propertyPath.length() > 0) {
			IModelProvider provider;
			if (isSubmodelLocal(pathElements[2])) {
				provider = submodel_providers.get(pathElements[2]);
			} else {
				// Get a model provider for the submodel in the registry
				provider = getModelProvider(pathElements[2]);
			}

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

		IModelProvider provider;
		if (isSubmodelLocal(pathElements[2])) {
			provider = submodel_providers.get(pathElements[2]);
		} else {
			// Get a model provider for the submodel in the registry
			provider = getModelProvider(pathElements[2]);
		}

		return provider.invokeOperation(operationPath, parameter);
	}
	
	/**
	 * Check whether the given submodel exists in submodel provider
	 * @param key to search the submodel
	 * @return boolean true/false
	 */
	private boolean isSubmodelLocal(String submodelId) {
		return submodel_providers.containsKey(submodelId);
	}
	
	/**
	 * Check whether a registry exists
	 * @return boolean true/false
	 */
	private boolean doesRegistryExist() {
		return this.registry != null;
	}
	
	/**
	 * Get submodel descriptor from the registry
	 * @param submodelId to search the submodel
	 * @return a specifi submodel descriptor
	 */
	private SubmodelDescriptor getSubmodelDescriptorFromRegistry(String submodelIdShort) {
		AASDescriptor aasDescriptor = registry.lookupAAS(aasId);
		SubmodelDescriptor desc = aasDescriptor.getSubmodelDescriptorFromIdShort(submodelIdShort);
		if(desc == null) {
			throw new ResourceNotFoundException("Could not resolve Submodel with idShort " + submodelIdShort + " for AAS " + aasId);
		}
		return desc;
	}
	
	/**
	 * Get a model provider from a submodel descriptor
	 * @param submodelDescriptor
	 * @return a model provider
	 */
	private IModelProvider getModelProvider(SubmodelDescriptor submodelDescriptor) {
		String endpoint = submodelDescriptor.getFirstEndpoint();

		// Remove "/submodel" since it will be readded later
		endpoint = endpoint.substring(0, endpoint.length() - SubmodelProvider.SUBMODEL.length() - 1);

		return connectorFactory.getConnector(endpoint);
	}
	
	/**
	 * Get a model provider from a submodel id
	 * @param submodelId to select a specific submodel
	 * @throws ResourceNotFoundException if no registry is found
	 * @return a model provider
	 */
	private IModelProvider getModelProvider(String submodelId) {
		if (!doesRegistryExist()) {
			throw new ResourceNotFoundException("Submodel with id " + submodelId + " cannot be resolved locally, but no registry is passed");	
		}
		
		SubmodelDescriptor submodelDescriptor = getSubmodelDescriptorFromRegistry(submodelId);
		return getModelProvider(submodelDescriptor);
	}
	
	/**
	 * Gets the server URL of a given endpoint.
	 * e.g. http://localhost:1234/x/y/z/aas/submodels/Sm1IdShort would return
	 * http://localhost:1234/x/y/z
	 * 
	 * @param endpoint
	 * @return the server URL part of the given endpoint
	 */
	public static String getServerURL(String endpoint) {
		int endServerURL = endpoint.indexOf("/aas");
		// if indexOf returned -1 ("/aas" not present in String)
		// return the whole given path
		if(endServerURL < 0) {
			return endpoint;
		}
		return endpoint.substring(0, endServerURL);
	}
}
