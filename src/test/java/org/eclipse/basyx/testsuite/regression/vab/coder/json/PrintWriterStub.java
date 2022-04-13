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
package org.eclipse.basyx.testsuite.regression.vab.coder.json;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PrintWriter stub that simulates response stream
 * 
 * @author pschorn
 *
 */
public class PrintWriterStub extends PrintWriter {

	private static Logger logger = LoggerFactory.getLogger(PrintWriterStub.class);

	private String acceptor;
	private String result;

	public PrintWriterStub(String fileName, String acceptor) throws FileNotFoundException {
		super(fileName);
		// TODO Auto-generated constructor stub

		this.acceptor = acceptor;
	}

	@Override
	public void write(String stream) {
		// check for
		logger.trace("[TEST] Writing to output: {}", stream);

		if (!acceptor.equals("ignore")) {
			assertTrue(acceptor.equals(stream));
		}

		result = stream;
	}

	@Override
	public void flush() {
		// do nothing
		logger.trace("[TEST] Flushing..");
	}

	public String getResult() {
		return result;
	}
}
