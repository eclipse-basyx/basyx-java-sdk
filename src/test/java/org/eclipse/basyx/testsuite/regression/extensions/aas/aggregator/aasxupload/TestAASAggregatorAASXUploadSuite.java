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
 * 
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

		checkAAS1(shells);

		checkAAS2(shells);
	}

	private static void checkAAS2(Collection<IAssetAdministrationShell> shells) {
		String aasId2Identifier = "www.admin-shell.io/aas-sample/1/1";

		IAssetAdministrationShell shell2 = getShellByIdentifier(shells, aasId2Identifier);
		assertEquals("test_asset_aas", shell2.getIdShort());

		Iterator<IReference> smIteratorShell2 = shell2.getSubmodelReferences().iterator();
		IReference shell2Sm1 = smIteratorShell2.next();
		assertEquals("de.iese.com/ids/sm/0000_000_000_001", shell2Sm1.getKeys().get(0).getValue());
	}

	private static void checkAAS1(Collection<IAssetAdministrationShell> shells) {
		String aasId1Identifier = "smart.festo.com/demo/aas/1/1/454576463545648365874";
		IAssetAdministrationShell shell1 = getShellByIdentifier(shells, aasId1Identifier);

		assertEquals("Festo_3S7PM0CP4BD", shell1.getIdShort());

		Iterator<IReference> smIteratorShell1 = shell1.getSubmodelReferences().iterator();
		IReference shell1Sm1 = smIteratorShell1.next();
		assertEquals("www.company.com/ids/sm/4343_5072_7091_3242", shell1Sm1.getKeys().get(0).getValue());
		IReference shell1Sm2 = smIteratorShell1.next();
		assertEquals("www.company.com/ids/sm/2543_5072_7091_2660", shell1Sm2.getKeys().get(0).getValue());
		IReference shell1Sm3 = smIteratorShell1.next();
		assertEquals("smart.festo.com/demo/sm/instance/1/1/13B7CCD9BF7A3F24", shell1Sm3.getKeys().get(0).getValue());
		IReference shell1Sm4 = smIteratorShell1.next();
		assertEquals("www.company.com/ids/sm/6053_5072_7091_5102", shell1Sm4.getKeys().get(0).getValue());
		IReference shell1Sm5 = smIteratorShell1.next();
		assertEquals("www.company.com/ids/sm/6563_5072_7091_4267", shell1Sm5.getKeys().get(0).getValue());
	}

	private static IAssetAdministrationShell getShellByIdentifier(Collection<IAssetAdministrationShell> shells, String aasId1Identifier) {
		return shells.stream().filter(s -> s.getIdentification().getId().equals(aasId1Identifier)).findFirst().get();
	}
}
