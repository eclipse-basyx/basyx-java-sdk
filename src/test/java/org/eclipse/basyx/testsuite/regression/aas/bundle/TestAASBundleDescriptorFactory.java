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
package org.eclipse.basyx.testsuite.regression.aas.bundle;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.bundle.AASBundleDescriptorFactory;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.junit.Test;

/**
 * Tests the methods of AASBundleDescriptorFactory for their correctness
 * 
 * @author schnicke
 *
 */
public class TestAASBundleDescriptorFactory {
	@Test
	public void testDescriptorCreation() {
		String aasId = "aasId";
		AssetAdministrationShell shell = new AssetAdministrationShell();
		shell.setIdentification(new Identifier(IdentifierType.CUSTOM, aasId));

		String smId = "smId";
		Submodel sm = new Submodel();
		sm.setIdShort(smId);
		sm.setIdentification(IdentifierType.IRI, "aasIdIRI");

		AASBundle bundle = new AASBundle(shell, Collections.singleton(sm));

		String basePath = "http://localhost:4040/test";
		AASDescriptor desc = AASBundleDescriptorFactory.createAASDescriptor(bundle, basePath);

		String aasPath = VABPathTools.concatenatePaths(basePath, AASAggregatorProvider.PREFIX, aasId, "aas");
		String smPath = VABPathTools.concatenatePaths(aasPath, "submodels", sm.getIdShort(), "submodel");
		assertEquals(aasPath, desc.getFirstEndpoint());
		assertEquals(smPath, desc.getSubmodelDescriptorFromIdShort(smId).getFirstEndpoint());

	}

}
