/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.factory.aasx;

/**
 * Container class for the content of a File and its Path
 * 
 * @author conradi
 *
 */
public class InMemoryFile {

	private byte[] fileContent;
	private String path;
	
	public InMemoryFile(byte[] fileContent, String path) {
		this.fileContent = fileContent;
		this.path = path;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public String getPath() {
		return path;
	}
}
