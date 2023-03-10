/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.basyx.testsuite.regression.submodel.restapi;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.basyx.aas.aggregator.AASAggregatorFactory;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.AASHTTPServerResource;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

/**
 * Test for the File Upload via HTTP REST
 * 
 * @author fried
 *
 */
public class TestHTTPFileUpload {
	private static final String SERVER = "localhost";
	private static final int PORT = 4000;
	private static final String CONTEXT_PATH = "fileSubmodelElementTest";
	private static final String API_URL = "http://" + SERVER + ":" + PORT + "/" + CONTEXT_PATH + "/shells";
	private static final Object HTTP_200 = "HTTP/1.1 200 ";
	private static final Object HTTP_201 = "HTTP/1.1 201 ";
	private static final Object HTTP_400 = "HTTP/1.1 400 ";
	private AASAggregatorProvider provider = new AASAggregatorProvider(new AASAggregatorFactory().create());

	@Rule
	public AASHTTPServerResource res = new AASHTTPServerResource(new BaSyxContext("/" + CONTEXT_PATH, "", SERVER, PORT)
			.addServletMapping("/*", new VABHTTPInterface<IModelProvider>(provider)));

	@Test
	public void uploadFileToFileSubmodelElement() throws ClientProtocolException, IOException {
		CloseableHttpResponse aasUploadResponse = uploadDummyAAS();
		CloseableHttpResponse submodelUploadResponse = uploadDummySubmodel();
		CloseableHttpResponse submodelElementFileUploadResponse = uploadDummyFileToSubmodelElement("file_sme");
		assertEquals(HTTP_200, aasUploadResponse.getStatusLine().toString());
		assertEquals(HTTP_200, submodelUploadResponse.getStatusLine().toString());
		assertEquals(HTTP_201, submodelElementFileUploadResponse.getStatusLine().toString());
	}

	@Test
	public void uploadFileToNonFileSubmodelElement()
			throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		CloseableHttpResponse aasUploadResponse = uploadDummyAAS();
		CloseableHttpResponse submodelUploadResponse = uploadDummySubmodel();
		CloseableHttpResponse submodelElementFileUploadResponse = uploadDummyFileToSubmodelElement("test_value");
		assertEquals(HTTP_200, aasUploadResponse.getStatusLine().toString());
		assertEquals(HTTP_200, submodelUploadResponse.getStatusLine().toString());
		assertEquals(HTTP_400, submodelElementFileUploadResponse.getStatusLine().toString());
	}

	@Test
	public void fileUploadedCorrectly() throws IOException {
		CloseableHttpResponse aasUploadResponse = uploadDummyAAS();
		CloseableHttpResponse submodelUploadResponse = uploadDummySubmodel();
		CloseableHttpResponse submodelElementFileUploadResponse = uploadDummyFileToSubmodelElement("file_sme");
		assertEquals(HTTP_200, aasUploadResponse.getStatusLine().toString());
		assertEquals(HTTP_200, submodelUploadResponse.getStatusLine().toString());
		assertEquals(HTTP_201, submodelElementFileUploadResponse.getStatusLine().toString());

		File expected = ResourceUtils.getFile("src/test/resources/aas/dummyAAS.json");
		File actual = new File("file_sme.json");

		BufferedInputStream in = new BufferedInputStream(
				new URL(API_URL + "/basyx.examples.test/aas/submodels/test_sm/submodel/submodelElements/file_sme/File")
						.openStream());

		FileOutputStream fileOutputStream = new FileOutputStream(actual);
		byte dataBuffer[] = new byte[1024];
		int bytesRead;
		while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
			fileOutputStream.write(dataBuffer, 0, bytesRead);
		}
		fileOutputStream.close();
		assertEquals(readFile(expected.toPath().toString(), Charset.defaultCharset()),
				readFile(actual.toPath().toString(), Charset.defaultCharset()));

	}

	private CloseableHttpResponse uploadDummyFileToSubmodelElement(String submodelElementIdShort) throws IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();

		File file = ResourceUtils.getFile("src/test/resources/aas/dummyAAS.json");

		HttpPost uploadFile = new HttpPost(
				API_URL + "/basyx.examples.test/aas/submodels/test_sm/submodel/submodelElements/"
						+ submodelElementIdShort + "/File/upload");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();

		builder.addPart("file", new FileBody(file));
		builder.setContentType(ContentType.MULTIPART_FORM_DATA);

		HttpEntity multipart = builder.build();
		uploadFile.setEntity(multipart);

		return client.execute(uploadFile);
	}

	private CloseableHttpResponse uploadDummyAAS()
			throws FileNotFoundException, IOException, UnsupportedEncodingException, ClientProtocolException {
		File file = ResourceUtils.getFile("src/test/resources/aas/dummyAAS.json");
		InputStream in = new FileInputStream(file);
		String aas = IOUtils.toString(in, StandardCharsets.UTF_8.name());
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpPut put = new HttpPut(API_URL + "/basyx.examples.test");
		put.setHeader("Accept", "application/json");
		put.setHeader("Content-type", "application/json");
		StringEntity entity = new StringEntity(aas);
		put.setEntity(entity);
		return client.execute(put);
	}

	@SuppressWarnings("unused")
	private CloseableHttpResponse uploadDummySubmodel()
			throws FileNotFoundException, IOException, UnsupportedEncodingException, ClientProtocolException {
		File file = ResourceUtils.getFile("src/test/resources/aas/dummySubmodel.json");
		InputStream in = new FileInputStream(file);
		String aas = IOUtils.toString(in, StandardCharsets.UTF_8.name());
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpPut put = new HttpPut(API_URL + "/basyx.examples.test/aas/submodels/test_sm");
		put.setHeader("Accept", "application/json");
		put.setHeader("Content-type", "application/json");
		StringEntity entity = new StringEntity(aas);
		put.setEntity(entity);
		return client.execute(put);
	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

}