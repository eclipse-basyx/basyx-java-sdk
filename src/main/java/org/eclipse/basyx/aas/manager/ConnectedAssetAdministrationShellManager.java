/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.aas.aggregator.AASAggregatorAPIHelper;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.manager.api.IAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.parts.Endpoint;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedSubmodel;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.vab.exception.FeatureNotImplementedException;
import org.eclipse.basyx.vab.factory.java.ModelProxyFactory;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;

/**
 * Implement a AAS manager backend that communicates via HTTP/REST<br>
 * <br>
 *
 * @author kuhn, schnicke
 *
 */
public class ConnectedAssetAdministrationShellManager implements IAssetAdministrationShellManager {

	protected IAASRegistry shellDirectory;
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
		this.shellDirectory = directory;
		this.connectorFactory = provider;
		this.proxyFactory = new ModelProxyFactory(provider);
	}

	@Override
	public ISubmodel retrieveSubmodel(IIdentifier aasId, IIdentifier smId) {
		// look up SM descriptor in the registry
		SubmodelDescriptor submodelDescriptor = shellDirectory.lookupSubmodel(aasId, smId);

		// get address of the submodel descriptor
		String submodelEndpointAddress = extractFirstEndpointAddress(submodelDescriptor);

		// Return a new VABElementProxy
		return new ConnectedSubmodel(proxyFactory.createProxy(submodelEndpointAddress));
	}

	@Override
	public ConnectedAssetAdministrationShell retrieveAAS(IIdentifier aasId) {
		VABElementProxy proxy = getAASProxyFromId(aasId);
		return new ConnectedAssetAdministrationShell(proxy);
	}

	@Override
	public Map<String, ISubmodel> retrieveSubmodels(IIdentifier aasId) {
		AASDescriptor shellDescriptor = shellDirectory.lookupShell(aasId);
		Collection<SubmodelDescriptor> smDescriptors = shellDescriptor.getSubmodelDescriptors();
		Map<String, ISubmodel> submodels = new HashMap<>();

		for (SubmodelDescriptor smDesc : smDescriptors) {
			String smIdShort = smDesc.getIdShort();

			String smEndpoint = extractFirstEndpointAddress(smDesc);
			VABElementProxy smProxy = proxyFactory.createProxy(smEndpoint);
			ConnectedSubmodel connectedSM = new ConnectedSubmodel(smProxy);

			submodels.put(smIdShort, connectedSM);
		}
		return submodels;
	}

	private VABElementProxy getAASProxyFromId(IIdentifier aasId) {
		AASDescriptor shellDescriptor = shellDirectory.lookupShell(aasId);

		String shellAddress = extractFirstEndpointAddress(shellDescriptor);

		return proxyFactory.createProxy(shellAddress);
	}

	@Override
	public Collection<IAssetAdministrationShell> retrieveAASAll() {
		throw new FeatureNotImplementedException();
	}

	@Override
	public void deleteAAS(IIdentifier id) {
		// Lookup AAS descriptor
		AASDescriptor shellDescriptor = shellDirectory.lookupShell(id);

		String shellEndpointAddress = extractFirstEndpointAddress(shellDescriptor);

		shellEndpointAddress = prepareShellEndpointAddress(shellEndpointAddress);

		// Delete from server
		proxyFactory.createProxy(shellEndpointAddress).deleteValue("");

		// Delete from Registry
		shellDirectory.deleteModel(id);

		// TODO: How to handle submodels -> Lifecycle needs to be clarified
	}

	private String prepareShellEndpointAddress(String shellAddress) {
		// Address ends in "/aas", has to be stripped for removal
		shellAddress = VABPathTools.stripSlashes(shellAddress);
		shellAddress = shellAddress.substring(0, shellAddress.length() - "/aas".length());
		return shellAddress;
	}

	@Override
	public void createSubmodel(IIdentifier shellIdentifier, Submodel submodel) {

		// Push the SM to the server using the ConnectedAAS

		retrieveAAS(shellIdentifier).addSubmodel(submodel);

		AASDescriptor shellDescriptor = shellDirectory.lookupShell(shellIdentifier);

		String shellEndpointAddress = extractFirstEndpointAddress(shellDescriptor);

		registerSubmodel(shellIdentifier, submodel, shellEndpointAddress);
	}

	private void registerSubmodel(IIdentifier shellIdentifier, Submodel submodel, String shellEndpointAddress) {
		String submodelEndpointPath = VABPathTools.concatenatePaths(shellEndpointAddress, AssetAdministrationShell.SUBMODELS, submodel.getIdShort(), SubmodelProvider.SUBMODEL);
		shellDirectory.registerSubmodelForShell(shellIdentifier, new SubmodelDescriptor(submodel, new Endpoint(submodelEndpointPath)));
	}

	@Override
	public void deleteSubmodel(IIdentifier shellIdentifier, IIdentifier submodelIdentifier) {
		IAssetAdministrationShell shell = retrieveAAS(shellIdentifier);
		shell.removeSubmodel(submodelIdentifier);

		shellDirectory.deleteSubmodelFromShell(shellIdentifier, submodelIdentifier);
	}

	@Override
	public void createShell(AssetAdministrationShell shell, String endpoint) {
		endpoint = VABPathTools.stripSlashes(endpoint);

		IModelProvider provider = connectorFactory.getConnector(endpoint);
		AASAggregatorProxy proxy = new AASAggregatorProxy(provider);

		proxy.createShell(shell);

		registerShell(shell, endpoint);
	}

	private void registerShell(AssetAdministrationShell shell, String endpoint) {
		String combinedEndpoint = VABPathTools.concatenatePaths(endpoint, AASAggregatorAPIHelper.getAASAccessPath(shell.getIdentification()));
		shellDirectory.register(new AASDescriptor(shell, new Endpoint(combinedEndpoint)));
	}

	private String extractFirstEndpointAddress(ModelDescriptor descriptor) {
		return descriptor.getFirstEndpoint().getProtocolInformation().getEndpointAddress();
	}
}
