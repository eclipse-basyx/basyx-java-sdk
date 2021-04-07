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

import java.io.IOException;
import java.util.List;

/**
 * Abstracts from a generic file system
 * @author schnicke
 *
 */
public interface FileSystem {
	
	/**
	 * Reads the content of a file formated as UTF-8.<br>
	 * Throws an Exception if file at given path does not exist. 
	 * 
	 * @param path
	 * @return The content of specified file as String
	 * @throws IOException
	 */
	public String readFile(String path) throws IOException;

	/**
	 * Writes a given String to a file at the given path.<br>
	 * Does create the file if it does not exist.<br>
	 * Does overwrite the file if it exists.<br>
	 * Does not create nonexistent parent directories.
	 * 
	 * @param path
	 * @param content the String to be written to a file
	 * @throws IOException
	 */
	public void writeFile(String path, String content) throws IOException;

	/**
	 * Deletes the file at the specified path.<br>
	 * Does not throw an Exception if the file does not exist.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void deleteFile(String path) throws IOException;

	/**
	 * Creates a directory at the given path.<br>
	 * Creates parent directories if nonexistent.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void createDirectory(String path) throws IOException;

	/**
	 * Lists all directories and files at the specified path.<br>
	 * Does not list items in subdirectories.
	 * 
	 * @param path
	 * @return List of items at the specified path
	 * @throws IOException
	 */
	public List<File> readDirectory(String path) throws IOException;
	
	/**
	 * Deletes the directory at the specified path,
	 * including contained files and subdirectories.<br>
	 * Does not throw an Exception if directory at path does not exist. 
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void deleteDirectory(String path) throws IOException;
	
	/**
	 * Gets the FileType of an object at a specified path
	 * 
	 * @param path
	 * @return The FileType of the object at the given path
	 * or null if this object does not exist
	 */
	public FileType getType(String path);
}
