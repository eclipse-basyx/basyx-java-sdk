/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.registry.restapi;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;

/**
 * API server helper for AAS Registry Path reading
 *
 * @author fischer, fried, jung
 *
 */
public class RegistryServerAPIHelper {
	public static final String PREFIX = "registry";
	public static final String SHELL_DESCRIPTORS = "shell-descriptors";
	public static final String SUBMODEL_DESCRIPTORS = "submodel-descriptors";

	private static final String ENCODING = "UTF-8";

	private RegistryPath registryPath;

	/**
	 * Splits string into multiple parts as registry v2 specifies
	 *
	 * @param path
	 */
	public RegistryServerAPIHelper(String path) {
		if (path.isEmpty()) {
			throw new MalformedRequestException("A correct registry path must be given.");
		}

		registryPath = new RegistryPath(path);
		checkIfPathPrefixIsValid(registryPath);
		checkIfFirstDescriptorIsValid(registryPath);
		checkIfSecondDescriptorIsValid(registryPath);
	}

	private void checkIfPathPrefixIsValid(RegistryPath registryPath) {
		if (!registryPath.getPathPrefix().equals(PREFIX)) {
			throw new MalformedRequestException("Registry path must start with " + PREFIX);
		}
	}

	private void checkIfFirstDescriptorIsValid(RegistryPath registryPath) {
		if (!(registryPath.getFirstDescriptorType().equals(SHELL_DESCRIPTORS) || registryPath.getFirstDescriptorType().equals(SUBMODEL_DESCRIPTORS))) {
			throw new MalformedRequestException("After " + PREFIX + "the path must continue with " + SHELL_DESCRIPTORS + " or " + SUBMODEL_DESCRIPTORS);
		}
	}

	private void checkIfSecondDescriptorIsValid(RegistryPath registryPath) {
		if (!(registryPath.getSecondDescriptorType() == null || registryPath.getSecondDescriptorType().equals(SUBMODEL_DESCRIPTORS))) {
			throw new MalformedRequestException("Second path element must be (if present): " + SUBMODEL_DESCRIPTORS);
		}
	}

	private boolean hasFirstDescriptorForShell() {
		return registryPath.getFirstDescriptorType().equals(SHELL_DESCRIPTORS);
	}

	private boolean hasFirstDescriptorForSubmodel() {
		return registryPath.getFirstDescriptorType().equals(SUBMODEL_DESCRIPTORS);
	}

	private boolean hasFirstDescriptorId() {
		return registryPath.getFirstDescriptorId() != null;
	}

	private boolean hasSecondDescriptorForSubmodel() {
		return registryPath.getSecondDescriptorType() != null && registryPath.getSecondDescriptorType().equals(SUBMODEL_DESCRIPTORS);
	}

	private boolean hasSecondDescriptorId() {
		return registryPath.getSecondDescriptorId() != null;
	}

	public boolean isAllShellDescriptorsPath() {
		return hasFirstDescriptorForShell() && !hasFirstDescriptorId() && !hasSecondDescriptorForSubmodel() && !hasSecondDescriptorId();
	}

	public boolean isSingleShellDescriptorPath() {
		return hasFirstDescriptorForShell() && hasFirstDescriptorId() && !hasSecondDescriptorForSubmodel() && !hasSecondDescriptorId();
	}

	public boolean isSingleShellDescriptorAllSubmodelDescriptorsPath() {
		return hasFirstDescriptorForShell() && hasFirstDescriptorId() && hasSecondDescriptorForSubmodel() && !hasSecondDescriptorId();
	}

	public boolean isSingleShellDescriptorSingleSubmodelDescriptorPath() {
		return hasFirstDescriptorForShell() && hasFirstDescriptorId() && hasSecondDescriptorForSubmodel() && hasSecondDescriptorId();
	}

	public boolean isAllSubmodelDescriptorsPath() {
		return hasFirstDescriptorForSubmodel() && !hasFirstDescriptorId();
	}

	public boolean isSingleSubmodelDescriptorPath() {
		return hasFirstDescriptorForSubmodel() && hasFirstDescriptorId();
	}

	public String getFirstDescriptorId() {
		return registryPath.getFirstDescriptorId();
	}

	public String getSecondDescriptorId() {
		return registryPath.getSecondDescriptorId();
	}

	private static class RegistryPath {
		private String pathPrefix;
		private String firstDescriptorType;
		private String firstDescriptorId;
		private String secondDescriptorType;
		private String secondDescriptorId;

		public RegistryPath(String path) {
			String[] splittedPath = path.split("/");

			pathPrefix = splittedPath.length > 0 ? utf8Decode(splittedPath[0]) : null;
			firstDescriptorType = splittedPath.length > 1 ? utf8Decode(splittedPath[1]) : null;
			firstDescriptorId = splittedPath.length > 2 ? utf8Decode(splittedPath[2]) : null;
			secondDescriptorType = splittedPath.length > 3 ? utf8Decode(splittedPath[3]) : null;
			secondDescriptorId = splittedPath.length > 4 ? utf8Decode(splittedPath[4]) : null;

			if (splittedPath.length > 5) {
				throw new MalformedRequestException("Given path '" + path + "' contains too many path elements and is therefore invalid.");
			}
		}

		private String utf8Decode(String string) {
			try {
				return URLDecoder.decode(string, ENCODING);
			} catch (UnsupportedEncodingException e) {
				throw new MalformedRequestException("Path has to be encoded as UTF-8 string.");
			}
		}

		/**
		 * Retrieves prefix inside the given path
		 *
		 * @return prefix of given path
		 */
		public String getPathPrefix() {
			return pathPrefix;
		}

		/**
		 * Retrieves first descriptor type inside the given path
		 *
		 * @return first type of descriptors for the given path
		 */
		public String getFirstDescriptorType() {
			return firstDescriptorType;
		}

		/**
		 * Retrieves first descriptor id inside the given path
		 *
		 * @return first descriptor id for the given path
		 */
		public String getFirstDescriptorId() {
			return firstDescriptorId;
		}

		/**
		 * Retrieves second descriptor type inside the given path
		 *
		 * @return second type of descriptors for the given path
		 */
		public String getSecondDescriptorType() {
			return secondDescriptorType;
		}

		/**
		 * Retrieves second descriptor id inside the given path
		 *
		 * @return second descriptor id for the given path
		 */
		public String getSecondDescriptorId() {
			return secondDescriptorId;
		}
	}
}