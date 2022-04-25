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
