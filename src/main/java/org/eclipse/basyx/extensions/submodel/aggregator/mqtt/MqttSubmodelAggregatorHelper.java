/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.submodel.aggregator.mqtt;

/**
 * A helper class containing string constants of topics used by the
 * SubmodelAggregator.
 * 
 * @author fischer, jungjan
 *
 */
public class MqttSubmodelAggregatorHelper {
	public static final String TOPIC_CREATESUBMODEL = "BaSyxAggregator_createdSubmodel";
	public static final String TOPIC_DELETESUBMODEL = "BaSyxAggregator_deletedSubmodel";
	public static final String TOPIC_UPDATESUBMODEL = "BaSyxAggregator_updatedSubmodel";

	public static String getCombinedMessage(String shellId, String submodelId) {
		return "(" + shellId + "," + submodelId + ")";
	}
}
