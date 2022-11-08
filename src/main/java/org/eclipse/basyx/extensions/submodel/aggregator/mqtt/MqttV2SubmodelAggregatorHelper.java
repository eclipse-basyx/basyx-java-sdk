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
package org.eclipse.basyx.extensions.submodel.aggregator.mqtt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.StringJoiner;

/**
 * A helper class containing methods that create topics used by the
 * SubmodelAggregator.
 *
 */
public class MqttV2SubmodelAggregatorHelper {
	private static final String AASREPOSITORY = "aas-repository";
	private static final String SHELLS = "shells";
	private static final String SUBMODELS = "submodels";
	private static final String CREATED = "created";
	private static final String UPDATED = "updated";
	private static final String DELETED = "deleted";
	
	
	public static String getCombinedMessage(String shellId, String submodelId) {
		return "(" + shellId + "," + submodelId + ")";
	}
	
	public static String createCreateSubmodelTopic(String aasId, String repoId) {
		return new StringJoiner("/", "/", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(encodeAASId(aasId))
				.add(SUBMODELS)
				.add(CREATED)
				.toString();		
	}
	
	public static String createUpdateSubmodelTopic(String aasId, String repoId) {
		return new StringJoiner("/", "/", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(encodeAASId(aasId))
				.add(SUBMODELS)
				.add(UPDATED)
				.toString();		
	}
	
	public static String createDeleteSubmodelTopic(String aasId, String repoId) {
		return new StringJoiner("/", "/", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(encodeAASId(aasId))
				.add(SUBMODELS)
				.add(DELETED)
				.toString();		
	}
	
	private static String encodeAASId(String aasId) {
		if (aasId == null) {
			return "<empty>";
		}
		return Base64.getUrlEncoder().withoutPadding().encodeToString(aasId.getBytes(StandardCharsets.UTF_8));
	}
}
