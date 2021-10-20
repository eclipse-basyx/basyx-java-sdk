/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
