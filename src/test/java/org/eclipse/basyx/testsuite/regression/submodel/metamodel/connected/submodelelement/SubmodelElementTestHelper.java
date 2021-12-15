/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement;

import java.util.Map;
import org.eclipse.basyx.submodel.restapi.SubmodelElementProvider;
import org.eclipse.basyx.testsuite.regression.vab.manager.VABConnectionManagerStub;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.support.TypeDestroyingProvider;

/**
 * A helper class for SubmodelElement.
 * 
 * @author danish
 *
 */

public class SubmodelElementTestHelper {
	/**
	 * Returns a stub for the provided propertyElement
	 * 
	 * @return 
	 */
	public static VABElementProxy createElementProxy(Map<String, Object> propertyElement) {
		VABConnectionManagerStub manager = new VABConnectionManagerStub(
				new TypeDestroyingProvider(new SubmodelElementProvider(new VABLambdaProvider(propertyElement))));
		
		return manager.connectToVABElement("");
	}
	
}
