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
package org.eclipse.basyx.aas.restapi;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.vab.VABAASAPI;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.NotAnInvokableException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;

/**
 * Model provider explicitely meant to implement the access to the AAS object.
 * This excludes access to the submodels, that are wrapped into their own
 * provider.
 * 
 * @author espen
 *
 */
public class AASModelProvider implements IModelProvider {

	private IAASAPI aasApi;

	/**
	 * Constructor based on the model provider containing the AAS model. This is
	 * based on the default AAS API
	 */
	public AASModelProvider(IModelProvider modelProvider) {
		aasApi = new VABAASAPI(modelProvider);
	}

	/**
	 * Creates an AASModelProvider based on a lambda provider and a given model
	 */
	public AASModelProvider(AssetAdministrationShell shell) {
		this.aasApi = new VABAASAPI(new VABLambdaProvider(shell));
	}

	/**
	 * Creates an AASModelProvider based on a passed AAS API
	 * 
	 * @param aasApi
	 */
	public AASModelProvider(IAASAPI aasApi) {
		this.aasApi = aasApi;
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		path = preparePath(path);
		if (path.isEmpty()) {
			Object value =  aasApi.getAAS();
			value  = addAssetReferenceIfNecessary(value);
			return value;
		} else {
			throw new MalformedRequestException("Path " + path + " is not supported");
		}
	}

	/**
	 * Ass asset reference to the aas if it is a call to AAS or /shells
	 * 
	 * @param value
	 * @return modified value with asset reference
	 */
	private Object addAssetReferenceIfNecessary(Object value) {
		if (value instanceof AssetAdministrationShell) {
			AssetAdministrationShell aas = (AssetAdministrationShell) value;
			aas = addAssetReferenceInAAS(aas);
			return aas;
			// }
			// else if (value instanceof List<?>) {
			// @SuppressWarnings("unchecked")
			// List<Object> valueList = (List<Object>) value;
			// if (!valueList.isEmpty() && valueList.get(0) instanceof
			// AssetAdministrationShell) {
			// valueList.forEach(v -> {
			// AssetAdministrationShell aas = (AssetAdministrationShell) v;
			// v = addAssetReferenceInAAS(aas);
			// });
			// }
			// return valueList;
		} else {
			return value;
		}
	}

	/**
	 * Add asset reference to asset of the aas
	 * 
	 * @param aas
	 * @return aas with the modified asset
	 */
	private AssetAdministrationShell addAssetReferenceInAAS(AssetAdministrationShell aas) {
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, Object> asset = (LinkedHashMap<String, Object>) aas.get("asset");
		LinkedHashMap<String, Object> assetMap = addAssetReferenceToAsset(asset);
		aas.put("asset", assetMap);
		return aas;
	}

	/**
	 * Add asset reference to an asset raw map
	 * 
	 * @param asset
	 * @return modified asset map
	 */
	private LinkedHashMap<String, Object> addAssetReferenceToAsset(LinkedHashMap<String, Object> asset) {
		Reference assetReference = createAssetReference(asset);
		LinkedHashMap<String, Object> modifiedAsset = new LinkedHashMap<>();
		modifiedAsset.put("keys", assetReference.getKeys());
		modifiedAsset.putAll(asset);
		return modifiedAsset;
	}

	/**
	 * Create an asset reference from an asset raw map
	 * 
	 * @param asset
	 * @return asset reference
	 */
	private Reference createAssetReference(LinkedHashMap<String, Object> asset) {
		@SuppressWarnings("unchecked")
		Map<String, Object> assetIdMap = (Map<String, Object>) asset.get("identification");
		String idType = (String) assetIdMap.get("idType");
		String id = (String) assetIdMap.get("id");
		Identifier assetId = new Identifier(IdentifierType.fromString(idType), id);
		return new Reference(assetId, KeyElements.ASSET, true);
	}

	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		throw new MalformedRequestException("For an AAS, Set is not supported");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		path = preparePath(path);
		if (path.equals("submodels")) {
			Map<String, Object> smMap = (Map<String, Object>) newEntity;
			Submodel sm = Submodel.createAsFacade(smMap);
			// It is allowed to overwrite existing submodels
			aasApi.removeSubmodel(sm.getIdentification().getId());
			aasApi.addSubmodel(sm.getReference());
		} else {
			throw new MalformedRequestException("Path " + path + " is not supported");
		}
	}

	/**
	 * @param path
	 * @return
	 */
	private String preparePath(String path) {
		VABPathTools.checkPathForNull(path);
		path = VABPathTools.stripSlashes(path);
		return path;
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		path = preparePath(path);
		String[] splitted = VABPathTools.splitPath(path);
		if (splitted.length == 3 && splitted[1].equals(AssetAdministrationShell.SUBMODELS)) {
			String id = VABPathTools.decodePathElement(splitted[2]);
			aasApi.removeSubmodel(id);
		} else {
			throw new MalformedRequestException("Delete on path " + path + " is not supported");
		}
	}

	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {
		throw new MalformedRequestException("Delete with parameter is not supported");
	}

	/**
	 * Operations that can be invoked are not contained inside of AAS, but inside of
	 * submodels
	 */
	@Override
	public Object invokeOperation(String path, Object... parameter) throws ProviderException {
		throw new NotAnInvokableException("An AAS does not contain any operations that can be invoked");
	}
}
