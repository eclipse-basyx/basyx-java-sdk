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
