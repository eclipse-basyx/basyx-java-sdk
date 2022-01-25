/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.bundle;

import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * Helper class that supports AASDescriptor utilization for an AASBundle
 * 
 * @author schnicke
 *
 */
public class AASBundleDescriptorFactory {
	/**
	 * Creates the AASDescriptor for the given bundle and aasAggregatorPath
	 * aasAggregatorPath is normalized before the descriptor is created
	 * 
	 * @param bundle
	 * @param aasAggregatorPath
	 * @return
	 */
	public static AASDescriptor createAASDescriptor(AASBundle bundle, String aasAggregatorPath) {
		String nHostBasePath = normalizeAggregatorPrefix(aasAggregatorPath);
		return createAASDescriptorUnchecked(bundle, nHostBasePath);
	}

	private static String normalizeAggregatorPrefix(String aasAggregatorPath) {
		String nHostBasePath = VABPathTools.stripSlashes(aasAggregatorPath);
		if (AASAggregatorProvider.PREFIX.equals(VABPathTools.getLastElement(nHostBasePath)))
			return nHostBasePath;
		return VABPathTools.append(nHostBasePath, AASAggregatorProvider.PREFIX);
	}

	private static AASDescriptor createAASDescriptorUnchecked(AASBundle bundle, String nHostBasePath) {
		String endpointId = bundle.getAAS().getIdentification().getId();
		endpointId = VABPathTools.encodePathElement(endpointId);
		String aasBase = VABPathTools.concatenatePaths(nHostBasePath, endpointId, "aas");
		AASDescriptor desc = new AASDescriptor(bundle.getAAS(), aasBase);
		bundle.getSubmodels().stream().forEach(s -> {
			SubmodelDescriptor smDesc = new SubmodelDescriptor(s, VABPathTools.concatenatePaths(aasBase, "submodels", s.getIdShort(), "submodel"));
			desc.addSubmodelDescriptor(smDesc);
		});
		return desc;
	}
}
