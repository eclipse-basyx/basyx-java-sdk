/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.aggregator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.aas.restapi.vab.VABAASAPIFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;

/**
 * An implementation of the IAASAggregator interface using maps internally
 * 
 * @author conradi, schnicke
 *
 */
public class AASAggregator implements IAASAggregator {

	protected Map<String, MultiSubmodelProvider> aasProviderMap = new HashMap<>();

	protected IAASRegistry registry;

	/**
	 * Store AAS API Provider. By default, uses the VAB API Provider
	 */
	protected IAASAPIFactory aasApiProvider;

	/**
	 * Store Submodel API Provider. By default, uses the VAB Submodel Provider
	 */
	protected ISubmodelAPIFactory smApiProvider;

	/**
	 * Constructs default AAS Aggregator
	 */
	public AASAggregator() {
		this.aasApiProvider = new VABAASAPIFactory();
		this.smApiProvider = new VABSubmodelAPIFactory();
	}

	/**
	 * Constructs an AAS aggregator with custom API providers
	 */
	public AASAggregator(IAASAPIFactory aasApiProvider, ISubmodelAPIFactory smApiProvider) {
		this.aasApiProvider = aasApiProvider;
		this.smApiProvider = smApiProvider;
	}

	/**
	 * Constructs AAS Aggregator using the passed registry. This registry is used to
	 * resolve requests for remote submodels
	 * 
	 * @param registry
	 */
	public AASAggregator(IAASRegistry registry) {
		this.registry = registry;
		this.aasApiProvider = new VABAASAPIFactory();
		this.smApiProvider = new VABSubmodelAPIFactory();
	}

	/**
	 * Constructs AAS Aggregator using the passed registry. This registry is used to
	 * resolve requests for remote submodels. Additionally takes custom API providers;
	 */
	public AASAggregator(IAASAPIFactory aasApiProvider, ISubmodelAPIFactory smApiProvider,
			IAASRegistry registry) {
		this.registry = registry;
		this.aasApiProvider = aasApiProvider;
		this.smApiProvider = smApiProvider;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IAssetAdministrationShell> getAASList() {
		return aasProviderMap.values().stream().map(p -> {
			try {
				return p.getValue("/aas");
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new RuntimeException();
			}
		}).map(m -> {
			AssetAdministrationShell aas = new AssetAdministrationShell();
			aas.putAll((Map<? extends String, ? extends Object>) m);
			return aas;
		}).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public IAssetAdministrationShell getAAS(IIdentifier aasId) {
		IModelProvider aasProvider = getAASProvider(aasId);

		// get all Elements from provider
		Map<String, Object> aasMap = (Map<String, Object>) aasProvider.getValue("/aas");
		IAssetAdministrationShell aas = AssetAdministrationShell.createAsFacade(aasMap);

		return aas;
	}

	@Override
	public void createAAS(AssetAdministrationShell aas) {
		aasProviderMap.put(aas.getIdentification().getId(), createMultiSubmodelProvider(aas));
	}

	@Override
	public void updateAAS(AssetAdministrationShell aas) {
		aasProviderMap.put(aas.getIdentification().getId(), createMultiSubmodelProvider(aas));
	}

	private MultiSubmodelProvider createMultiSubmodelProvider(AssetAdministrationShell aas) {
		IConnectorFactory connProvider = new HTTPConnectorFactory();
		IAASAPI aasApi = aasApiProvider.getAASApi(aas);
		AASModelProvider contentProvider = new AASModelProvider(aasApi);
		MultiSubmodelProvider multiAASProvider = new MultiSubmodelProvider(contentProvider, registry,
				connProvider, smApiProvider, aasApiProvider);
		return multiAASProvider;
	}

	@Override
	public void deleteAAS(IIdentifier aasId) {
		aasProviderMap.remove(aasId.getId());
	}

	@Override
	public IModelProvider getAASProvider(IIdentifier aasId) {
		MultiSubmodelProvider provider = aasProviderMap.get(aasId.getId());

		if (provider == null) {
			throw new ResourceNotFoundException("AAS with Id " + aasId.getId() + " does not exist");
		}

		return provider;
	}
}
