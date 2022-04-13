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
package org.eclipse.basyx.submodel.metamodel.map.dataspecification;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IDataSpecificationContent;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IDataSpecificationIEC61360Content;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;

/**
 * An embedded DataSpecification that uses the DataSpecificationIEC61360 as a
 * template
 * 
 * @author espen
 */
public class DataSpecificationIEC61360 extends EmbeddedDataSpecification {
	public static final String TEMPLATE_IRI = "www.admin-shell.io/DataSpecificationTemplates/DataSpecificationIEC61360";
	public static final Reference TEMPLATE_REFERENCE = new Reference(new Key(KeyElements.GLOBALREFERENCE, false, TEMPLATE_IRI, KeyType.IRI));

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
