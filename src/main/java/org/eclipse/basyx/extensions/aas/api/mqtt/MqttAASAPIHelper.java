/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.api.mqtt;

/**
 * A helper class containing string constants of topics used by the AASAPI.
 * 
 * @author fried
 *
 */
public class MqttAASAPIHelper {
	public static final String TOPIC_ADDSUBMODEL = "BaSyxAAS_addedSubmodelReference";
	public static final String TOPIC_REMOVESUBMODEL = "BaSyxAAS_removedSubmodelReference";
}
