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
