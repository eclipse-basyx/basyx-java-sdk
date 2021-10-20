/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.restapi.operation;

import java.util.Map;

import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * Direct response when invoking an async operation
 * 
 * @author espen
 *
 */
public class CallbackResponse extends VABModelMap<Object> {
	public static final String REQUESTID = "requestId";
	public static final String CALLBACKURL = "callbackUrl";
	
	public CallbackResponse() {
	}
	
	public static CallbackResponse createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		CallbackResponse ret = new CallbackResponse();
		ret.setRequestId((String) map.get(REQUESTID));
		ret.setCallbackUrl((String) map.get(CALLBACKURL));

		return ret;
	}

	public CallbackResponse(String requestId, String url) {
		setRequestId(requestId);
		put(CALLBACKURL, url);
	}

	public String getRequestId() {
		return (String) get(REQUESTID);
	}

	public void setRequestId(String requestId) {
		put(REQUESTID, requestId);
	}

	public String getCallbackUrl() {
		return (String) get(CALLBACKURL);
	}

	public void setCallbackUrl(String callbackUrl) {
		put(CALLBACKURL, callbackUrl);
	}
}
