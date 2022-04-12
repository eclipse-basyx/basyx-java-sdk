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
package org.eclipse.basyx.testsuite.regression.vab.protocol.http;

import org.eclipse.basyx.vab.registry.memory.VABInMemoryRegistry;




/**
 * Implement the test suite directory service with pre-configured directory entries
 * 
 * @author kuhn
 *
 */
public class TestsuiteDirectory extends VABInMemoryRegistry {
	
	/**
	 * Constructor - load all directory entries
	 */
	public TestsuiteDirectory() {
		this("http");
	}
	
	/**
	 * Constructor - load all directory entries with link 
	 * protocol defined in the parameter
	 * @param protocol
	 */
	public TestsuiteDirectory(String protocol) {
		defineMapping(protocol);
	}
	
	/**
	 * Define mapping of submodel and vab element
	 */
	private void defineMapping(String protocol) {
		addMapping("urn:fhg:es.iese:vab:1:1:simplevabelement",  protocol + "://localhost:8080/basys.sdk/Testsuite/SimpleVAB/");
		addMapping("urn:fhg:es.iese:aas:1:1:submodel", protocol + "://localhost:8080/basys.sdk/Testsuite/SimpleAASSubmodel/");
	}
}
