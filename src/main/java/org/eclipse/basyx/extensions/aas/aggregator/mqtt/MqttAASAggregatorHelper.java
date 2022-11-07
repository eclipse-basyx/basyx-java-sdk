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

/**
 * A helper class containing string constants of topics used by the
 * AASAggregator.
 * 
 * @author danish
 *
 */
public class MqttAASAggregatorHelper {
	public static final String TOPIC_CREATEAAS = "BaSyxAggregator_createdAAS";
	public static final String TOPIC_DELETEAAS = "BaSyxAggregator_deletedAAS";
	public static final String TOPIC_UPDATEAAS = "BaSyxAggregator_updatedAAS";

	/***
	 * Create payload for changed aas which includes creating, deleting, and
	 * updating the corresponding aas.
	 * 
	 * @param aasId
	 *            - the id of the changed aas
	 * @return Mqtt-message payload
	 */
	public static String createAASChangedPayload(String aasId) {
		return aasId;
	}
}
