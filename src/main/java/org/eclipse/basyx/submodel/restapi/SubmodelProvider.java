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
package org.eclipse.basyx.submodel.restapi;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.regex.Pattern;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.facade.submodelelement.SubmodelElementFacadeFactory;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueTypeHelper;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;

/**
 * Additional VAB provider specific for providing submodels together with other
 * SDKs by implementing the submodel API.
 *
 * The VAB provides generic models without considering high-level information.
 * For example, Operation submodel elements are realized as a Map. The function
 * is contained and can be accessed at /operations/opId/invokable. Therefore
 * invoking the Operation directly at /operations/opId/ attempts to invoke the
 * Map, which does not have any effect for the VABModelProvider.
 *
 * This provider maps these requests to the VAB primitives to generate the
 * desired behavior as it is described in the BaSys documentation.
 *
 * @author espen, schnicke
 *
 */
public class SubmodelProvider implements IModelProvider {

	public static final String VALUES = "values";
	public static final String SUBMODEL = "submodel";
	public static final String FILE = "File";
	public static final String UPLOAD = "upload";

	ISubmodelAPI submodelAPI;

	/**
	 * Default constructor - based on an empty submodel with a lambda provider
	 */
	public SubmodelProvider() {
		this(new Submodel());
	}

	/**
	 * Creates a SubmodelProvider based on the VAB API, wrapping the passed provider
	 * 
	 * @param provider
	 *            to be wrapped by submodel API
	 */
	public SubmodelProvider(IModelProvider provider) {
		submodelAPI = new VABSubmodelAPI(provider);
	}

	/**
	 * Creates a SubmodelProvider based on a lambda provider and a given model
	 */
	public SubmodelProvider(Submodel model) {
		submodelAPI = new VABSubmodelAPI(new VABLambdaProvider(model));
	}

	/**
	 * Creates a SubmodelProvider based on a given ISubmodelAPI.
	 */
	public SubmodelProvider(ISubmodelAPI submodelAPI) {
		this.submodelAPI = submodelAPI;
	}

	/**
	 * Strips submodel prefix if it exists
	 *
	 * @param path
	 * @return
	 */
	private String removeSubmodelPrefix(String path) {
		path = VABPathTools.stripSlashes(path);
		String submodelWithSlash = SUBMODEL + "/";
		if (path.startsWith(submodelWithSlash)) {
			path = path.replaceFirst(submodelWithSlash, "");
		} else if (path.equals(SUBMODEL)) {
			path = "";
		} else {
			throw new MalformedRequestException("The request " + path + " is not allowed for this endpoint. /" + SUBMODEL + " is missing");
		}
		path = VABPathTools.stripSlashes(path);
		return path;
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		VABPathTools.checkPathForNull(path);
		path = removeSubmodelPrefix(path);
		if (path.isEmpty()) {
			return handleEmptyPath();
		}

		String[] splitted = VABPathTools.splitPath(path);

		if (isRequestForAllSubmodelElementValues(splitted)) {
			return getSubmodelProviderValues();
		} else if (isSubmodelElementsRequest(splitted)) {
			return submodelAPI.getSubmodelElements();
		} else if (isSpecificSubmodelElementRequest(splitted)) {
			// Remove initial "/submodelElements"
			path = removeSMElementPrefix(path);
			return handleSpecificSubmodelElementRequest(path, splitted);
		}

		throw new MalformedRequestException("Unknown path " + path + " was requested");
	}

	private Object getSubmodelProviderValues() {
		Map<String, Object> objectValues = submodelAPI.getSubmodel().getValues();
		prepareValuesForSerialization(objectValues);
		return objectValues;
	}

