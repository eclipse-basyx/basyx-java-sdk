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
package org.eclipse.basyx.aas.manager;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.aggregator.AASAggregatorAPIHelper;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.manager.api.IAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedElement;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedSubmodel;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.factory.java.ModelProxyFactory;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement a AAS manager backend that communicates via HTTP/REST<br>
 * <br>
 * 
 * @author kuhn, schnicke, danish
 * 
 */
public class ConnectedAssetAdministrationShellManager implements IAssetAdministrationShellManager {
	
	private Logger logger = LoggerFactory.getLogger(ConnectedAssetAdministrationShell.class);

	protected IAASRegistry aasDirectory;
	protected IConnectorFactory connectorFactory;
	protected ModelProxyFactory proxyFactory;

	/**
	 * Creates a manager assuming an HTTP connection
	 * 
	 * @param directory
	 */
	public ConnectedAssetAdministrationShellManager(IAASRegistry directory) {
		this(directory, new HTTPConnectorFactory());
	}

	/**
	 * @param directory
	 * @param provider
	 */
	public ConnectedAssetAdministrationShellManager(IAASRegistry directory, IConnectorFactory provider) {
		this.aasDirectory = directory;
		this.connectorFactory = provider;
		this.proxyFactory = new ModelProxyFactory(provider);
	}

	@Override
	public ISubmodel retrieveSubmodel(IIdentifier aasId, IIdentifier smId) {
		VABElementProxy proxy = getSubmodelProxyFromId(aasId, smId);
		return new ConnectedSubmodel(proxy);
	}

	@Override
	public ConnectedAssetAdministrationShell retrieveAAS(IIdentifier aasId) {
		VABElementProxy proxy = getAASProxyFromId(aasId);
		return new ConnectedAssetAdministrationShell(proxy);
	}

	@Override
	public Map<String, ISubmodel> retrieveSubmodels(IIdentifier aasId) {
		AASDescriptor aasDesc = aasDirectory.lookupAAS(aasId);
		Collection<SubmodelDescriptor> smDescriptors = aasDesc.getSubmodelDescriptors();
		Map<String, ISubmodel> submodels = new LinkedHashMap<>();
		for (SubmodelDescriptor smDesc : smDescriptors) {
			String smIdShort = smDesc.getIdShort();
			ISubmodel connectedSM = retrieveSubmodel(aasDesc.getIdentifier(), smDesc.getIdentifier());
			submodels.put(smIdShort, connectedSM);
		}
		
		return submodels;
	}

	/**
	 * Retrieves all AASs registered. This can take a long time if many AASs are
	 * present! Use with caution!
	 * 
	 * @return all AASs registered
	 */
	@Override
	public Collection<IAssetAdministrationShell> retrieveAASAll() {
		List<AASDescriptor> aasDescriptors = aasDirectory.lookupAll();
		return aasDescriptors.stream().map(d -> retrieveAAS(d.getIdentifier())).collect(Collectors.toList());
	}

	@Override
	public void deleteAAS(IIdentifier id) {
		// Lookup AAS descriptor
		AASDescriptor aasDescriptor = aasDirectory.lookupAAS(id);

		// Get AAS address from AAS descriptor
		String addr = aasDescriptor.getFirstEndpoint();

		// Address ends in "/aas", has to be stripped for removal
		addr = VABPathTools.stripSlashes(addr);
		addr = addr.substring(0, addr.length() - "/aas".length());

		// Delete from server
		proxyFactory.createProxy(addr).deleteValue("");

		// Delete from Registry
		deleteAasFromDirectoryIfPresent(id);

		// TODO: How to handle submodels -> Lifecycle needs to be clarified
	}

	@Override
	public void createSubmodel(IIdentifier aasId, Submodel submodel) {

		// Push the SM to the server using the ConnectedAAS

		retrieveAAS(aasId).addSubmodel(submodel);

		// Lookup AAS descriptor
		AASDescriptor aasDescriptor = aasDirectory.lookupAAS(aasId);

		// Get aas endpoint
		String addr = aasDescriptor.getFirstEndpoint();

		// Register the SM
		String smEndpoint = VABPathTools.concatenatePaths(addr, AssetAdministrationShell.SUBMODELS, submodel.getIdShort(), SubmodelProvider.SUBMODEL);
		aasDirectory.register(aasId, new SubmodelDescriptor(submodel, smEndpoint));
	}

