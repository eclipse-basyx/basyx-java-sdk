/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.vab.coder.json;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PrintWriter stub that simulates response stream
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
