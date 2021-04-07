/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.restapi.vab;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Implements the AAS API by mapping it to the VAB
 * 
 * @author schnicke
 *
 */
public class VABAASAPI implements IAASAPI {

	// The VAB model provider containing the model this API implementation is based
	// on
	private IModelProvider provider;

	/**
	 * Creates a VABAASAPI that wraps an IModelProvider
	 * 
	 * @param provider
	 *            providing the AAS
	 */
	public VABAASAPI(IModelProvider provider) {
		super();
		this.provider = provider;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IAssetAdministrationShell getAAS() {
		// For access on the container property root, return the whole model
		Map<String, Object> map = (Map<String, Object>) provider.getValue("");
		return AssetAdministrationShell.createAsFacade(map);
	}

	@Override
	public void addSubmodel(IReference submodel) {
		provider.createValue(AssetAdministrationShell.SUBMODELS, submodel);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void removeSubmodel(String id) {
		Collection<Map<String, Object>> smReferences = (Collection<Map<String, Object>>) provider.getValue(AssetAdministrationShell.SUBMODELS);
		// Reference to submodel could be either by idShort (=> local) or directly via
		// its identifier
		for (Iterator<Map<String, Object>> iterator = smReferences.iterator(); iterator.hasNext();) {
			Map<String, Object> smRefMap = iterator.next();
			IReference ref = Reference.createAsFacade(smRefMap);
			List<IKey> keys = ref.getKeys();
			IKey lastKey = keys.get(keys.size() - 1);
			String idValue = lastKey.getValue();
			// remove this reference, if the last key points to the submodel
			if (idValue.equals(id)) {
				provider.deleteValue(AssetAdministrationShell.SUBMODELS, ref);
				break;
			}
		}
	}
}
