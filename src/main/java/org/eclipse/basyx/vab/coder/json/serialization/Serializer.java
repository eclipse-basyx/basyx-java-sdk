/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.coder.json.serialization;

/**
 * A BaSys serializer
 * 
 * @author kuhn
 *
 */
public interface Serializer {
	/**
	 * Serialize a primitive or complex value into JSON object
	 */
	public String serialize(Object value);

	
	/**
	 * Deserialize a primitive or complex value from JSON object
	 */
	public Object deserialize(String serializedValue);
}
