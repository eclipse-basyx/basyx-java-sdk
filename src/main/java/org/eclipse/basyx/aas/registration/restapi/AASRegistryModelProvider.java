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
package org.eclipse.basyx.aas.registration.restapi;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Connects an arbitrary IRegistryService implementation to the VAB
 * 
 * @author schnicke, conradi
 *
 */
public class AASRegistryModelProvider implements IModelProvider {

	IAASRegistry registry;

	public static final String PREFIX = "api/v1/registry";
	public static final String SUBMODELS = "submodels";

	public AASRegistryModelProvider(IAASRegistry registry) {
		this.registry = registry;
	}

	public AASRegistryModelProvider() {
		this(new InMemoryRegistry());
	}

	/**
	 * Check for correctness of path and returns a stripped path (i.e. no leading
	 * prefix)
	 * 
	 * @param path
	 * @return
	 * @throws MalformedRequestException if path does not start with PREFIX
	 *                                   "api/v1/registry"
	 */
	private String stripPrefix(String path) throws MalformedRequestException {
		path = VABPathTools.stripSlashes(path);
		if (!path.startsWith(PREFIX)) {
			throw new MalformedRequestException(
					"Path " + path + " not recognized as registry path. Has to start with " + PREFIX);
		}
		path = path.replaceFirst(PREFIX, "");
		path = VABPathTools.stripSlashes(path);
		return path;
	}

	/**
	 * Splits a path and checks, that first element is not "submodels" and that the
	 * second one, if exists, is "submodels"
	 * 
	 * @param path the path to be splitted
	 * @return Array of path elements
	 * @throws MalformedRequestException if path is not valid
	 */
	private String[] splitPath(String path) throws MalformedRequestException {

		if (path.isEmpty()) {
			return new String[0];
		}

		String[] splitted = path.split("/");

		// Assumes "submodels" is not a valid AASId
		if (splitted[0].equals(SUBMODELS)) {
			throw new MalformedRequestException("Path must not start with " + SUBMODELS);
		}

		// If path contains more than one element, the second one has to be "submodels"
		if (splitted.length > 1 && !splitted[1].equals(SUBMODELS)) {
			throw new MalformedRequestException("Second path element must be (if present): " + SUBMODELS);
		}

		return splitted;
	}

	private String[] preparePath(String path) throws MalformedRequestException {
		path = stripPrefix(path);

		String[] splitted = splitPath(path);

		try {
			for (int i = 0; i < splitted.length; i++) {
				splitted[i] = URLDecoder.decode(splitted[i], "UTF-8");
			}
			return splitted;
		} catch (UnsupportedEncodingException e) {
			// Malformed request because of unsupported encoding
			throw new MalformedRequestException("Path has to be encoded as UTF-8 string.");
		}
	}

	/**
	 * Checks if a given Object is a Map and checks if it has the correct modelType
	 * 
	 * @param expectedModelType the modelType the Object is expected to have
	 * @param value             the Object to be checked and casted
	 * @return the object casted to a Map
	 * @throws MalformedRequestException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> checkModelType(String expectedModelType, Object value)
			throws MalformedRequestException {
		// check if the given value is a Map
		if (!(value instanceof Map)) {
			throw new MalformedRequestException("Given newValue is not a Map");
		}

		Map<String, Object> map = (Map<String, Object>) value;

		// check if the given Map contains an AAS
		String type = ModelType.createAsFacade(map).getName();

		// have to accept Objects without modeltype information,
		// as modeltype is not part of the public metamodel
		if (!expectedModelType.equals(type) && type != null) {
			throw new MalformedRequestException("Given newValue map has not the correct ModelType");
		}

		return map;
	}

	/**
	 * Makes sure, that given Object is an AASDescriptor by checking its ModelType<br>
	 * Creates a new AASDescriptor with the content of the given Map
	 * 
	 * @param value the AAS Map object
	 * @return an AAS
	 * @throws MalformedRequestException
	 */
	private AASDescriptor createAASDescriptorFromMap(Object value) throws MalformedRequestException {
		Map<String, Object> map = checkModelType(AASDescriptor.MODELTYPE, value);
		AASDescriptor aasDescriptor = new AASDescriptor(map);
		return aasDescriptor;
	}

