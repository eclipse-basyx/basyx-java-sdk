/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.vab.protocol.http.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Helper class to achieve http post multipart/form-data upload
 * @author haque
 *
 */
public class HTTPUploadHelper {
	
	/**
	 * Uploads the given stream to the given API URL
	 * @param stream
	 * @param url
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void uploadHTTPPost(InputStream stream, String url) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = getDefautlHTTPClient();
		HttpPost uploadPost = getUploadHTTPPost(stream, url);
		executeUploadHTTPPost(httpClient, uploadPost);
	}
	
	/**
	 * Executes the client with multipart entity as a post file
	 * @param httpClient
	 * @param uploadFile
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static void executeUploadHTTPPost(CloseableHttpClient httpClient, HttpPost uploadFile) throws ClientProtocolException, IOException {	
		CloseableHttpResponse response = httpClient.execute(uploadFile);
		response.close();
	}

	/**
	 * Gets a default HTTP client
	 * @return
	 * @throws FileNotFoundException
	 */
	private static CloseableHttpClient getDefautlHTTPClient() throws FileNotFoundException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return httpClient;
	}
	
	/**
	 * Gets HTTP post with a multipart entity
	 * @param stream
	 * @param url
	 * @return
	 * @throws FileNotFoundException
	 */
	private static HttpPost getUploadHTTPPost(InputStream stream, String url) throws FileNotFoundException {
		HttpPost uploadFile = new HttpPost(url);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		
		builder.addBinaryBody(
		    "file",
		    stream,
		    ContentType.MULTIPART_FORM_DATA,
		    ""
		);

		HttpEntity multipart = builder.build();
		uploadFile.setEntity(multipart);
		return uploadFile;
	}
}
