/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.registration.proxy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.AASRegistryAPIHelper;
import org.eclipse.basyx.aas.registration.restapi.AASRegistryModelProvider;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnector;
import org.eclipse.basyx.vab.registry.proxy.VABRegistryProxy;



/**
 * Local proxy class that hides HTTP calls to BaSys registry
 * 
 * @author kuhn, schnicke
 *
 */
public class AASRegistryProxy extends VABRegistryProxy implements IAASRegistry {

	/**
	 * Constructor for an AAS registry proxy based on a HTTP connection
	 * 
	 * @param registryUrl
	 *            The endpoint of the registry with a HTTP-REST interface
	 */
	public AASRegistryProxy(String registryUrl) {
		this(new JSONConnector(new HTTPConnector(harmonizeURL(registryUrl))));
	}

	/**
	 * Removes prefix if it exists since it will be readded at a later stage
	 * 
	 * @param url
	 * @return
	 */
	private static String harmonizeURL(String url) {
		if (url.endsWith(AASRegistryModelProvider.PREFIX)) {
			url = url.substring(0, url.length() - AASRegistryModelProvider.PREFIX.length());
		}
		return url;
	}

	/**
	 * Constructor for an AAS registry proxy based on its model provider
	 * 
	 * @param provider
	 *            A model provider for the actual registry
	 */
	public AASRegistryProxy(IModelProvider provider) throws ProviderException {
		super(createProxy(provider));
	}

	private static VABElementProxy createProxy(IModelProvider provider) {
		return new VABElementProxy("", provider);
	}

	/**
	 * Register AAS descriptor in registry, delete old registration
	 */
	@Override
	public void register(AASDescriptor deviceAASDescriptor) throws ProviderException {
		// Add a mapping from the AAS id to the serialized descriptor
		try {
			String encodedId = AASRegistryAPIHelper.getAASPath(deviceAASDescriptor.getIdentifier());

			// Typically, VAB SET should not create new entries. Nevertheless, the registry
			// API is defined to do it.
			provider.setValue(encodedId, deviceAASDescriptor);
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}

	/**
	 * Delete AAS descriptor from registry
	 */
	@Override
	public void delete(IIdentifier aasIdentifier) throws ProviderException {
		this.removeMapping(AASRegistryAPIHelper.getAASPath(aasIdentifier));
	}
	
	/**
	 * Lookup device AAS
	 */
	@Override @SuppressWarnings("unchecked")
	public AASDescriptor lookupAAS(IIdentifier aasIdentifier) throws ProviderException {
		try {
			Object result = provider.getValue(AASRegistryAPIHelper.getAASPath(aasIdentifier));
			return new AASDescriptor((Map<String, Object>) result);
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AASDescriptor> lookupAll() throws ProviderException {
		try {
			Object result = provider.getValue(AASRegistryAPIHelper.getRegistryPath());
			Collection<?> descriptors = (Collection<?>) result;
			return descriptors.stream().map(x -> new AASDescriptor((Map<String, Object>) x)).collect(Collectors.toList());
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}

	@Override
	public void register(IIdentifier aas, SubmodelDescriptor smDescriptor) throws ProviderException {
		try {
			// Typically, VAB SET should not create new entries. Nevertheless, the registry
			// API is defined to do it.
			provider.setValue(AASRegistryAPIHelper.getSubmodelAccessPath(aas, smDescriptor.getIdentifier()), smDescriptor);
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}

	@Override
	public void delete(IIdentifier aasId, IIdentifier smId) throws ProviderException {
		try {
			provider.deleteValue(AASRegistryAPIHelper.getSubmodelAccessPath(aasId, smId));
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<SubmodelDescriptor> lookupSubmodels(IIdentifier aasId) throws ProviderException {
		try {
			Object result = provider.getValue(AASRegistryAPIHelper.getSubmodelListOfAASPath(aasId));
			Collection<?> descriptors = (Collection<?>) result;
			return descriptors.stream().map(x -> new SubmodelDescriptor((Map<String, Object>) x)).collect(Collectors.toList());
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier aasId, IIdentifier smId) throws ProviderException {
		try {
			Object result = provider.getValue(AASRegistryAPIHelper.getSubmodelAccessPath(aasId, smId));
			return new SubmodelDescriptor((Map<String, Object>) result);
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}
}

