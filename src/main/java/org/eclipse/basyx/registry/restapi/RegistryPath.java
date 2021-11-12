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

public class RegistryPath {
	public static final String PREFIX = "registry";
	public static final String SHELL_DESCRIPTORS = "shell-descriptors";
	public static final String SUBMODEL_DESCRIPTORS = "submodel-descriptors";

	private static final String ENCODING = "UTF-8";

	private String pathPrefix;
	private String firstDescriptorType;
	private String firstDescriptorId;
	private String secondDescriptorType;
	private String secondDescriptorId;

	private boolean hasFirstDescriptorForShell;
	private boolean hasFirstDescriptorForSubmodel;
	private boolean hasFirstDescriptorId;
	private boolean hasSecondDescriptorForSubmodel;
	private boolean hasSecondDescriptorId;

	/**
	 * Splits string into multiple parts as registry v2 specifies
	 *
	 * @param path
	 */
	public RegistryPath(String path) throws UnsupportedEncodingException {
		if (path.isEmpty()) {
			throw new MalformedRequestException("A correct registry path must be given.");
		}

		populatePrivateVariables(path);
		checkIfPathPrefixIsValid();
		checkIfFirstDescriptorIsValid();
		checkIfSecondDescriptorIsValid();
		populateCheckVariables();
	}

	private void populatePrivateVariables(String path) throws UnsupportedEncodingException {
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

	private String utf8Decode(String string) throws UnsupportedEncodingException {
		return URLDecoder.decode(string, ENCODING);
	}

	private void checkIfPathPrefixIsValid() {
		if (!pathPrefix.equals(PREFIX)) {
			throw new MalformedRequestException("Registry path must start with " + PREFIX);
		}
	}

	private void checkIfFirstDescriptorIsValid() {
		if (!(firstDescriptorType.equals(SHELL_DESCRIPTORS) || firstDescriptorType.equals(SUBMODEL_DESCRIPTORS))) {
			throw new MalformedRequestException("After " + PREFIX + "the path must continue with " + SHELL_DESCRIPTORS + " or " + SUBMODEL_DESCRIPTORS);
		}
	}

	private void checkIfSecondDescriptorIsValid() {
		if (!(secondDescriptorType == null || secondDescriptorType.equals(SUBMODEL_DESCRIPTORS))) {
			throw new MalformedRequestException("Second path element must be (if present): " + SUBMODEL_DESCRIPTORS);
		}
	}

	private void populateCheckVariables() {
		hasFirstDescriptorForShell = firstDescriptorType.equals(SHELL_DESCRIPTORS);
		hasFirstDescriptorForSubmodel = firstDescriptorType.equals(SUBMODEL_DESCRIPTORS);
		hasFirstDescriptorId = firstDescriptorId != null;
		hasSecondDescriptorForSubmodel = secondDescriptorType != null && secondDescriptorType.equals(SUBMODEL_DESCRIPTORS);
		hasSecondDescriptorId = secondDescriptorId != null;
	}

	public boolean isAllShellDescriptorsPath() {
		return hasFirstDescriptorForShell && !hasFirstDescriptorId && !hasSecondDescriptorForSubmodel && !hasSecondDescriptorId;
	}

	public boolean isSingleShellDescriptorPath() {
		return hasFirstDescriptorForShell && hasFirstDescriptorId && !hasSecondDescriptorForSubmodel && !hasSecondDescriptorId;
	}

	public boolean isSingleShellDescriptorAllSubmodelDescriptorsPath() {
		return hasFirstDescriptorForShell && hasFirstDescriptorId && hasSecondDescriptorForSubmodel && !hasSecondDescriptorId;
	}

	public boolean isSingleShellDescriptorSingleSubmodelDescriptorPath() {
		return hasFirstDescriptorForShell && hasFirstDescriptorId && hasSecondDescriptorForSubmodel && hasSecondDescriptorId;
	}

	public boolean isAllSubmodelDescriptorsPath() {
		return hasFirstDescriptorForSubmodel && !hasFirstDescriptorId;
	}

	public boolean isSingleSubmodelDescriptorPath() {
		return hasFirstDescriptorForSubmodel && hasFirstDescriptorId;
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
