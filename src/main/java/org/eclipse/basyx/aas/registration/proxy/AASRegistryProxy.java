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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.restapi.AASRegistryModelProvider;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnector;
import org.eclipse.basyx.vab.registry.proxy.VABRegistryProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Local proxy class that hides HTTP calls to BaSys registry
 * 
 * @author kuhn, schnicke
 *
 */
public class AASRegistryProxy extends VABRegistryProxy implements IAASRegistry {
	
	private static Logger logger = LoggerFactory.getLogger(AASRegistryProxy.class);

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
		return new VABElementProxy(AASRegistryModelProvider.PREFIX, provider);
	}

	/**
	 * Register AAS descriptor in registry, delete old registration
	 */
	@Override
	public void register(AASDescriptor deviceAASDescriptor) throws ProviderException {
		// Add a mapping from the AAS id to the serialized descriptor
		try {
			String encodedId = VABPathTools.encodePathElement(deviceAASDescriptor.getIdentifier().getId());

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
		try {
			this.removeMapping(URLEncoder.encode(aasIdentifier.getId(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("Could not encode URL. This should not happen");
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Lookup device AAS
	 */
	@Override @SuppressWarnings("unchecked")
	public AASDescriptor lookupAAS(IIdentifier aasIdentifier) throws ProviderException {
		try {
			Object result = provider.getValue(URLEncoder.encode(aasIdentifier.getId(), "UTF-8"));
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
			Object result = provider.getValue("");
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
			provider.setValue(VABPathTools.concatenatePaths(buildSubmodelPath(aas), URLEncoder.encode(smDescriptor.getIdentifier().getId(), "UTF-8")), smDescriptor);
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
			provider.deleteValue(VABPathTools.concatenatePaths(buildSubmodelPath(aasId), URLEncoder.encode(smId.getId(), "UTF-8")));
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}

	private String buildSubmodelPath(IIdentifier aas) throws ProviderException {
		// Encode id to handle usage of reserved symbols, e.g. /
		String encodedAASId = VABPathTools.encodePathElement(aas.getId());
		return VABPathTools.concatenatePaths(encodedAASId, AASRegistryModelProvider.SUBMODELS);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SubmodelDescriptor> lookupSubmodels(IIdentifier aasId) throws ProviderException {
		try {
			Object result = provider.getValue(VABPathTools.concatenatePaths(buildSubmodelPath(aasId)));
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
			Object result = provider.getValue(VABPathTools.concatenatePaths(buildSubmodelPath(aasId), URLEncoder.encode(smId.getId(), "UTF-8")));
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