	/**
	 * Makes sure, that given Object is an SubmodelDescriptor by checking its ModelType<br>
	 * Creates a new SubmodelDescriptor with the content of the given Map
	 * 
	 * @param value the AAS Map object
	 * @return an AAS
	 * @throws MalformedRequestException
	 */
	private SubmodelDescriptor createSMDescriptorFromMap(Object value) throws MalformedRequestException {
		Map<String, Object> map = checkModelType(SubmodelDescriptor.MODELTYPE, value);
		SubmodelDescriptor smDescriptor = new SubmodelDescriptor(map);
		return smDescriptor;
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		String[] splitted = preparePath(path);

		if (isRequestForAllAASDescriptors(splitted)) {
			return registry.lookupAll();
		} else if (isRequestForAASDescriptor(splitted)) {
			return getAASDescriptor(splitted);
		} else if (isRequestForSubmodelDescriptorsOfAAS(splitted)) {
			return getSmDescriptorsFromAAS(createAASModelUrn(splitted));
		} else if (isRequestForSubmodelDescriptorOfAAS(splitted)) {
			return getSubmodelDescriptor(splitted);
		}

		throw new MalformedRequestException(createInvalidPathMessage(path));
	}

	private String createInvalidPathMessage(String path) {
		return "Given path '" + path + "' contains more than three path elements and is therefore invalid.";
	}

	private Object getSubmodelDescriptor(String[] splitted) {
		SubmodelDescriptor smDescriptor = getSmDescriptorFromAAS(createAASModelUrn(splitted), splitted[2]);

		if (smDescriptor == null) {
			throw new ResourceNotFoundException(
					"Specified SubmodelId '" + splitted[2] + "' does not exist in AAS '" + splitted[0] + "'.");
		}

		return smDescriptor;
	}

	private boolean isRequestForSubmodelDescriptorOfAAS(String[] splitted) {
		return splitted.length == 3;
	}

	private boolean isRequestForSubmodelDescriptorsOfAAS(String[] splitted) {
		return splitted.length == 2;
	}

	private boolean isRequestForAASDescriptor(String[] splitted) {
		return splitted.length == 1;
	}

	private boolean isRequestForAllAASDescriptors(String[] splitted) {
		return splitted.length == 0;
	}

	private Object getAASDescriptor(String[] splitted) {
		AASDescriptor descriptor = registry.lookupAAS(createAASModelUrn(splitted));

		if (descriptor == null) {
			throw new ResourceNotFoundException("Specified AASid '" + splitted[0] + "' does not exist.");
		}

		return descriptor;
	}

	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		String[] splitted = preparePath(path);

		if (!isValidSetPath(splitted)) {
			throw new MalformedRequestException("Set with empty path is not supported by registry");
		}

		ModelUrn identifier = createAASModelUrn(splitted);

