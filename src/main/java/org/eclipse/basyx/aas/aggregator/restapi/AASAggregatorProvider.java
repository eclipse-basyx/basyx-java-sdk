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
package org.eclipse.basyx.aas.aggregator.restapi;

import java.util.Map;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Connects an IAASAggregator to the VAB
 * 
 * @author conradi
 *
 */
public class AASAggregatorProvider implements IModelProvider {

	protected IAASAggregator aggregator;

	public static final String PREFIX = "shells";

	public AASAggregatorProvider(IAASAggregator aggregator) {
		this.aggregator = aggregator;
	}

	/**
	 * Check for correctness of path and returns a stripped path (i.e. no leading
	 * prefix)
	 * 
	 * @param path
	 * @return
	 * @throws MalformedRequestException
	 */
	protected String stripPrefix(String path) throws MalformedRequestException {
		path = VABPathTools.stripSlashes(path);
		if (!path.startsWith(PREFIX)) {
			throw new MalformedRequestException("Path " + path + " not recognized as aggregator path. Has to start with " + PREFIX);
		}
		path = path.replaceFirst(PREFIX, "");
		path = VABPathTools.stripSlashes(path);
		return path;
	}

	/**
	 * Makes sure, that given Object is an AAS by checking its ModelType<br>
	 * Creates a new AAS with the content of the given Map
	 * 
	 * @param value
	 *            the AAS Map object
	 * @return an AAS
	 * @throws MalformedRequestException
	 */
	@SuppressWarnings("unchecked")
	private AssetAdministrationShell createAASFromMap(Object value) throws MalformedRequestException {

		// check if the given value is a Map
		if (!(value instanceof Map)) {
			throw new MalformedRequestException("Given newValue is not a Map");
		}

		Map<String, Object> map = (Map<String, Object>) value;

		// check if the given Map contains an AAS
		String type = ModelType.createAsFacade(map).getName();

		// have to accept Objects without modeltype information,
		// as modeltype is not part of the public metamodel
		if (!AssetAdministrationShell.MODELTYPE.equals(type) && type != null) {
			throw new MalformedRequestException("Given newValue map has not the correct ModelType");
		}

		AssetAdministrationShell aas = AssetAdministrationShell.createAsFacade(map);

		return aas;
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		path = stripPrefix(path);

		if (path.isEmpty()) { // Return all AAS if path is empty
			return aggregator.getAASList();
		} else {
			String[] splitted = VABPathTools.splitPath(path);
			if (splitted.length == 1) { // A specific AAS was requested
				String id = VABPathTools.decodePathElement(splitted[0]);
				IAssetAdministrationShell aas = aggregator.getAAS(new ModelUrn(id));
				return aas;
			} else {
				String id = VABPathTools.decodePathElement(splitted[0]);
				String restPath = VABPathTools.skipEntries(path, 1);
				IIdentifier identifier = new Identifier(IdentifierType.CUSTOM, id);
				return aggregator.getAASProvider(identifier).getValue(restPath);
			}
		}
	}

	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		path = stripPrefix(path);

		if (!path.isEmpty()) { // Overwriting existing entry
			if (!path.contains("/")) { // Update of AAS

				AssetAdministrationShell aas = createAASFromMap(newValue);
				// Decode encoded path
				path = VABPathTools.decodePathElement(path);
				ModelUrn identifier = new ModelUrn(path);

				if (!aas.getIdentification().getId().equals(path)) {
					throw new MalformedRequestException("Given aasID and given AAS do not match");
				}

				try {
					aggregator.getAAS(identifier);
					aggregator.updateAAS(aas);
				} catch (ResourceNotFoundException e) {
					aggregator.createAAS(aas);
				}
			} else { // Update of contained element
				String id = VABPathTools.decodePathElement(VABPathTools.getEntry(path, 0));
				String restPath = VABPathTools.skipEntries(path, 1);
				IIdentifier identifier = new Identifier(IdentifierType.CUSTOM, id);
				aggregator.getAASProvider(identifier).setValue(restPath, newValue);
			}
		} else {
			throw new MalformedRequestException("Set with empty path is not supported by aggregator");
		}
	}

	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		path = stripPrefix(path);

		if (path.isEmpty()) {
			throw new MalformedRequestException("Create with empty path is not supported by aggregator");
		} else {
			String id = VABPathTools.decodePathElement(VABPathTools.getEntry(path, 0));
			String restPath = VABPathTools.skipEntries(path, 1);
			IIdentifier identifier = new Identifier(IdentifierType.CUSTOM, id);
			aggregator.getAASProvider(identifier).createValue(restPath, newEntity);
		}

	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		path = stripPrefix(path);

		if (!path.isEmpty()) { // Deleting an entry
			if (!path.contains("/")) {
				String aasId = VABPathTools.decodePathElement(path);
				IIdentifier identifier = new ModelUrn(aasId);

				if (aggregator.getAAS(identifier) == null) {
					throw new ResourceNotFoundException("Value '" + aasId + "' to be deleted does not exists.");
				}

				aggregator.deleteAAS(identifier);
			} else {
				String id = VABPathTools.decodePathElement(VABPathTools.getEntry(path, 0));
				String restPath = VABPathTools.skipEntries(path, 1);
				IIdentifier identifier = new Identifier(IdentifierType.CUSTOM, id);
				aggregator.getAASProvider(identifier).deleteValue(restPath);
			}
		} else {
			throw new MalformedRequestException("Delete with empty path is not supported by registry");
		}
	}

	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {
		throw new MalformedRequestException("DeleteValue with parameter not supported by aggregator");
	}

	@Override
	public Object invokeOperation(String path, Object... parameter) throws ProviderException {
		path = stripPrefix(path);
		String id = VABPathTools.decodePathElement(VABPathTools.getEntry(path, 0));
		String restPath = VABPathTools.skipEntries(path, 1);
		IIdentifier identifier = new Identifier(IdentifierType.CUSTOM, id);
		return aggregator.getAASProvider(identifier).invokeOperation(restPath, parameter);
	}

}
