/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.modelprovider;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;

/**
 * Utility functions to handle a VAB path
 * 
 * @author kuhn, espen
 * 
 */
public class VABPathTools {
	public static final String SEPERATOR = "/";

	/**
	 * Removes leading and trailing slashes
	 * 
	 * @param path
	 * @return
	 */
	public static String stripSlashes(String path) {
		while (path.startsWith("/")) {
			path = path.substring(1);
		}

		while (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	/**
	 * Encodes sensitive characters, e.g. "/" and "#"
	 * 
	 * @param elem
	 * @return
	 */
	public static String encodePathElement(String elem) {
		try {
			return URLEncoder.encode(elem, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Decodes sensitive characters, e.g. "/" and "#"
	 * 
	 * @param encodedElem
	 * @return
	 */
	public static String decodePathElement(String encodedElem) {
		try {
			return URLDecoder.decode(encodedElem, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Skips the first N entries of a path. E.g. for <i>a/b/c</i> skipping 2 means
	 * returning <i>c</i>
	 * 
	 * @param path
	 * @param toSkip
	 * @return
	 */
	public static String skipEntries(String path, int toSkip) {
		StringBuilder builder = new StringBuilder();
		String[] splitted = VABPathTools.splitPath(path);

		for (int i = toSkip; i < splitted.length; i++) {
			builder.append(splitted[i]);

			// Don't add slash at last step
			if (i < splitted.length - 1) {
				builder.append("/");
			}

		}

		return builder.toString();
	}

	/**
	 * Returns the Nth entry of a path, e.g. the second entry of <i>a/b/c</i> is
	 * <i>c</i>
	 * 
	 * @param path
	 * @param entry
	 * @return
	 */
	public static String getEntry(String path, int entry) {
		return VABPathTools.splitPath(path)[entry];
	}

	/**
	 * Split a path into path elements, e.g. /a/b/c {@literal ->} [ a, b, c ]
	 */
	public static String[] splitPath(String path) {
		// Return null result for null argument
		if (path == null) {
			return null;
		}

		// includes null-values, "" and "/";
		if (VABPathTools.isEmptyPath(path)) {
			return new String[] {};
		}

		// Remove leading separator, otherwise /a leads to {"", "a"}
		String fixedPath = removePrefix(path, SEPERATOR);

		String[] splitted = fixedPath.split(SEPERATOR);
		List<String> nonEmptySplitted = new ArrayList<>();

		// Remove empty entries
		for (String s : splitted) {
			if (!s.isEmpty()) {
				nonEmptySplitted.add(s);
			}
		}
		return nonEmptySplitted.toArray(new String[nonEmptySplitted.size()]);
	}

	/**
	 * Remove the last element from the path
	 */
	public static String getParentPath(String path) {
		// Return null result for null argument
		if (path == null) {
			return null;
		}

		if (isEmptyPath(path)) {
			return "";
		}
		int lastIndex = path.lastIndexOf(SEPERATOR);
		if (lastIndex == path.length() - 1) {
			lastIndex = path.lastIndexOf(SEPERATOR, path.length() - 2);
		}
		if (lastIndex >= 0) {
			return removePrefix(path.substring(0, lastIndex), SEPERATOR);
		} else {
			return "";
		}
	}

	/**
	 * Get the last element of a path. Return "" if there is no element in the path
	 */
	public static String getLastElement(String path) {
		// Return null result for null argument
		if (path == null) {
			return null;
		}

		String[] elements = splitPath(path);
		if (elements.length > 0) {
			return elements[elements.length - 1];
		} else {
			return "";
		}
	}

	/**
	 * Remove prefix from beginning of path
	 */
	public static String removePrefix(String path, String prefix) {
		// Return null result for null argument
		if (path == null) {
			return null;
		}

		if (VABPathTools.isEmptyPath(path)) {
			// same result as for any other "empty" path, like "" and "/"
			return "";
		}
		if (path.startsWith(prefix)) {
			return path.substring(prefix.length());
		} else {
			return path;
		}
	}

	public static String append(String path, String element) {
		// Return null result for null argument
		if (path == null || element == null) {
			return null;
		}

		if (path.lastIndexOf(SEPERATOR) == path.length() - 1) {
			return path + element;
		} else {
			return path + SEPERATOR + element;
		}
	}

	/**
	 * Build and return a path with pathElements[startIndex] as the root element
	 */
	public static String buildPath(String[] pathElements, int startIndex) {
		// Return null result for null argument
		if (pathElements == null) {
			return null;
		}

		if (startIndex >= pathElements.length) {
			return "";
		}

		// This will store the resulting path
		StringBuilder result = new StringBuilder();

		// Build path
		for (int i = startIndex; i < pathElements.length; i++)
			result.append(pathElements[i] + SEPERATOR);

		// Remove last '/'
		result.deleteCharAt(result.length() - 1);

		// Return created path
		return result.toString();
	}

	/**
	 * Check if the path to an VAB elements leads to the invocation of an operation. In this case, the
	 * element path conforms to /aas/submodels/{subModelId}/submodelElements/{operationId}/invoke
	 */
	public static boolean isOperationInvokationPath(String path) {
		// null-Paths are no operation paths
		if (path == null) {
			return false;
		}

		// Split path
		String[] pathElements = splitPath(path);
		
		if(pathElements.length == 0) {
			return false;
		}

		// Check if last path element is "invoke" or "operations" is contained anywhere
		return pathElements[pathElements.length - 1].startsWith(Operation.INVOKE) || isOperationPath(path);
	}

	private static boolean isOperationPath(String path) {
		String lowerCasePath = path.toLowerCase();
		return lowerCasePath.startsWith("operations/") || path.toLowerCase().contains("/operations/");
	}

	/**
	 * Check, if the path does not contain any elements.
	 */
	public static boolean isEmptyPath(String path) {
		return path.equals("") || path.equals("/");
	}

	/**
	 * Gets the first endpoint of a path.
	 * @param fullPath 
	 * 		A path that can contain 0..* endpoints.
	 * @return
	 * 		The first address entry of a path. The address entry is the first endpoint combined with a protocol.
	 * 		If there is no protocol defined, the address entry is empty ("").
	 * 		E.g. basyx://127.0.0.1:6998//https://localhost/test/ will return basyx://127.0.0.1:6998, 
	 * 		https://localhost/test//basyx://127.0.0.1:6998/ will return https://localhost/test
	 * 		and http://localhost/test/ will return "". 
	 */
	public static String getFirstEndpoint(String fullPath) {
		// Return null result for null argument
		if (fullPath == null) {
			return null;
		}

		if (isEmptyPath(fullPath) || !fullPath.contains("//")) {
			return "";
		} else {
			String[] splitted = fullPath.split("//");
			return splitted[0] + "//" + splitted[1];
		}
	}

	/**
	 * Removes the first endpoint from a path.<br>
	 * @param fullPath
	 * @return 
	 * 		The first endpoint. E.g. basyx://127.0.0.1:6998//https://localhost/test/ will return
	 * 		https://localhost/test/.
	 * 
	 */
	public static String removeFirstEndpoint(String fullPath) {
		// Return null result for null argument
		if (fullPath == null) {
			return null;
		}

		if (isEmptyPath(fullPath)) {
			return "";
		} else if (!fullPath.contains("//")) {
			return fullPath;
		} else {
			String firstEndpoint = fullPath.replaceFirst(getFirstEndpoint(fullPath), "");
			if (firstEndpoint.startsWith("//")) {
				firstEndpoint = firstEndpoint.replaceFirst("//", "");
			}
			return firstEndpoint;
		}
	}

	/**
	 * Concatenate two paths
	 */
	public static String concatenatePaths(String... paths) {
		// Return null result for null argument
		if (paths == null || paths.length == 0) {
			return null;
		}

		// Store result
		StringBuffer result = new StringBuffer();

		// Flag that indicates whether processed path segment is first segment
		boolean isFirst = true;

		// Process all path segments
		for (String pathSegment : paths) {
			// Return empty result, if any element is null
			if (pathSegment == null) {
				return null;
			}

			// Remove leading and trailing "/" from pathsegment
			while (pathSegment.endsWith("/"))
				pathSegment = pathSegment.substring(0, pathSegment.length() - 1);
			while (pathSegment.startsWith("/"))
				pathSegment = pathSegment.substring(1);

			// Add path to result; if its first segment, do not split with "'"
			if (!isFirst)
				result.append("/");
			else
				isFirst = false;
			result.append(pathSegment);
		}

		// Return combined path
		return result.toString();
	}
	
	/**
	 * Checks if path is null, if yes throw exception
	 * 
	 * @param path
	 */
	public static void checkPathForNull(String path) throws MalformedRequestException {
		if (path == null) {
			throw new MalformedRequestException("Path is not allowed to be null");
		}
	}
	
	/**
	 * Strips the last path element if it is "invoke"
	 * 
	 * @param path
	 * @return path without last element "invoke" or unchanged path
	 */
	public static String stripInvokeFromPath(String path) {
		return stripFromPath(path, Operation.INVOKE);
	}
	
	/**
	 * Strips the last path element if it is elementToStrip
	 * 
	 * @param path
	 * @param elementToStrip
	 * @return path without last element "invoke" or unchanged path
	 */
	public static String stripFromPath(String path, String elementToStrip) {
		if(path == null)
			return null;
		
		if(getLastElement(path).startsWith(elementToStrip)) {
			return getParentPath(path);
		}
		
		return path;
	}
	
	/**
	 * Gets the path from a URL
	 * e.g "http://localhost:8080/path/to/test.file" results in "/path/to/test.file"
	 * 
	 * @param url
	 * @return the path from the URL
	 */
	public static String getPathFromURL(String url) {
		if(url == null) {
			return null;
		}
		
		if(url.contains("://")) {
			
			// Find the ":" and and remove the "http://" from the url
			int index = url.indexOf(":") + 3;
			url = url.substring(index);
			
			// Find the first "/" from the URL (now without the "http://") and remove everything before that
			index = url.indexOf("/");
			url = url.substring(index);
			
			// Recursive call to deal with more than one server parts 
			// (e.g. basyx://127.0.0.1:6998//https://localhost/test/)
			return getPathFromURL(url);
		} else {
			// Make sure the path has a / at the start
			if(!url.startsWith("/")) {
				url = "/" + url;
			}
			return url;
		}
	}

	/**
	 * Harmonizes a path so that it will always and with the suffix and no ending
	 * slash (even if the suffix contains one).
	 * 
	 * @param path
	 *            to harmonize
	 * @param suffix
	 *            to check for existance and append if necessary
	 * @return harmonized path
	 */
	public static String harmonizePathWithSuffix(String path, String suffix) {
		String strippedPath = stripSlashes(path);
		String strippedSuffix = stripSlashes(suffix);

		if (strippedPath.endsWith("/" + strippedSuffix)) {
			return strippedPath;
		} else {
			return VABPathTools.concatenatePaths(strippedPath, strippedSuffix);
		}
	}
}
