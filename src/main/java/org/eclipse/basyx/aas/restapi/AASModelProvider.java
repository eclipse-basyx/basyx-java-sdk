/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.restapi;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.vab.VABAASAPI;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.NotAnInvokableException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;

/**
 * Model provider explicitely meant to implement the access to the AAS object.
 * This excludes access to the submodels, that are wrapped into their own provider.
 * 
 * @author espen
 *
 */
public class AASModelProvider implements IModelProvider {

	private IAASAPI aasApi;

	/**
	 * Constructor based on the model provider containing the AAS model. This is based
	 * on the default AAS API
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
			return aasApi.getAAS();
		} else {
			throw new MalformedRequestException("Path " + path + " is not supported");
		}
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
			String id = splitted[2];
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
	 * Operations that can be invoked are not contained inside of AAS, but inside of submodels
	 */
	@Override
	public Object invokeOperation(String path, Object... parameter) throws ProviderException {
		throw new NotAnInvokableException("An AAS does not contain any operations that can be invoked");
	}
}
