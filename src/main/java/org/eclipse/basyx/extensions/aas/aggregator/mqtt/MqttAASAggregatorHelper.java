/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.aggregator.mqtt;

/**
 * A helper class for MqttAASAggregator. 
 * 
 * @author danish
 *
 */
public class MqttAASAggregatorHelper {
	public static final String TOPIC_CREATEAAS = "BaSyxAggregator_createdAAS";
	public static final String TOPIC_DELETEAAS = "BaSyxAggregator_deletedAAS";
	public static final String TOPIC_UPDATEAAS = "BaSyxAggregator_updatedAAS";
}
