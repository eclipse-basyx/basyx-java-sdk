/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.coder.json.metaprotocol;

import org.eclipse.basyx.vab.exception.provider.ProviderException;

/**
 * 
 * @author zhangzai
 *
 */

public interface IMetaProtocolHandler {
	
	/**
	 * Deserialize the returned JSON String, handle meta-information of the protocol and return response object
	 * 
	 * @param message 
	 * 				serialized JSON String
	 * @return
	 * 				response object with handled meta-information
	 */
	public Object deserialize(String message) throws ProviderException;

}
