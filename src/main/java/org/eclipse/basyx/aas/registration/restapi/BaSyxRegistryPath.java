/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.aas.registration.restapi;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;

public class BaSyxRegistryPath {
	public static final String PREFIX = "registry";
	public static final String SHELL_DESCRIPTORS = "shell-descriptors";
	public static final String SUBMODEL_DESCRIPTORS = "submodel-descriptors";

	private static final String ENCODING = "UTF-8";

	private String pathPrefix;
	private String pathShellDescriptors;
	private String pathAASId;
	private String pathSubmodelDescriptors;
	private String pathSubmodelId;

	/**
	 * Splits string into three parts as registry v2 specifies
	 *
	 * @param path
	 */
	public BaSyxRegistryPath(String path) throws UnsupportedEncodingException {
		if (path.isEmpty()) {
			throw new MalformedRequestException("A correct registry path must be given.");
		}

		populatePrivateVariables(path);
		checkIfPathPrefixIsValid();
		checkIfPathShellDescriptorsIsValid();
		checkIfPathSubmodelDescriptorsIsValid();
	}

	private void populatePrivateVariables(String path) throws UnsupportedEncodingException {
		String[] splitted_path = path.split("/");

		pathPrefix = splitted_path.length > 0 ? utf8Decode(splitted_path[0]) : null;
		pathShellDescriptors = splitted_path.length > 1 ? utf8Decode(splitted_path[1]) : null;
		pathAASId = splitted_path.length > 2 ? utf8Decode(splitted_path[2]) : null;
		pathSubmodelDescriptors = splitted_path.length > 3 ? utf8Decode(splitted_path[3]) : null;
		pathSubmodelId = splitted_path.length > 4 ? utf8Decode(splitted_path[4]) : null;

		if (splitted_path.length > 5) {
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

	private void checkIfPathShellDescriptorsIsValid() {
		if (!pathShellDescriptors.equals(SHELL_DESCRIPTORS)) {
			throw new MalformedRequestException("Registry path must start with " + PREFIX + "/" + SHELL_DESCRIPTORS);
		}
	}

	private void checkIfPathSubmodelDescriptorsIsValid() {
		if (!(pathSubmodelDescriptors == null || pathSubmodelDescriptors.equals(SUBMODEL_DESCRIPTORS))) {
			throw new MalformedRequestException("Second path element must be (if present): " + SUBMODEL_DESCRIPTORS);
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
	 * Retrieves shell descriptors inside the given path
	 *
	 * @return shell descriptors of given path
	 */
	public String getPathShellDescriptors() {
		return pathShellDescriptors;
	}

	/**
	 * Retrieves aasId inside the given path
	 *
	 * @return aasId of given path
	 */
	public String getPathAASId() {
		return pathAASId;
	}

	/**
	 * Retrieves submodel descriptors inside the given path
	 *
	 * @return submodel descriptors of given path
	 */
	public String getPathSubmodelDescriptors() {
		return pathSubmodelDescriptors;
	}

	/**
	 * Retrieves submodelId inside the given path
	 *
	 * @return submodelId of given path
	 */
	public String getPathSubmodelId() {
		return pathSubmodelId;
	}
}
