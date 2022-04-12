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
package org.eclipse.basyx.aas.factory.aasx;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.AasEnv;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;

/**
 * Helper class providing methods for handling inconsistencies in regard to
 * BaSyx and AASXPackageExplorer
 * 
 * @author fried
 *
 */
public class AASXPackageExplorerConformantHelper {
	
	/**
	 * Converts meta model elements so that the AASXPackageExplorer can load their
	 * serialized AASX. This method modifies the passed parameter Asset Administration Shell List.
	 * As the passed Asset Administration Shell List contains objects as references thatswhy original 
	 * List is modified.
	 * 
	 * @param aasList
	 * @param assetList
	 * @param conceptDescriptionList
	 * @param submodelList
	 * @return
	 */
	public static AasEnv adapt(Collection<IAssetAdministrationShell> aasList, Collection<IAsset> assetList,
		Collection<IConceptDescription> conceptDescriptionList, Collection<ISubmodel> submodelList) {

		Collection<IAssetAdministrationShell> convertedAASs = aasList.stream()
				.map(AASXPackageExplorerConformantHelper::removeFirstKeyFromSubmodelReferences).collect(Collectors.toList());

		return new AasEnv(convertedAASs, assetList, conceptDescriptionList, submodelList);
	}

	/**
	 * Converts meta model elements so that the AASXPackageExplorer can load their
	 * serialized AASX
	 * 
	 * @param env
	 * @return
	 */
	public static AasEnv adapt(AasEnv env) {
		return adapt(env.getAssetAdministrationShells(), env.getAssets(), env.getConceptDescriptions(),
				env.getSubmodels());
	}

	private static IAssetAdministrationShell removeFirstKeyFromSubmodelReferences(IAssetAdministrationShell shell) {
		Collection<IReference> convertedReferences = shell.getSubmodelReferences().stream()
				.map(AASXPackageExplorerConformantHelper::removeFirstKeyElement).collect(Collectors.toList());

		((AssetAdministrationShell) shell).setSubmodelReferences(convertedReferences);

		return shell;
	}

	private static IReference removeFirstKeyElement(IReference reference) {
		List<IKey> keys = reference.getKeys();

		if(!keys.isEmpty() && keys.get(0).getType().equals(KeyElements.ASSETADMINISTRATIONSHELL)) {
			keys.remove(0);
			
			return new Reference(keys);
		}

		return reference;
	}
}
