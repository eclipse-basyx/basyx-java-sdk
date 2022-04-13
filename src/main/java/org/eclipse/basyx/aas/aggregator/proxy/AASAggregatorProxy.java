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
package org.eclipse.basyx.aas.aggregator.proxy;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.aggregator.AASAggregatorAPIHelper;
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
	protected IModelProvider provider;
	private static Logger logger = LoggerFactory.getLogger(AASRegistryProxy.class);

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
	 * Removes the "/shells" suffix if it exists
	 * 
	 * @param url
	 * @return
	 */
	protected static String harmonizeURL(String url) {
		return VABPathTools.stripFromPath(url, AASAggregatorProvider.PREFIX);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IAssetAdministrationShell> getAASList() {
		Collection<Map<String, Object>> collection = (Collection<Map<String, Object>>) provider.getValue(AASAggregatorAPIHelper.getAggregatorPath());
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
		String path = AASAggregatorAPIHelper.getAASAccessPath(aasId);
		VABElementProxy proxy = new VABElementProxy(path, provider);
		return proxy;
	}

	@Override
	public void createAAS(AssetAdministrationShell aas) {
		provider.setValue(AASAggregatorAPIHelper.getAASEntryPath(aas.getIdentification()), aas);
		logger.info("AAS with Id " + aas.getIdentification().getId() + " created");
	}

	@Override
	public void updateAAS(AssetAdministrationShell aas) {
		provider.setValue(AASAggregatorAPIHelper.getAASEntryPath(aas.getIdentification()), aas);
		logger.info("AAS with Id " + aas.getIdentification().getId() + " updated");
	}

	@Override
	public void deleteAAS(IIdentifier aasId) {
		provider.deleteValue(AASAggregatorAPIHelper.getAASEntryPath(aasId));
		logger.info("AAS with Id " + aasId.getId() + " deleted");
	}

	@Override
	public IModelProvider getAASProvider(IIdentifier aasId) {
		return new VABElementProxy(AASAggregatorAPIHelper.getAASEntryPath(aasId), provider);
	}
}
