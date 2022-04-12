/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.aas.factory.aasx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class for providing InputStream of file from specified path.
 * 
 * @author danish
 *
 */
public class FileLoaderHelper {
	private static Logger logger = LoggerFactory.getLogger(FileLoaderHelper.class);
	
	public static InputStream getInputStream(String aasxFilePath) throws IOException {
		InputStream stream = getResourceStream(aasxFilePath);
		
		if (stream != null) {
			return stream;
		} else {
			try {
				return new FileInputStream(aasxFilePath);
			} catch (FileNotFoundException e) {
				logger.error("File '" + aasxFilePath + "' to be loaded was not found.");
				throw e;
			}
		}
	}

	private static InputStream getResourceStream(String relativeResourcePath) {
		ClassLoader classLoader = AASXToMetamodelConverter.class.getClassLoader();
		
		return classLoader.getResourceAsStream(relativeResourcePath);
	}
}
