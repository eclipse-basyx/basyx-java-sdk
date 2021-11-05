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
import org.eclipse.basyx.aas.registration.AASRegistryAPIHelper;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.restapi.BaSyxRegistryPath;
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
 * @author kuhn, schnicke, fischer
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
		if (url.endsWith(BaSyxRegistryPath.PREFIX)) {
			url = url.substring(0, url.length() - BaSyxRegistryPath.PREFIX.length());
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
	 * Registers an AASDescriptor in the registry, if not already existing.
	 *
	 * @param aasDescriptor
	 * @throws ProviderException
	 */
	@Override
	public void register(AASDescriptor aasDescriptor) throws ProviderException {
		// Add a mapping from the AAS id to the serialized descriptor
		try {
			String allAASDescriptorsPath = AASRegistryAPIHelper.getAllShellDescriptorsPath();

			provider.createValue(allAASDescriptorsPath, aasDescriptor);
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}

	/**
	 * Registers a SubmodelDescriptor in the registry with the given identifier.
	 *
	 * @param aasIdentifier
	 * @param submodelDescriptor
	 * @throws ProviderException
	 */
	@Override
	public void register(IIdentifier aasIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {
		try {
			String allSubmodelDescriptorsPath = AASRegistryAPIHelper.getSingleShellDescriptorAllSubmodelDescriptorsPath(aasIdentifier);

			provider.createValue(allSubmodelDescriptorsPath, submodelDescriptor);
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}

	/**
	 * Updates the AASDescriptor in the registry with the given identifier.
	 *
	 * @param aasIdentifier
	 * @param aasDescriptor
	 * @throws ProviderException
	 */
	@Override
	public void update(IIdentifier aasIdentifier, AASDescriptor aasDescriptor) throws ProviderException {
		// Add a mapping from the AAS id to the serialized descriptor
		try {
			String singleAASDescriptorPath = AASRegistryAPIHelper.getSingleShellDescriptorPath(aasDescriptor.getIdentifier());

			provider.setValue(singleAASDescriptorPath, aasDescriptor);
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}

	/**
	 * Deletes the AASDescriptor from the registry with the given identifier.
	 *
	 * @param aasIdentifier
	 * @throws ProviderException
	 */
	@Override
	public void delete(IIdentifier aasIdentifier) throws ProviderException {
		this.removeMapping(AASRegistryAPIHelper.getSingleShellDescriptorPath(aasIdentifier));
	}

	/**
	 * Deletes the SubmodelDescriptor from the registry with the given identifier.
	 *
	 * @param aasIdentifier
	 * @param submodelIdentifier
	 * @throws ProviderException
	 */
	@Override
	public void delete(IIdentifier aasIdentifier, IIdentifier submodelIdentifier) throws ProviderException {
		try {
			provider.deleteValue(AASRegistryAPIHelper.getSingleShellDescriptorSingleSubmodelDescriptorPath(aasIdentifier, submodelIdentifier));
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}

	/**
	 * Looks up the AASDescriptor for the given aasIdentifier.
	 *
	 * @param aasIdentifier
	 * @return AASDescriptor with the given aasIdentifier
	 * @throws ProviderException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public AASDescriptor lookupAAS(IIdentifier aasIdentifier) throws ProviderException {
		try {
			Object result = provider.getValue(AASRegistryAPIHelper.getSingleShellDescriptorPath(aasIdentifier));
			return new AASDescriptor((Map<String, Object>) result);
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}

	/**
	 * Retrieves all registered AASDescriptors.
	 *
	 * @return List of all registered AASDescriptors
	 * @throws ProviderException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AASDescriptor> lookupAll() throws ProviderException {
		try {
			Object result = provider.getValue(AASRegistryAPIHelper.getAllShellDescriptorsPath());
			Collection<?> aasDescriptors = (Collection<?>) result;
			return aasDescriptors.stream().map(x -> new AASDescriptor((Map<String, Object>) x)).collect(Collectors.toList());
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}

	/**
	 * Retrieves all SubmodelDescriptors for the AAS with the given aasIdentifier.
	 *
	 * @param aasIdentifier
	 * @return the list of all SubmodelDescriptors part of the given AAS
	 * @throws ProviderException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SubmodelDescriptor> lookupSubmodels(IIdentifier aasIdentifier) throws ProviderException {
		try {
			Object result = provider.getValue(AASRegistryAPIHelper.getSingleShellDescriptorAllSubmodelDescriptorsPath(aasIdentifier));
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

	/**
	 * Retrieves the SubmodelDescriptor with the given submodelIdentifier that is
	 * part of the AAS with the given aasIdentifier.
	 *
	 * @param aasIdentifier
	 * @param submodelIdentifier
	 * @return the SubmodelDescriptor
	 * @throws ProviderException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier aasIdentifier, IIdentifier submodelIdentifier) throws ProviderException {
		try {
			Object result = provider.getValue(AASRegistryAPIHelper.getSingleShellDescriptorSingleSubmodelDescriptorPath(aasIdentifier, submodelIdentifier));
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
