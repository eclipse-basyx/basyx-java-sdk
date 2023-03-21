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
package org.eclipse.basyx.submodel.restapi.vab;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.restapi.MultiSubmodelElementProvider;
import org.eclipse.basyx.submodel.restapi.SubmodelAPIHelper;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

import com.google.common.io.Files;

/**
 * Implements the Submodel API by mapping it to VAB paths
 * 
 * @author schnicke
 *
 */
public class VABSubmodelAPI implements ISubmodelAPI {

	// The VAB model provider containing the model this API implementation is based
	// on
	private IModelProvider modelProvider;

	private String tmpDirectory = Files.createTempDir().getAbsolutePath();

	/**
	 * Creates a VABSubmodelAPI that wraps an IModelProvider
	 * 
	 * @param modelProvider providing the Submodel
	 */
	public VABSubmodelAPI(IModelProvider modelProvider) {
		super();
		this.modelProvider = modelProvider;
	}

	/**
	 * Creates an IModelProvider for handling accesses to the elements within the
	 * submodel
	 * 
	 * @return returns the SubmodelElementProvider pointing to the contained
	 *         submodelelements
	 */
	private MultiSubmodelElementProvider getElementProvider() {
		IModelProvider elementProxy = new VABElementProxy(SubmodelAPIHelper.getSubmodelElementsPath(), modelProvider);
		return new MultiSubmodelElementProvider(elementProxy);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ISubmodel getSubmodel() {
		// For access on the container property root, return the whole model
		Map<String, Object> map = (Map<String, Object>) modelProvider.getValue(SubmodelAPIHelper.getSubmodelPath());

		// Only return a copy of the Submodel
		Map<String, Object> smCopy = new LinkedHashMap<>();
		smCopy.putAll(map);
		return Submodel.createAsFacade(smCopy);
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		getElementProvider().createValue(SubmodelAPIHelper.getSubmodelElementPath(elem.getIdShort()), elem);
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		getElementProvider().createValue(SubmodelAPIHelper.getSubmodelElementPath(idShortPath), elem);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteSubmodelElement(String idShortPath) {
		ISubmodelElement submodelElement = getSubmodelElement(idShortPath);
		if (File.isFile((Map<String, Object>) submodelElement)) {
			File file = File.createAsFacade((Map<String, Object>) submodelElement);
			java.io.File tmpFile = new java.io.File(file.getValue());
			tmpFile.delete();
		}
		getElementProvider().deleteValue(SubmodelAPIHelper.getSubmodelElementPath(idShortPath));
	}

	@Override
	public Collection<IOperation> getOperations() {
		return getSubmodelElements().stream().filter(e -> e instanceof IOperation).map(e -> (IOperation) e)
				.collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<ISubmodelElement> getSubmodelElements() {
		Collection<Map<String, Object>> elements = (Collection<Map<String, Object>>) getElementProvider()
				.getValue(SubmodelAPIHelper.getSubmodelElementsPath());
		return elements.stream().map(SubmodelElement::createAsFacade).collect(Collectors.toList());
	}

	@Override
	public void updateSubmodelElement(String idShortPath, Object newValue) {
		getElementProvider().setValue(SubmodelAPIHelper.getSubmodelElementValuePath(idShortPath), newValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void uploadSubmodelElementFile(String idShortPath, InputStream fileStream) {
		ISubmodelElement submodelElement = getSubmodelElement(idShortPath);
		if (File.isFile((Map<String, Object>) submodelElement)) {
			try {
				createFile(idShortPath, fileStream, submodelElement);
			} catch (IOException e) {
				throw new ProviderException(e);
			}
		} else {
			throw new MalformedRequestException(
					"The request is invalid for a SubmodelElement with type '" + submodelElement.getModelType() + "'");
		}
	}

	@SuppressWarnings("unchecked")
	private void createFile(String idShortPath, Object newValue, ISubmodelElement submodelElement) throws IOException {
		File file = File.createAsFacade((Map<String, Object>) submodelElement);
		String filePath = getFilePath(idShortPath, file);

		java.io.File targetFile = new java.io.File(filePath);

		FileOutputStream outStream = new FileOutputStream(targetFile);
		InputStream inStream = (InputStream) newValue;

		IOUtils.copy(inStream, outStream);

		inStream.close();
		outStream.close();
	}

	private String getFilePath(String idShortPath, File file) {
		String fileName = idShortPath.replaceAll("/", "-");

		return tmpDirectory + "/" + fileName;
	}

	@Override
	public Object getSubmodelElementValue(String idShortPath) {
		return getElementProvider().getValue(SubmodelAPIHelper.getSubmodelElementValuePath(idShortPath));
	}

	@SuppressWarnings("unchecked")
	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		return SubmodelElement.createAsFacade((Map<String, Object>) getElementProvider()
				.getValue(SubmodelAPIHelper.getSubmodelElementPath(idShortPath)));
	}

	@Override
	public Object invokeOperation(String idShortPath, Object... params) {
		return getElementProvider().invokeOperation(SubmodelAPIHelper.getSubmodelElementSyncInvokePath(idShortPath),
				params);
	}

	@Override
	public Object invokeAsync(String idShortPath, Object... params) {
		return getElementProvider().invokeOperation(SubmodelAPIHelper.getSubmodelElementAsyncInvokePath(idShortPath),
				params);
	}

	@Override
	public Object getOperationResult(String idShortPath, String requestId) {
		return getElementProvider()
				.getValue(SubmodelAPIHelper.getSubmodelElementResultValuePath(idShortPath, requestId));
	}

	@SuppressWarnings("unchecked")
	@Override
	public java.io.File getSubmodelElementFile(String idShortPath) {

		Map<String, Object> submodelElement = (Map<String, Object>) getSubmodelElement(idShortPath);

		File fileSubmodelElement = File.createAsFacade(submodelElement);

		String filePath = getFilePath(idShortPath, fileSubmodelElement);

		return new java.io.File(filePath);
	}

}
