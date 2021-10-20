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
	 * @throws MalformedRequestException if path does not start with PERFIX "api/v1/registry"
	 */
	private String stripPrefix(String path) throws MalformedRequestException {
		path = VABPathTools.stripSlashes(path);
		if (!path.startsWith(PREFIX)) {
			throw new MalformedRequestException("Path " + path + " not recognized as registry path. Has to start with " + PREFIX);
		}
		path = path.replaceFirst(PREFIX, "");
		path = VABPathTools.stripSlashes(path);
		return path;
	}
	
	/**
	 * Splits a path and checks, that first element is not "submodels" and that the second one, if exists, is "submodels"
	 * 
	 * @param path the path to be splitted
	 * @return Array of path elements
	 * @throws MalformedRequestException if path is not valid
	 */
	private String[] splitPath(String path) throws MalformedRequestException {
		
		if(path.isEmpty()) {
			return new String[0];
		}
		
		String[] splitted = path.split("/");
		
		//Assumes "submodels" is not a valid AASId
		if(splitted[0].equals(SUBMODELS)) {
			throw new MalformedRequestException("Path must not start with " + SUBMODELS);
		}
		
		//If path contains more than one element, the second one has to be "submodels"
		if(splitted.length > 1 && !splitted[1].equals(SUBMODELS)) {
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
			//Malformed request because of unsupported encoding
			throw new MalformedRequestException("Path has to be encoded as UTF-8 string.");
		}
	}
	
	/**
	 * Checks if a given Object is a Map and checks if it has the correct modelType
	 * 
	 * @param expectedModelType the modelType the Object is expected to have
	 * @param value the Object to be checked and casted
	 * @return the object casted to a Map
	 * @throws MalformedRequestException 
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> checkModelType(String expectedModelType, Object value) throws MalformedRequestException {
		//check if the given value is a Map
		if(!(value instanceof Map)) {
			throw new MalformedRequestException("Given newValue is not a Map");
		}

		Map<String, Object> map = (Map<String, Object>) value;
		
		//check if the given Map contains an AAS
		String type = ModelType.createAsFacade(map).getName();
		
		//have to accept Objects without modeltype information,
		//as modeltype is not part of the public metamodel
		if(!expectedModelType.equals(type) && type != null) {
			throw new MalformedRequestException("Given newValue map has not the correct ModelType");
		}
		
		return map;
	}
	
	/**
	 * Makes sure, that given Object is an AASDescriptor by checking its ModelType<br />
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
	 * Makes sure, that given Object is an SubmodelDescriptor by checking its ModelType<br />
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

		//Path is empty, request for all AASDescriptors
		if (splitted.length == 0) {
			return registry.lookupAll();
		} else {
			
			//Given path consists only of an AAS Id
			if(splitted.length == 1) {
				AASDescriptor descriptor = registry.lookupAAS(new ModelUrn(splitted[0]));
				
				//Throw an Exception if the requested AAS does not exist 
				if(descriptor == null) {
					throw new ResourceNotFoundException("Specified AASid '" + splitted[0] + "' does not exist.");
				}
				return descriptor;
			
			//Given path consists of an AAS Id and "/submodels"
			//Request for all SubmodelDescriptors of given AAS
			} else if(splitted.length == 2) {
				return getSmDescriptorsFromAAS(new ModelUrn(splitted[0]));
			
			//Given path consists of an AAS Id and "/submodels/" and a SubmodelId
			//Request for the SubmodelDescriptor of given AAS with given id
			} else if(splitted.length == 3) {
				SubmodelDescriptor smDescriptor = getSmDescriptorFromAAS(new ModelUrn(splitted[0]), splitted[2]);
				if(smDescriptor == null) {
					throw new ResourceNotFoundException("Specified SubmodelId '" + splitted[2] + "' does not exist in AAS '" + splitted[0] + "'.");
				}
				return smDescriptor;
			}
			
			//path has more than three elements and is therefore invalid
			throw new MalformedRequestException("Given path '" + path + "' contains more than three path elements and is therefore invalid.");
		}
	}

	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		String[] splitted = preparePath(path);

		if (splitted.length > 0) { // Overwriting existing entry
			//if path contains more or less than an aasID after the prefix

			// Decode encoded path
			ModelUrn identifier = new ModelUrn(splitted[0]);
			
			if (splitted.length == 1) {
				// Typically, VAB SET should not create new entries. Nevertheless, the registry
				// API is defined to do it.
				AASDescriptor desc = createAASDescriptorFromMap(newValue);

				// Ensure the passed identifier is equals the
				String descId = desc.getIdentifier().getId();
				String urlId = splitted[0];
				if (descId.equals(urlId)) {
					registry.register(desc);
				} else {
					throw new MalformedRequestException("The Identifier " + descId + " in the descriptor does not match the URL with id " + urlId);
				}
			} else if (splitted.length == 3) {
				SubmodelDescriptor smDesc = createSMDescriptorFromMap(newValue);
				registry.register(identifier, smDesc);
			} else {
				throw new MalformedRequestException("Unknown path " + path);
			}
		} else {
			throw new MalformedRequestException("Set with empty path is not supported by registry");
		}
	}

	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		throw new MalformedRequestException("Create (POST) on a registry is not supported. Please, use put");
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		String[] splitted = preparePath(path);
			
		if (splitted.length == 1) { //delete an aas
			
			ModelUrn aasId = new ModelUrn(splitted[0]);
			
			//aas to be deleted does not exist
			if(registry.lookupAAS(aasId) == null) {
				throw new ResourceNotFoundException("AAS '" + splitted[0] + "' to be deleted does not exist.");
			}
			
			registry.delete(aasId);
			
		} else if(splitted.length == 3) { //delete a submodel
			ModelUrn aasId = new ModelUrn(splitted[0]);
			String smId = splitted[2];
			// a submodel with this Id does not exist in given aas
			// getSmDescriptorFromAAS also checks if aas exists
			SubmodelDescriptor smDesc = getSmDescriptorFromAAS(aasId, smId);
			if (smDesc == null) {
				throw new ResourceNotFoundException("A Submodel with id '" + smId + "' does not exist in aas '" + splitted[0] + "'.");
			}

			registry.delete(aasId, smDesc.getIdentifier());
		} else {
			throw new MalformedRequestException("Delete with empty path is not supported by registry");
		}
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
	 * Gets all SubmodelDescriptor objects form an aas.
	 * Throws RuntimeException if aas does not exist.
	 * 
	 * @param id id of the aas
	 * @return Set of contained SubmodelDescriptor objects
	 * @throws ResourceNotFoundException if the AAS does not exist
	 */
	private Collection<SubmodelDescriptor> getSmDescriptorsFromAAS(IIdentifier id) throws ResourceNotFoundException {
		AASDescriptor aasDescriptor = registry.lookupAAS(id);
		if(aasDescriptor == null) {
			throw new ResourceNotFoundException("Specified AASid '" + id.getId() + "' does not exist.");
		}
		return aasDescriptor.getSubmodelDescriptors();
	}
	
	/**
	 * Gets a specific SubmodelDescriptor form an aas.
	 * Throws RuntimeException if aas does not exist.
	 * 
	 * @param aasId id of the aas
	 * @param smId id of the submodel
	 * @return the SubmodelDescriptor with the given id
	 * @throws ResourceNotFoundException if aasId does not exist
	 */
	private SubmodelDescriptor getSmDescriptorFromAAS(IIdentifier aasId, String smId)
			throws ResourceNotFoundException {
		AASDescriptor aasDescriptor = registry.lookupAAS(aasId);
		if(aasDescriptor == null) {
			throw new ResourceNotFoundException("Specified AASId '" + aasId.getId() + "' does not exist.");
		}
		
		SubmodelDescriptor smDescriptor = aasDescriptor.getSubmodelDescriptorFromIdentifierId(smId);
		if (smDescriptor == null) {
			throw new ResourceNotFoundException("Specified SMId '" + smId + "' for AAS " + aasId.getId() + " does not exist.");
		}
		return smDescriptor;
	}

}