		if (isRequestForAASDescriptor(splitted)) {
			registerAASDescriptor(newValue, splitted);
		} else if (isRequestForSubmodelDescriptorOfAAS(splitted)) {
			registerSubmodelDescriptor(newValue, identifier);
		} else {
			throw new MalformedRequestException("Unknown path " + path);
		}
	}

	private void registerSubmodelDescriptor(Object newValue, ModelUrn identifier) {
		SubmodelDescriptor smDesc = createSMDescriptorFromMap(newValue);
		registry.register(identifier, smDesc);
	}

	private void registerAASDescriptor(Object newValue, String[] splitted) {
		AASDescriptor desc = createAASDescriptorFromMap(newValue);

		String descId = desc.getIdentifier().getId();
		String urlId = splitted[0];

		if (descId.equals(urlId)) {
			registry.register(desc);
		} else {
			throw new MalformedRequestException(
					"The Identifier " + descId + " in the descriptor does not match the URL with id " + urlId);
		}
	}

	private boolean isValidSetPath(String[] splitted) {
		return splitted.length > 0;
	}

	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		throw new MalformedRequestException("Create (POST) on a registry is not supported. Please, use put");
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		String[] splitted = preparePath(path);

		if (!isValidDeletePath(splitted)) {
			throw new MalformedRequestException("Delete with empty path is not supported by registry");
		}

		if (isRequestForAASDescriptor(splitted)) {
			deleteAASDescriptor(splitted);
		} else if (isRequestForSubmodelDescriptorOfAAS(splitted)) {
			deleteSubmodelDescriptor(splitted);
		}

	}

	private boolean isValidDeletePath(String[] splitted) {
		return splitted.length > 0;
	}

	private void deleteSubmodelDescriptor(String[] splitted) {
		ModelUrn aasId = createAASModelUrn(splitted);
		String smId = splitted[2];

		SubmodelDescriptor smDesc = getSmDescriptorFromAAS(aasId, smId);
		if (smDesc == null) {
			throw new ResourceNotFoundException(
					"A Submodel with id '" + smId + "' does not exist in aas '" + aasId + "'.");
		}

		registry.delete(aasId, smDesc.getIdentifier());
	}

	private ModelUrn createAASModelUrn(String[] splitted) {
		return new ModelUrn(splitted[0]);
	}

	private void deleteAASDescriptor(String[] splitted) {
		ModelUrn aasId = createAASModelUrn(splitted);

		if (registry.lookupAAS(aasId) == null) {
			throw new ResourceNotFoundException("AAS '" + aasId + "' to be deleted does not exist.");
		}

		registry.delete(aasId);
	}

	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {
		throw new MalformedRequestException("DeleteValue with parameter not supported by registry");
	}

	@Override
	public Object invokeOperation(String path, Object... parameter) throws ProviderException {
		throw new MalformedRequestException("Invoke not supported by registry");
	}

	/**
	 * Gets all SubmodelDescriptor objects form an aas. Throws RuntimeException if
	 * aas does not exist.
	 * 
	 * @param id id of the aas
	 * @return Set of contained SubmodelDescriptor objects
	 * @throws ResourceNotFoundException if the AAS does not exist
	 */
	private Collection<SubmodelDescriptor> getSmDescriptorsFromAAS(IIdentifier id) throws ResourceNotFoundException {
		AASDescriptor aasDescriptor = registry.lookupAAS(id);

		if (aasDescriptor == null) {
			throw new ResourceNotFoundException("Specified AASid '" + id.getId() + "' does not exist.");
		}

		return aasDescriptor.getSubmodelDescriptors();
	}

	/**
	 * Gets a specific SubmodelDescriptor form an aas. Throws RuntimeException if
	 * aas does not exist.
	 * 
	 * @param aasId id of the aas
	 * @param smId  id of the submodel
	 * @return the SubmodelDescriptor with the given id
	 * @throws ResourceNotFoundException if aasId does not exist
	 */
	private SubmodelDescriptor getSmDescriptorFromAAS(IIdentifier aasId, String smId) throws ResourceNotFoundException {
		AASDescriptor aasDescriptor = registry.lookupAAS(aasId);

		if (aasDescriptor == null) {
			throw new ResourceNotFoundException("Specified AASId '" + aasId.getId() + "' does not exist.");
		}

		SubmodelDescriptor smDescriptor = aasDescriptor.getSubmodelDescriptorFromIdentifierId(smId);

		if (smDescriptor == null) {
			throw new ResourceNotFoundException(
					"Specified SMId '" + smId + "' for AAS " + aasId.getId() + " does not exist.");
		}

		return smDescriptor;
	}

}
