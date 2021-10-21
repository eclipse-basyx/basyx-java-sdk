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

import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.extensions.aas.aggregator.aasxupload.AASAggregatorAASXUpload;

/**
 * Tests AAS Aggragator with AASX upload functionality
 * @author haque
 *
 */
public class TestAASAggregatorAASXUpload extends TestAASAggregatorAASXUploadSuite{
	@Override
	protected IAASAggregator getAggregator() {
		return new AASAggregatorAASXUpload(new AASAggregator());
	}
}
