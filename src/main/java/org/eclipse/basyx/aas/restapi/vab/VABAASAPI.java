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
package org.eclipse.basyx.aas.restapi.vab;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.AASAPIHelper;
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
		Map<String, Object> map = (Map<String, Object>) provider.getValue(AASAPIHelper.getAASPath());
		return AssetAdministrationShell.createAsFacade(map);
	}

	@Override
	public void addSubmodel(IReference submodel) {
		provider.createValue(AASAPIHelper.getSubmodelsPath(), submodel);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void removeSubmodel(String id) {
		Collection<Map<String, Object>> smReferences = (Collection<Map<String, Object>>) provider.getValue(AASAPIHelper.getSubmodelsPath());
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
				provider.deleteValue(AASAPIHelper.getSubmodelsPath(), ref);
				break;
			}
		}
	}
}