	@Override
	public void deleteSubmodel(IIdentifier aasId, IIdentifier submodelId) {
		IAssetAdministrationShell shell = retrieveAAS(aasId);
		shell.removeSubmodel(submodelId);

		deleteSubmodelFromDirectoryIfPresent(aasId, submodelId);
	}

	@Override
	public void createAAS(AssetAdministrationShell aas, String endpoint) {
		String harmonizedEndpoint = AASAggregatorAPIHelper.harmonizeURL(endpoint);

		IModelProvider provider = connectorFactory.create(harmonizedEndpoint);
		AASAggregatorProxy proxy = new AASAggregatorProxy(provider);
		proxy.createAAS(aas);
		String combinedEndpoint = VABPathTools.concatenatePaths(harmonizedEndpoint, AASAggregatorAPIHelper.getAASAccessPath(aas.getIdentification()));
		aasDirectory.register(new AASDescriptor(aas, combinedEndpoint));
	}
	
	private void deleteAasFromDirectoryIfPresent(IIdentifier aasId) {
		try {
			aasDirectory.delete(aasId);
		} catch (ResourceNotFoundException e) {
			logger.info("The AAS with id {} does not exist in the registry.", aasId.getId());
		}
	}
	
	private void deleteSubmodelFromDirectoryIfPresent(IIdentifier aasId, IIdentifier submodelId) {
		try {
			aasDirectory.delete(aasId, submodelId);
		} catch (ResourceNotFoundException e) {
			logger.info("The submodel with id {} does not exist in the registry.", submodelId.getId());
		}
	}
	
	private VABElementProxy getAASProxyFromId(IIdentifier aasId) {
		AASDescriptor aasDescriptor = aasDirectory.lookupAAS(aasId);
		
		Optional<Map<String, Object>> optionalAasDescriptor = getWorkingAasEndpoint(aasDescriptor.getEndpoints());
		
		if (!optionalAasDescriptor.isPresent())
			throw new ResourceNotFoundException("The resource with id : " + aasId + " could not be found!");
		
		return proxyFactory.createProxy((String) optionalAasDescriptor.get().get(AssetAdministrationShell.ADDRESS));
	}
	
	private VABElementProxy getSubmodelProxyFromId(IIdentifier aasId, IIdentifier smId) {
		SubmodelDescriptor smDescriptor = aasDirectory.lookupSubmodel(aasId, smId);

		Optional<Map<String, Object>> optionalSubmodelDescriptor = getWorkingSubmodelEndpoint(smDescriptor.getEndpoints());
		
		if (!optionalSubmodelDescriptor.isPresent())
			throw new ResourceNotFoundException("The resource with id : " + aasId + " could not be found!");

		return proxyFactory.createProxy((String) optionalSubmodelDescriptor.get().get(AssetAdministrationShell.ADDRESS));
	}

	private Optional<Map<String, Object>> getWorkingAasEndpoint(Collection<Map<String, Object>> endpoints) {
		return endpoints.stream().filter(endpoint -> isWorking(new ConnectedAssetAdministrationShell(createProxy(endpoint)))).findFirst();
	}
	
	private Optional<Map<String, Object>> getWorkingSubmodelEndpoint(Collection<Map<String, Object>> endpoints) {
		return endpoints.stream().filter(endpoint -> isWorking(new ConnectedSubmodel(createProxy(endpoint)))).findFirst();
	}
	
	private VABElementProxy createProxy(Map<String, Object> endpoint) {
		return proxyFactory.createProxy((String) endpoint.get(AssetAdministrationShell.ADDRESS));
	}

	private boolean isWorking(ConnectedElement connectedElement) {
		
		try {
			attemptIdentificationRetrieval(connectedElement);
			
			return true;
		} catch (ProviderException e) {
			return false;
		}
		
	}

	private void attemptIdentificationRetrieval(ConnectedElement connectedElement) {
		if (connectedElement instanceof ConnectedAssetAdministrationShell) {
			((ConnectedAssetAdministrationShell) connectedElement).getIdentification();
		} else {
			((ConnectedSubmodel) connectedElement).getIdentification();
		}
	}

}
