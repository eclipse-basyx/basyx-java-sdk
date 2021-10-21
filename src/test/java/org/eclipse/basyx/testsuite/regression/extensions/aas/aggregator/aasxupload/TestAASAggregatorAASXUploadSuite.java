/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.testsuite.regression.extensions.aas.aggregator.aasxupload;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.aas.aggregator.aasxupload.api.IAASAggregatorAASXUpload;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.testsuite.regression.aas.aggregator.AASAggregatorSuite;
import org.junit.Test;

/**
 * Test suite for testing AAS Aggregator along with AASX upload
 * @author haque
 *
 */
public abstract class TestAASAggregatorAASXUploadSuite extends AASAggregatorSuite {
	public static final String AASX_PATH = "src/test/resources/aas/factory/aasx/01_Festo.aasx";
	
	@Test
	public void testUploadAASX() throws ClientProtocolException, IOException {
		File file = Paths.get(AASX_PATH).toFile();
		IAASAggregatorAASXUpload aggregator = (IAASAggregatorAASXUpload) getAggregator();
		aggregator.uploadAASX(new FileInputStream(file));
		checkAASX(aggregator.getAASList());
	}
	
	public static void checkAASX(Collection<IAssetAdministrationShell> shells) {
		assertEquals(2, shells.size());
		
		Iterator<IAssetAdministrationShell> iterator = shells.iterator();
		IAssetAdministrationShell shell1 = iterator.next();
		assertEquals("www.admin-shell.io/aas-sample/1/1", shell1.getIdentification().getId());
		assertEquals("test_asset_aas", shell1.getIdShort());
		
		Iterator<IReference> smIteratorShell1 = shell1.getSubmodelReferences().iterator();
		IReference shell1Sm1 = smIteratorShell1.next();
		assertEquals("de.iese.com/ids/sm/0000_000_000_001", shell1Sm1.getKeys().get(0).getValue());
		
		IAssetAdministrationShell shell2 = iterator.next();
		assertEquals("smart.festo.com/demo/aas/1/1/454576463545648365874", shell2.getIdentification().getId());
		assertEquals("Festo_3S7PM0CP4BD", shell2.getIdShort());
		
		Iterator<IReference> smIteratorShell2 = shell2.getSubmodelReferences().iterator();
		IReference shell2Sm1 = smIteratorShell2.next();
		assertEquals("www.company.com/ids/sm/4343_5072_7091_3242", shell2Sm1.getKeys().get(0).getValue());
		IReference shell2Sm2 = smIteratorShell2.next();
		assertEquals("www.company.com/ids/sm/2543_5072_7091_2660", shell2Sm2.getKeys().get(0).getValue());
		IReference shell2Sm3 = smIteratorShell2.next();
		assertEquals("smart.festo.com/demo/sm/instance/1/1/13B7CCD9BF7A3F24", shell2Sm3.getKeys().get(0).getValue());
		IReference shell2Sm4 = smIteratorShell2.next();
		assertEquals("www.company.com/ids/sm/6053_5072_7091_5102", shell2Sm4.getKeys().get(0).getValue());
		IReference shell2Sm5 = smIteratorShell2.next();
		assertEquals("www.company.com/ids/sm/6563_5072_7091_4267", shell2Sm5.getKeys().get(0).getValue());
	}
}
