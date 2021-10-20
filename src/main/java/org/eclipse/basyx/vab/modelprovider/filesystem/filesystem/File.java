/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.modelprovider.filesystem.filesystem;

/**
 * Represents a generic file which is either data or a directory
 * 
 * @author schnicke
 *
 */
public class File {
	private String name;
	private FileType type;

	public File(String name, FileType type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public FileType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "File [name=" + name + ", type=" + type + "]";
	}
}
