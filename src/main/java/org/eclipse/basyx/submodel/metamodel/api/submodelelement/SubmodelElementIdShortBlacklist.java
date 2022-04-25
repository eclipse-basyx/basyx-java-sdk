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
package org.eclipse.basyx.submodel.metamodel.api.submodelelement;

import java.util.Arrays;

/**
 * Tests if an IdShort of a SubmodelElement is blacklisted due to API
 * constraints
 * 
 * @author schnicke
 *
 */
public class SubmodelElementIdShortBlacklist {
	/*
	 * Due to API definition for SubmodelElementCollections
	 * (/submodelElements/$IdShort1/.../$IdShortN) it is not possible to distinguish
	 * if a SubmodelElement with idShort "value" or "invocationList" was requested
	 * or the endpoint defined in the API. Thus, these keywords are forbidden as
	 * idShort
	 * 
	 * E.g. /submodelElements/smCollectionId/value --> Is value here the /value
	 * endpoint or a SubmodelElement with idShort value?
	 */
	public static final String[] BLACKLIST = { "value", "invocationList" };

	/**
	 * Returns true iff an idShort is blacklisted
	 * 
	 * @param idShort
	 *            to test
	 * @return test result
	 */
	public static boolean isBlacklisted(String idShort) {
		return Arrays.asList(BLACKLIST).contains(idShort);
	}
}
