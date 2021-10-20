/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.api.dataspecification;

import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;

/**
 * DataSpecification containing a DataSpecificationContent
 * Difference to the IDataSpecification:
 * It is not identifiable, but has a reference to an identifiable DataSpecification template
 * 
 * @author espen
 *
 */
public interface IEmbeddedDataSpecification {
	/**
	 * Returns the reference to the identifiable data specification template
	 */
	public IReference getDataSpecificationTemplate();

	/**
	 * Returns the embedded content of the DataSpecification
	 */
	public IDataSpecificationContent getContent();
}
