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

package org.eclipse.basyx.extensions.aas.aggregator.aasxupload;

import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.bundle.AASBundleHelper;
import org.eclipse.basyx.aas.factory.aasx.AASXToMetamodelConverter;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.extensions.aas.aggregator.aasxupload.api.IAASAggregatorAASXUpload;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * An implementation of the IAASAggregatorAASXUpload interface using maps internally
 * with the support of AASX upload via {@link InputStream}
 * 
 * @author haque
 *
 */
public class AASAggregatorAASXUpload implements IAASAggregatorAASXUpload {
	private IAASAggregator aggregator;
	/**
	 * Constructs default AAS Aggregator with AASX upload
	 */
	public AASAggregatorAASXUpload(IAASAggregator aggregator) {
		this.aggregator = aggregator;
	}

	@Override
    public void uploadAASX(InputStream aasxStream) {
        try {
            AASXToMetamodelConverter converter = new AASXToMetamodelConverter(aasxStream);
            Set<AASBundle> bundles = converter.retrieveAASBundles();
            AASBundleHelper.integrate(this, bundles);
        } catch (Exception e) {
            throw new MalformedRequestException("invalid request to aasx path without valid aasx input stream");
        }
    }

	@Override
	public Collection<IAssetAdministrationShell> getAASList() {
		return aggregator.getAASList();
	}

	@Override
	public IAssetAdministrationShell getAAS(IIdentifier aasId) throws ResourceNotFoundException {
		return aggregator.getAAS(aasId);
	}

	@Override
	public IModelProvider getAASProvider(IIdentifier aasId) throws ResourceNotFoundException {
		return aggregator.getAASProvider(aasId);
	}

	@Override
	public void createAAS(AssetAdministrationShell aas) {
		aggregator.createAAS(aas);
	}

	@Override
	public void updateAAS(AssetAdministrationShell aas) throws ResourceNotFoundException {
		aggregator.updateAAS(aas);
	}

	@Override
	public void deleteAAS(IIdentifier aasId) {
		aggregator.deleteAAS(aasId);
	}
}
