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
package org.eclipse.basyx.submodel.metamodel.api.qualifier;

/**
 * Tests the validity of IdShorts according to DotAAS
 * 
 * @author schnicke
 *
 */
public class IdShortValidator {
	/*
	 * Taken from DotAAS p. 50: "Constraint AASd-002: idShort shall only feature
	 * letters, digits, underscore ("_"); starting mandatory with a letter."
	 */
	public static final String IDSHORT_REGEX = "[a-zA-Z][a-zA-Z0-9_]+";

	/**
	 * Returns true iff the idShort is valid according to DotAAS
	 * 
	 * @param idShort
	 *            to test
	 * @return test result
	 */
	public static boolean isValid(String idShort) {
		if (idShort == null) {
			return false;
		}

		return idShort.matches(IDSHORT_REGEX);
	}
}
