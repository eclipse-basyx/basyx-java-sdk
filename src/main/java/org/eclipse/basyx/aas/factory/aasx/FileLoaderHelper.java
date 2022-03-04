/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
