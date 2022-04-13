/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.vab.modelprovider.filesystem.filesystem;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements a generic file system
 * 
 * @author schnicke
 *
 */
public class GenericFileSystem implements FileSystem {

	@Override
	public String readFile(String path) throws IOException {
		path = toLowerCase(path);
		return new String(Files.readAllBytes(getPath(path)), StandardCharsets.UTF_8);
	}

	@Override
	public void writeFile(String path, String content) throws IOException {
		path = toLowerCase(path);
		FileWriter f = new FileWriter(path, false);
		f.write(content);
		f.close();
	}

	@Override
	public void deleteFile(String path) throws IOException {
		path = toLowerCase(path);
		Files.deleteIfExists(getPath(path));
	}

	@Override
	public void createDirectory(String path) throws IOException {
		path = toLowerCase(path);
		Files.createDirectories(getPath(path));
	}

	@Override
	public List<File> readDirectory(String path) throws IOException {
		path = toLowerCase(path);
		List<File> files = new ArrayList<>();

		// Use try to ensure that close is called
		try (Stream<Path> directory = Files.list(getPath(path))) {
			files.addAll(directory.filter(Files::isRegularFile).map(p -> new File(restoreCase(p.toString().replace("\\", "/")), FileType.DATA)).collect(Collectors.toList()));
		}

		// Use try to ensure that close is called
		try (Stream<Path> directory = Files.list(getPath(path))) {
			files.addAll(directory.filter(Files::isDirectory).map(p -> new File(restoreCase(p.toString().replace("\\", "/")), FileType.DIRECTORY)).collect(Collectors.toList()));
		}

		return files;
	}

	@Override
	public void deleteDirectory(String path) throws IOException {
		path = toLowerCase(path);
		try {
			Files.walk(getPath(path)).sorted(Comparator.reverseOrder()).map(p -> p.toFile()).forEach(f -> f.delete());
		} catch (NoSuchFileException e) {
			// Do nothing if the File to delete doesn't exist
		}
	}

	@Override
	public FileType getType(String path) {
		path = toLowerCase(path);

		if (!Files.exists(getPath(path)))
			return null;

		if (Files.isDirectory(getPath(path)))
			return FileType.DIRECTORY;

		return FileType.DATA;
	}

	private Path getPath(String path) {
		return FileSystems.getDefault().getPath(path);
	}

	/**
	 * Replaces each capital character by (_small character) ([A-Z] by _[a-z]) and
	 * each _ by __
	 */
	private String toLowerCase(String path) {
		return path.replaceAll("([A-Z_])", "_$1").toLowerCase();
	}

	/**
	 * Replaces each (_small character) by a capital character (_[a-z] by [A-Z]) and
	 * each __ by _
	 */
	private String restoreCase(String path) {
		StringBuffer result = new StringBuffer();
		Matcher m = Pattern.compile("_([_a-z])").matcher(path);

		while (m.find())
			m.appendReplacement(result, m.group(1).toUpperCase());

		m.appendTail(result);

		return result.toString();
	}

}
