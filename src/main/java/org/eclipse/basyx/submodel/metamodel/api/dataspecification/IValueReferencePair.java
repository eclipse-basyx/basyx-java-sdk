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
 * A value reference pair within a value list within a value list of the DataSpecificationIEC61360.
 * Each value has a global unique id defining its semantic.
 * 
 * @author espen
 *
 */
public interface IValueReferencePair {
	public String getValue();

	public IReference getValueId();
}
