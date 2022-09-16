/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.shared.mqtt;

/**
 * Abstract class containing basic functionality to parse Payloads
 * 
 * @author fried
 *
 */
public abstract class PayloadParser {
	protected String payload;

	public PayloadParser(String payload) {
		this.payload = payload;
	}

	protected String[] extractIds(String payload) {
		String tmpPayload = removeOuterBrackets(payload);
		return tmpPayload.split(",");
	}

	protected static String removeOuterBrackets(String str) {
		if (!(str.startsWith("(") && str.endsWith(")"))) {
			return str;
		}
		return str.substring(1, str.length() - 1);
	}
}
