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
package org.eclipse.basyx.extensions.aas.aggregator.mqtt;

import java.util.StringJoiner;

import org.eclipse.basyx.extensions.shared.encoding.IEncoder;
import org.eclipse.basyx.extensions.shared.mqtt.AbstractMqttV2TopicFactory;

/**
 * A helper class containing methods that create topics used by the
 * AASAggregator.
 * 
 */
public class MqttV2AASAggregatorTopicFactory extends AbstractMqttV2TopicFactory {
	private static final String AASREPOSITORY = "aas-repository";
	private static final String SHELLS = "shells";
	private static final String CREATED = "created";
	private static final String UPDATED = "updated";
	private static final String DELETED = "deleted";
	
	/**
	 * @param encoder
	 *            Used for encoding the aasId/submodelId
	 */
	public MqttV2AASAggregatorTopicFactory(IEncoder encoder) {
		super(encoder);
	}

	public String createCreateAASTopic(String repoId) {
		return new StringJoiner("/", "/", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(CREATED)
				.toString();
	}
	
	public String createUpdateAASTopic(String repoId) {
		return new StringJoiner("/", "/", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(UPDATED)
				.toString();
	}
	
	public String createDeleteAASTopic(String repoId) {
		return new StringJoiner("/", "/", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(DELETED)
				.toString();
	}
}
