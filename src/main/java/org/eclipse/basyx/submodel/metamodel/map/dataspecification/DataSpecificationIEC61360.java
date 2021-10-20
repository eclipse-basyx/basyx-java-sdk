/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.dataspecification;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IDataSpecificationContent;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IDataSpecificationIEC61360Content;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;

/**
 * An embedded DataSpecification that uses the DataSpecificationIEC61360 as a template
 * 
 * @author espen
 */
public class DataSpecificationIEC61360 extends EmbeddedDataSpecification {
	public static final String TEMPLATE_IRI = "www.admin-shell.io/DataSpecificationTemplates/DataSpecificationIEC61360";
	public static final Reference TEMPLATE_REFERENCE = new Reference(
			new Key(KeyElements.GLOBALREFERENCE, false, TEMPLATE_IRI, KeyType.IRI));

	/**
	 * Creates an empty DataSpecificationIEC61360
	 */
	public DataSpecificationIEC61360() {
		put(DATASPECIFICATION, TEMPLATE_REFERENCE);
	}

	/**
	 * Creates a DataSpecificationIEC61360 with a specific content
	 */
	public DataSpecificationIEC61360(IDataSpecificationIEC61360Content content) {
		this();
		put(CONTENT, content);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IDataSpecificationContent getContent() {
		return DataSpecificationIEC61360Content.createAsFacade((Map<String, Object>) get(CONTENT));
	}

}
