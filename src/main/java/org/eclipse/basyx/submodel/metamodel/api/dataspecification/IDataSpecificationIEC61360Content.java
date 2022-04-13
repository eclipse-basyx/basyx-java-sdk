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
package org.eclipse.basyx.submodel.metamodel.api.dataspecification;

import java.util.Collection;

import org.eclipse.basyx.submodel.metamodel.api.dataspecification.enums.DataTypeIEC61360;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.enums.LevelType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;

/**
 * Interface for DataSpecification In DAAS, this element is named
 * DataSpecificationIEC61360, but for clarity "Content" is added to the
 * interface name
 *
 * @author rajashek, espen
 *
 */
public interface IDataSpecificationIEC61360Content extends IDataSpecificationContent {
	public LangStrings getPreferredName();

	public LangStrings getShortName();

	public String getUnit();

	public IReference getUnitId();

	public String getSourceOfDefinition();

	public String getSymbol();

	public DataTypeIEC61360 getDataType();

	public LangStrings getDefinition();

	public String getValueFormat();

	public Collection<IValueReferencePair> getValueList();

	public String getValue();

	public IReference getValueId();

	public LevelType getLevelType();
}
