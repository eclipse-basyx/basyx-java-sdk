/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.aggregator.proxy;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AASAggregatorProxy implements IAASAggregator {
	private static Logger logger = LoggerFactory.getLogger(AASRegistryProxy.class);
	private IModelProvider provider;
	
	/**
	 * Constructor for an AAS aggregator proxy based on a HTTP connection
	 * 
	 * @param aasAggregatorURL
	 *            The endpoint of the aggregator with a HTTP-REST interface
	 */
	public AASAggregatorProxy(String aasAggregatorURL) {
		this(new JSONConnector(new HTTPConnector(harmonizeURL(aasAggregatorURL))));
	}

	/**
	 * Constructor for an AAS aggregator proxy based on an arbitrary
	 * {@link IModelProvider}
	 * 
	 * @param provider
	 */
	public AASAggregatorProxy(IModelProvider provider) {
		this.provider = new VABElementProxy("", provider);
	}

	/**
	 * Adds the "/shells" suffix if it does not exist
	 * 
	 * @param url
	 * @return
	 */
	private static String harmonizeURL(String url) {
		return VABPathTools.harmonizePathWithSuffix(url, AASAggregatorProvider.PREFIX);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IAssetAdministrationShell> getAASList() {
		Collection<Map<String, Object>> collection = (Collection<Map<String, Object>>) provider.getValue("");
		logger.debug("Getting all AAS");
		return collection.stream().map(m -> AssetAdministrationShell.createAsFacade(m)).map(aas -> getConnectedAAS(aas.getIdentification(), aas)).collect(Collectors.toList());
	}

	@Override
	public IAssetAdministrationShell getAAS(IIdentifier aasId) {
		logger.debug("Getting AAS with id " + aasId);
		return getConnectedAAS(aasId);
	}

	@SuppressWarnings("unchecked")
	private ConnectedAssetAdministrationShell getConnectedAAS(IIdentifier aasId) {
		VABElementProxy proxy = getAASProxy(aasId);
		Map<String, Object> map = (Map<String, Object>) proxy.getValue("");
		AssetAdministrationShell aas = AssetAdministrationShell.createAsFacade(map);
		return new ConnectedAssetAdministrationShell(proxy, aas);
	}

	private ConnectedAssetAdministrationShell getConnectedAAS(IIdentifier aasId, AssetAdministrationShell localCopy) {
		VABElementProxy proxy = getAASProxy(aasId);
		return new ConnectedAssetAdministrationShell(proxy, localCopy);
	}


	private VABElementProxy getAASProxy(IIdentifier aasId) {
		String path = VABPathTools.concatenatePaths(getEncodedIdentifier(aasId), "aas");
		VABElementProxy proxy = new VABElementProxy(path, provider);
		return proxy;
	}

	@Override
	public void createAAS(AssetAdministrationShell aas) {
		provider.setValue(getEncodedIdentifier(aas.getIdentification()), aas);
		logger.info("AAS with Id " + aas.getIdentification().getId() + " created");
	}

	@Override
	public void updateAAS(AssetAdministrationShell aas) {
		provider.setValue(getEncodedIdentifier(aas.getIdentification()), aas);
		logger.info("AAS with Id " + aas.getIdentification().getId() + " updated");
	}

	@Override
	public void deleteAAS(IIdentifier aasId) {
		provider.deleteValue(getEncodedIdentifier(aasId));
		logger.info("AAS with Id " + aasId.getId() + " deleted");
	}

	@Override
	public IModelProvider getAASProvider(IIdentifier aasId) {
		return new VABElementProxy(getEncodedIdentifier(aasId), provider);
	}

	private String getEncodedIdentifier(IIdentifier aasId) {
		return VABPathTools.encodePathElement(aasId.getId());
	}

}
