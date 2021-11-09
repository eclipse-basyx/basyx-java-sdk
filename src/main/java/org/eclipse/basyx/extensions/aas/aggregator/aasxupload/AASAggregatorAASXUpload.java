/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
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