	private void prepareValuesForSerialization(Map<String, Object> objectValues) {
		Queue<Map<String, Object>> toBeTransformed = new LinkedList<>();
		toBeTransformed.add(objectValues);
		while (!toBeTransformed.isEmpty()) {
			Map<String, Object> currentMap = toBeTransformed.remove();
			List<Map<String, Object>> nextMaps = transformMapEntries(currentMap);
			toBeTransformed.addAll(nextMaps);
		}
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> transformMapEntries(Map<String, Object> unprocessedMap) {
		List<Map<String, Object>> unprocessedEntries = new LinkedList<>();
		for (Entry<String, Object> mapEntry : unprocessedMap.entrySet()) {
			Object value = mapEntry.getValue();

			if (value instanceof Map<?, ?>) {
				unprocessedEntries.add((Map<String, Object>) value);
				continue;
			}

			Object fixedValue = ValueTypeHelper.prepareForSerialization(value);
			unprocessedMap.replace(mapEntry.getKey(), fixedValue);
		}
		return unprocessedEntries;
	}

	private List<String> getIdShorts(String[] splitted) {
		// Create list from array and wrap it in ArrayList to ensure modifiability
		List<String> idShorts = new ArrayList<>(Arrays.asList(splitted));

		// Drop inital "submodelElements"
		idShorts.remove(0);

		// If value is contained in path, remove it
		if (splitted[splitted.length - 1].equals(Property.VALUE)) {
			idShorts.remove(idShorts.size() - 1);
		}
		return idShorts;
	}

	private boolean endsWithValue(String[] splitted) {
		return splitted[splitted.length - 1].equals(Property.VALUE);
	}

	/**
	 * Removes a trailing <code>/value</code> from the path if it exists.
	 *
	 * @param path
	 *            The original path, which might or might not end on
	 *            {@value Property.VALUE}.
	 * @return The new path
	 */
	private String removeValueSuffix(String path) {
		String suffix = "/" + Property.VALUE;
		if (path.endsWith(suffix)) {
			path = path.substring(0, path.length() - suffix.length());
		}

		return path;
	}

	private boolean isInvocationListPath(String[] splitted) {
		return splitted.length > 2 && splitted[splitted.length - 2].equals(OperationProvider.INVOCATION_LIST);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		path = removeSubmodelPrefix(path);
		if (path.isEmpty()) {
			throw new MalformedRequestException("Set on \"" + SUBMODEL + "\" not supported");
		} else {
			String[] splitted = VABPathTools.splitPath(path);
			path = removeSMElementPrefix(path);
			String idshortPath = removeValueSuffix(path);
			if (endsWithValue(splitted)) {
				submodelAPI.updateSubmodelElement(idshortPath, newValue);
			} else {

				ISubmodelElement element = SubmodelElementFacadeFactory.createSubmodelElement((Map<String, Object>) newValue);

				if (!path.endsWith(element.getIdShort())) {
					throw new MalformedRequestException("The idShort of given Element '" + element.getIdShort() + "' does not match the ending of the given path '" + path + "'");
				}

				submodelAPI.addSubmodelElement(idshortPath, element);
			}
		}
	}

	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		path = removeSubmodelPrefix(path);
		if (path.isEmpty()) {
			throw new MalformedRequestException("POST on \"" + SUBMODEL + "\" not supported");
		}
		String[] splitted = VABPathTools.splitPath(path);
		if (endsWithFileUpload(splitted)) {
			submodelAPI.uploadSubmodelElementFile(getFileIdShortFromSplittedPath4FileUpload(splitted), (InputStream) newEntity);
			return;
		}
		throw new MalformedRequestException("POST on \"" + path + "\" not allowed");
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		path = removeSubmodelPrefix(path);
		if (!path.isEmpty()) {
			String[] splitted = VABPathTools.splitPath(path);
			if (isQualifier(splitted[0])) {
				if (splitted.length > 2) {
					path = removeSMElementPrefix(path);
					submodelAPI.deleteSubmodelElement(path);
				} else {
					submodelAPI.deleteSubmodelElement(getIdShortFromSplittedPath(splitted));
				}
			} else {
				throw new MalformedRequestException("Path " + path + " not supported for delete");
			}
		} else {
			throw new MalformedRequestException("Path \"" + SUBMODEL + "\" not supported for delete");
		}
	}

	private boolean isQualifier(String str) {
		return str.equals(MultiSubmodelElementProvider.ELEMENTS);
	}

	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {
		throw new MalformedRequestException("Delete with a passed argument not allowed");
	}

	@Override
	public Object invokeOperation(String path, Object... parameters) throws ProviderException {
		String pathWithoutSubmodelPrefix = removeSubmodelPrefix(path);
		if (pathWithoutSubmodelPrefix.isEmpty()) {
			throw new MalformedRequestException("Given path must not be empty");
		}

		if (!VABPathTools.isOperationInvokationPath(pathWithoutSubmodelPrefix)) {
			throw new MalformedRequestException("Given path '" + path + "' does not end in /" + Operation.INVOKE);
		}

		String pathWithoutSMElementPrefix = removeSMElementPrefix(pathWithoutSubmodelPrefix);

		if (isAsyncInvokePath(pathWithoutSMElementPrefix)) {
			return invokeAsync(pathWithoutSMElementPrefix, parameters);
		} else {
			return invokeSync(pathWithoutSMElementPrefix, parameters);
		}

	}

	private Object invokeSync(String path, Object... parameters) {
		String pathWithoutInvoke = path.substring(0, path.lastIndexOf(Operation.INVOKE));
		String strippedPathWithoutInvoke = VABPathTools.stripSlashes(pathWithoutInvoke);
		return submodelAPI.invokeOperation(strippedPathWithoutInvoke, parameters);
	}

