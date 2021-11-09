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
	 * @param shellDescriptor
	 * @throws ProviderException
	 */
	@Override
	public void register(AASDescriptor shellDescriptor) throws ProviderException {
		try {
			String allAASDescriptorsPath = AASRegistryAPIHelper.getAllShellDescriptorsPath();

			provider.createValue(allAASDescriptorsPath, shellDescriptor);
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}

	/**
	 * Registers a SubmodelDescriptor in the registry, if not already existing.
	 *
	 * @param submodelDescriptor
	 * @throws ProviderException
	 */
	@Override
	public void register(SubmodelDescriptor submodelDescriptor) throws ProviderException {
		try {
			String allSubmodelDescriptorsPath = AASRegistryAPIHelper.getAllSubmodelDescriptorsPath();

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
	 * Registers a SubmodelDescriptor in the registry for a shell with the given
	 * identifier.
	 *
	 * @param shellIdentifier
	 * @param submodelDescriptor
	 * @throws ProviderException
	 */
	@Override
	public void registerSubmodelForShell(IIdentifier shellIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {
		try {
			String allSubmodelDescriptorsPath = AASRegistryAPIHelper.getSingleShellDescriptorAllSubmodelDescriptorsPath(shellIdentifier);

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
	 * Updates a SubmodelDescriptor in the registry for a shell with the given
	 * identifier.
	 *
	 * @param shellIdentifier
	 * @param submodelDescriptor
	 * @throws ProviderException
	 */
	@Override
	public void updateSubmodelForShell(IIdentifier shellIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {
		try {
			String allSubmodelDescriptorsPath = AASRegistryAPIHelper.getSingleShellDescriptorSingleSubmodelDescriptorPath(shellIdentifier, submodelDescriptor.getIdentifier());

			provider.setValue(allSubmodelDescriptorsPath, submodelDescriptor);
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
	 * @param shellIdentifier
	 * @param shellDescriptor
	 * @throws ProviderException
	 */
	@Override
	public void updateShell(IIdentifier shellIdentifier, AASDescriptor shellDescriptor) throws ProviderException {
		try {
			String singleAASDescriptorPath = AASRegistryAPIHelper.getSingleShellDescriptorPath(shellDescriptor.getIdentifier());

			provider.setValue(singleAASDescriptorPath, shellDescriptor);
		} catch (Exception e) {
			if (e instanceof ProviderException) {
				throw (ProviderException) e;
			} else {
				throw new ProviderException(e);
			}
		}
	}

	/**
	 * Updates a SubmodelDescriptor in the registry with the given identifier.
	 *
	 * @param submodelIdentifier
	 * @param submodelDescriptor
	 * @throws ProviderException
	 */
	@Override
	public void updateSubmodel(IIdentifier submodelIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {
		try {
			String singleSubmodelDescriptorsPath = AASRegistryAPIHelper.getSingleSubmodelDescriptorPath(submodelDescriptor.getIdentifier());

			provider.setValue(singleSubmodelDescriptorsPath, submodelDescriptor);
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
	 * @param shellIdentifier
	 * @throws ProviderException
	 */
	@Override
	public void deleteShell(IIdentifier shellIdentifier) throws ProviderException {
		this.removeMapping(AASRegistryAPIHelper.getSingleShellDescriptorPath(shellIdentifier));
	}

	/**
	 * Deletes the SubmodelDescriptor from the registry with the given identifier.
	 *
	 * @param submodelIdentifier
	 * @throws ProviderException
	 */
	@Override
	public void deleteSubmodel(IIdentifier submodelIdentifier) throws ProviderException {
		this.removeMapping(AASRegistryAPIHelper.getSingleSubmodelDescriptorPath(submodelIdentifier));
	}

	/**
	 * Deletes the SubmodelDescriptor from the registry with the given identifier.
	 *
	 * @param shellIdentifier
	 * @param submodelIdentifier
	 * @throws ProviderException
	 */
	@Override
	public void deleteSubmodelFromShell(IIdentifier shellIdentifier, IIdentifier submodelIdentifier) throws ProviderException {
		try {
			provider.deleteValue(AASRegistryAPIHelper.getSingleShellDescriptorSingleSubmodelDescriptorPath(shellIdentifier, submodelIdentifier));
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
	 * @param shellIdentifier
	 * @return AASDescriptor with the given aasIdentifier
	 * @throws ProviderException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public AASDescriptor lookupShell(IIdentifier shellIdentifier) throws ProviderException {
		try {
			Object result = provider.getValue(AASRegistryAPIHelper.getSingleShellDescriptorPath(shellIdentifier));
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
	public List<AASDescriptor> lookupAllShells() throws ProviderException {
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
	 * Looks up the SubmodelDescriptor for the given submodelIdentifier.
	 *
	 * @param submodelIdentifier
	 * @return SubmodelDescriptor with the given submodelIdentifier
	 * @throws ProviderException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public SubmodelDescriptor lookupSubmodel(IIdentifier submodelIdentifier) throws ProviderException {
		try {
			Object result = provider.getValue(AASRegistryAPIHelper.getSingleSubmodelDescriptorPath(submodelIdentifier));
			return new SubmodelDescriptor((Map<String, Object>) result);
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
	public List<SubmodelDescriptor> lookupAllSubmodels() throws ProviderException {
		try {
			Object result = provider.getValue(AASRegistryAPIHelper.getAllSubmodelDescriptorsPath());
			Collection<?> submodelDescriptors = (Collection<?>) result;
			return submodelDescriptors.stream().map(x -> new SubmodelDescriptor((Map<String, Object>) x)).collect(Collectors.toList());
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
	 * @param shellIdentifier
	 * @return the list of all SubmodelDescriptors part of the given AAS
	 * @throws ProviderException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SubmodelDescriptor> lookupAllSubmodelsForShell(IIdentifier shellIdentifier) throws ProviderException {
		try {
			Object result = provider.getValue(AASRegistryAPIHelper.getSingleShellDescriptorAllSubmodelDescriptorsPath(shellIdentifier));
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
	 * @param shellIdentifier
	 * @param submodelIdentifier
	 * @return the SubmodelDescriptor
	 * @throws ProviderException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier shellIdentifier, IIdentifier submodelIdentifier) throws ProviderException {
		try {
			Object result = provider.getValue(AASRegistryAPIHelper.getSingleShellDescriptorSingleSubmodelDescriptorPath(shellIdentifier, submodelIdentifier));
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
