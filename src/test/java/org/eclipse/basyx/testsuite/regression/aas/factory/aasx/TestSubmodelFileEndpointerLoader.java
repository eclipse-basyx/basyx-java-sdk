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


package org.eclipse.basyx.testsuite.regression.aas.factory.aasx;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.eclipse.basyx.aas.factory.aasx.SubmodelFileEndpointLoader;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IFile;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the SubmodelFileEndpointLoader
 * 
 * @author espen
 *
 */
public class TestSubmodelFileEndpointerLoader {
	private Submodel submodel;
	private static final String RELATIVE_PATH = "/file/root/text.txt";
	private static final String ABOSLUTE_PATH = "http://localhost:1234/file/root/text.txt";
	private static final String RELATIVE_TARGET_PATH = "http://localhost:4321/new/file/root/text.txt";

	@Before
	public void setup() {
		File fRel = new File(RELATIVE_PATH, "application/json");
		fRel.setIdShort("fRel");
		File fAbs = new File(ABOSLUTE_PATH, "application/json");
		fAbs.setIdShort("fAbs");
		SubmodelElementCollection col = new SubmodelElementCollection();
		col.setIdShort("fileCollection");
		File fCol = new File(RELATIVE_PATH, "application/json");
		fCol.setIdShort("fInside");
		col.addSubmodelElement(fCol);
		submodel = new Submodel("FileTestSubmodel", new Identifier(IdentifierType.IRDI, "FileTestSubmodel"));
		submodel.addSubmodelElement(fRel);
		submodel.addSubmodelElement(fAbs);
		submodel.addSubmodelElement(col);
	}

	/**
	 * Tests setting a static string endpoint (relative to the given path in the
	 * existing value)
	 */
	@Test
	public void testRelativePaths1() {
		SubmodelFileEndpointLoader.setRelativeFileEndpoints(submodel, "http://localhost:4321/new");
		checkRelativeTargetPaths();
	}

	/**
	 * Tests setting a endpoint via host, port and root path (relative to the given
	 * path in the existing value)
	 */
	@Test
	public void testRelativePaths2() {
		SubmodelFileEndpointLoader.setRelativeFileEndpoints(submodel, "localhost", 4321, "/new");
		checkRelativeTargetPaths();
	}

	/**
	 * Tests elements inside of collections
	 */
	@Test
	public void testCollections() {
		SubmodelFileEndpointLoader.setRelativeFileEndpoints(submodel, "localhost", 4321, "/new");

		Map<String, ISubmodelElement> elements = submodel.getSubmodelElements();
		SubmodelElementCollection col = (SubmodelElementCollection) elements.get("fileCollection");
		IFile file = (IFile) col.getSubmodelElements().get("fInside");
		assertEquals(RELATIVE_TARGET_PATH, file.getValue());
	}

	private void checkRelativeTargetPaths() {
		Map<String, ISubmodelElement> elements = submodel.getSubmodelElements();

		String fromRelative = ((IFile) elements.get("fRel")).getValue();
		assertEquals(RELATIVE_TARGET_PATH, fromRelative);

		String fromAbsolute = ((IFile) elements.get("fAbs")).getValue();
		assertEquals(RELATIVE_TARGET_PATH, fromAbsolute);
	}
}
