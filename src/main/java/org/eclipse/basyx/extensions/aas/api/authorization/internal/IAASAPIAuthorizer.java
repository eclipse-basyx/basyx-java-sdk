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
package org.eclipse.basyx.extensions.aas.api.authorization.internal;

import java.util.function.Supplier;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.extensions.shared.authorization.internal.InhibitException;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;

/**
 * Interface for the authorization points used in {@link AuthorizedAASAPI}.
 *
 * @author wege
 */
public interface IAASAPIAuthorizer<SubjectInformationType> {
	/**
	 * Checks authorization for {@link IAASAPI#getAAS()}.
	 *
	 * @param subjectInformation
	 *                           information of the requester.
	 * @param aasSupplier
	 *                           supplier for the AAS.
	 * @throws InhibitException if authorization failed
	 */
	public IAssetAdministrationShell authorizeGetAAS(
			final SubjectInformationType subjectInformation,
			final Supplier<IAssetAdministrationShell> aasSupplier
  ) throws InhibitException;

	/**
	 * Checks authorization for {@link IAASAPI#addSubmodel(IReference)}.
	 *
	 * @param subjectInformation
	 *                           information of the requester.
	 * @param aasSupplier
	 *                           supplier for the AAS.
	 * @param submodel
	 *                           the submodel.
	 * @throws InhibitException if authorization failed
	 */
	public void authorizeAddSubmodel(
			final SubjectInformationType subjectInformation,
			final Supplier<IAssetAdministrationShell> aasSupplier,
			final IReference submodel
  ) throws InhibitException;

	/**
	 * Checks authorization for {@link IAASAPI#removeSubmodel(String)}.
	 *
	 * @param subjectInformation
	 *                           information of the requester.
	 * @param aasSupplier
	 *                           supplier for the AAS.
	 * @param smIdShortPath
	 *                           short path id of the submodel.
	 * @throws InhibitException if authorization failed
	 */
	public void authorizeRemoveSubmodel(
			final SubjectInformationType subjectInformation,
			final Supplier<IAssetAdministrationShell> aasSupplier,
			final String smIdShortPath
  ) throws InhibitException;
}