	private Object invokeAsync(String path, Object... parameters) {
		String pathWithoutAsyncInvoke = path.replaceFirst(Pattern.quote(Operation.INVOKE + OperationProvider.ASYNC), "");
		String strippedPathWithoutAsyncInvoke = VABPathTools.stripSlashes(pathWithoutAsyncInvoke);
		return submodelAPI.invokeAsync(strippedPathWithoutAsyncInvoke, parameters);
	}

	private boolean isAsyncInvokePath(String path) {
		return path.endsWith(OperationProvider.ASYNC);
	}

	public ISubmodelAPI getAPI() {
		return this.submodelAPI;
	}

	protected void setAPI(ISubmodelAPI api) {
		this.submodelAPI = api;
	}

	private String removeSMElementPrefix(String path) {
		return path.replaceFirst(MultiSubmodelElementProvider.ELEMENTS, "");
	}

	private boolean endsWithFile(String[] splitted) {
		return splitted[splitted.length - 1].equals(FILE);
	}

	private boolean endsWithFileUpload(String[] splitted) {
		return splitted[splitted.length - 1].equals(UPLOAD);
	}

	private String getFileIdShortFromSplittedPath4FileUpload(String[] splitted) {
		String idShort = "";
		for (int i = 1; i < splitted.length - 1; i++) {
			idShort = concatFileIdShortPath(splitted, idShort, i);
		}
		return idShort;
	}

	private String getFileIdShortFromSplittedPath4FileDownload(String[] splitted) {
		String idShort = "";
		for (int i = 1; i < splitted.length - 1; i++) {
			idShort = concatFileIdShortPath(splitted, idShort, i);
		}
		return idShort;
	}

	private String concatFileIdShortPath(String[] splitted, String idShort, int i) {
		if (idShort.isEmpty()) {
			idShort = idShort.concat(splitted[i]);
		} else {
			idShort = idShort.concat("/" + splitted[i]);
		}
		return idShort;
	}

	private String getIdShortFromSplittedPath(String[] splitted) {
		return splitted[1];
	}

	@SuppressWarnings("unchecked")
	private Object handleFile(String[] splitted) {
		String idShortPath = getFileIdShortFromSplittedPath4FileDownload(splitted);

		if (idShortPath.isEmpty()) {
			return submodelAPI.getSubmodelElement(FILE);
		}

		Map<String, Object> submodelElement = (Map<String, Object>) submodelAPI.getSubmodelElement(idShortPath);

		if (SubmodelElementCollection.isSubmodelElementCollection(submodelElement)) {
			SubmodelElementCollection smeCollection = SubmodelElementCollection.createAsFacade(submodelElement);
			return smeCollection.getSubmodelElement(FILE);
		} else if (!File.isFile(submodelElement)) {
			throw new MalformedRequestException("/File is only allowed for File Submodel Elements");
		}

		return submodelAPI.getSubmodelElementFile(idShortPath);
	}

	private Object handleEmptyPath() {
		ISubmodel sm = submodelAPI.getSubmodel();

		// Change internal map representation to set
		if (sm instanceof Submodel) {
			return SubmodelElementMapCollectionConverter.smToMap((Submodel) sm);
		} else {
			return sm;
		}
	}

	private Object handleSpecificSubmodelElementRequest(String path, String[] splitted) {
		if (endsWithValue(splitted)) { // Request for the value of an property
			return handleValue(path);
		} else if (isInvocationListPath(splitted)) {
			return handleInvocationListPath(splitted);
		} else if (endsWithFile(splitted)) {
			return handleFile(splitted);
		} else {
			return submodelAPI.getSubmodelElement(path);
		}
	}

	private Object handleValue(String path) {
		String idShortPath = removeValueSuffix(path);
		return submodelAPI.getSubmodelElementValue(idShortPath);
	}

	private Object handleInvocationListPath(String[] splitted) {
		List<String> idShorts = getIdShorts(splitted);

		// Remove invocationList/{requestId} from the idShorts
		idShorts.remove(idShorts.size() - 1);
		idShorts.remove(idShorts.size() - 1);
		return submodelAPI.getOperationResult(idShorts.get(0), splitted[splitted.length - 1]);
	}

	private boolean isRequestForAllSubmodelElementValues(String[] splitted) {
		return splitted.length == 1 && splitted[0].equals(VALUES);
	}

	private boolean isSubmodelElementsRequest(String[] splitted) {
		return splitted.length == 1 && splitted[0].equals(MultiSubmodelElementProvider.ELEMENTS);
	}

	private boolean isSpecificSubmodelElementRequest(String[] splitted) {
		return splitted.length >= 2 && isQualifier(splitted[0]);
	}
}
