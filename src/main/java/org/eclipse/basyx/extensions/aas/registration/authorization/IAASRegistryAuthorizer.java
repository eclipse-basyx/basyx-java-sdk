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
package org.eclipse.basyx.extensions.aas.registration.authorization;

import java.util.List;
import java.util.function.Supplier;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Interface for the authorization points used in {@link AuthorizedAASRegistry}.
 *
 * @author wege
 */
public interface IAASRegistryAuthorizer<SubjectInformationType> {
  /**
   * Checks authorization for {@link IAASRegistry#register(AASDescriptor)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aasDescriptor
   *                           descriptor of the AAS.
   * @throws InhibitException if authorization failed
   */
  public void authorizeRegisterAas(
      final SubjectInformationType subjectInformation,
      final AASDescriptor aasDescriptor
  ) throws InhibitException;

  /**
   * Checks authorization for {@link IAASRegistry#register(IIdentifier, SubmodelDescriptor)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aasId
   *                           id of the AAS.
   * @param smDescriptor
   *                           the submodel descriptor.
   * @throws InhibitException if authorization failed
   */
  public void authorizeRegisterSubmodel(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId,
      final SubmodelDescriptor smDescriptor
  ) throws InhibitException;

  /**
   * Checks authorization for {@link IAASRegistry#delete(IIdentifier)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aasId
   *                           id of the AAS.
   * @throws InhibitException if authorization failed
   */
  public void authorizeUnregisterAas(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId
  ) throws InhibitException;

  /**
   * Checks authorization for {@link IAASRegistry#delete(IIdentifier, IIdentifier)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aasId
   *                           id of the AAS.
   * @param smId
   *                           id of the submodel.
   * @throws InhibitException if authorization failed
   */
  public void authorizeUnregisterSubmodel(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId,
      final IIdentifier smId
  ) throws InhibitException;

  /**
   * Checks authorization for {@link IAASRegistry#lookupAAS(IIdentifier)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aasId
   *                           id of the AAS.
   * @param aasSupplier
   *                           supplier for the AAS.
   * @return the authorized AAS descriptor.
   * @throws InhibitException if authorization failed
   */
  public AASDescriptor authorizeLookupAAS(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId,
      final Supplier<AASDescriptor> aasSupplier
  ) throws InhibitException;

  /**
   * Checks authorization for {@link IAASRegistry#lookupAll()}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aasDescriptorsSupplier
   *                           supplier for the list of AAS descriptors.
   * @return the authorized list of AAS descriptors.
   * @throws InhibitException if authorization failed
   */
  public List<AASDescriptor> authorizeLookupAll(
      final SubjectInformationType subjectInformation,
      final Supplier<List<AASDescriptor>> aasDescriptorsSupplier
  ) throws InhibitException;

  /**
   * Checks authorization for {@link IAASRegistry#lookupSubmodels(IIdentifier)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aasId
   *                           id of the AAS.
   * @param submodelDescriptorsSupplier
   *                           supplier for the list of submodel descriptors.
   * @return the authorized list of submodel descriptors.
   * @throws InhibitException if authorization failed
   */
  public List<SubmodelDescriptor> authorizeLookupSubmodels(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId,
      final Supplier<List<SubmodelDescriptor>> submodelDescriptorsSupplier
  ) throws InhibitException;

  /**
   * Checks authorization for {@link IAASRegistry#lookupSubmodel(IIdentifier, IIdentifier)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aasId
   *                           id of the AAS.
   * @param smId
   *                           id of the submodel.
   * @param smSupplier
   *                           supplier for the submodel.
   * @return the authorized submodel descriptor.
   * @throws InhibitException if authorization failed
   */
  public SubmodelDescriptor authorizeLookupSubmodel(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId,
      final IIdentifier smId,
      final Supplier<SubmodelDescriptor> smSupplier
  ) throws InhibitException;
}