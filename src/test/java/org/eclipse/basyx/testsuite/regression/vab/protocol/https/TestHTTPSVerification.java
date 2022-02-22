package org.eclipse.basyx.testsuite.regression.vab.protocol.https;

import static org.junit.Assert.assertTrue;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.auth.x500.X500Principal;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.AASHTTPServerResource;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.SimpleVABElementServlet;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.TestsuiteDirectory;
import org.eclipse.basyx.testsuite.regression.vab.support.RecordingProvider;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.eclipse.basyx.vab.protocol.https.HTTPSConnectorProvider;
import org.eclipse.basyx.vab.protocol.https.JerseyHttpsClientFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test whether ssl validation is performed on a https client
 * 
 * @author zhangzai
 *
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestHTTPSVerification {
	@Mock
	private SSLSession sslSessionMock;
	@Mock
	private X509Certificate x509Mock;

	private DefaultHostnameVerifier testVerifier = new DefaultHostnameVerifier();

	private RecordingProvider recorder = new RecordingProvider(new VABMapProvider(new LinkedHashMap<>()));

	/**
	 * Makes sure Tomcat Server is started after before each test case
	 */
	@Rule
	public AASHTTPServerResource res = new AASHTTPServerResource(new BaSyxContext("/basys.sdk", System.getProperty("java.io.tmpdir"), "localhost", 8080, true, "resources/ssl.cert", "pass123")
			.addServletMapping("/Testsuite/SimpleVAB/*", new SimpleVABElementServlet()).addServletMapping("/Testsuite/Recorder/*", new VABHTTPInterface<RecordingProvider>(recorder)));

	@Test(expected = javax.ws.rs.ProcessingException.class)
	public void testValidation() throws KeyManagementException, NoSuchAlgorithmException {
		performRequest("https://localhost:8080/basys.sdk/Testsuite/SimpleVAB");
	}

	@Test(expected = org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException.class)
	public void testValidationFlagHTTPSConnector() {
		HTTPSConnectorProvider httpsConnectorProvider = new HTTPSConnectorProvider();
		httpsConnectorProvider.enableValidation();
		VABConnectionManager connManager = new VABConnectionManager(new TestsuiteDirectory("https"), httpsConnectorProvider);

		VABElementProxy proxy = connManager.connectToVABElement("urn:fhg:es.iese:vab:1:1:simplevabelement");
		proxy.getValue("/primitives/integer");
	}


	/**
	 * Create a test that verifies the host name with a mocked certificate.
	 * 
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws SSLPeerUnverifiedException
	 */
	@Test
	public void testHTTPSClientValidation() throws KeyManagementException, NoSuchAlgorithmException, SSLPeerUnverifiedException {
		X509Certificate[] certs = { x509Mock };
		X500Principal principleStub = new X500Principal("CN=localhost, OU=JavaSoft, O=Sun Microsystems, C=US");
		Mockito.when(x509Mock.getSubjectX500Principal()).thenReturn(principleStub);
		Mockito.when(sslSessionMock.getPeerCertificates()).thenReturn(certs);

		assertTrue(testVerifier.verify("localhost", sslSessionMock));
	}

	/**
	 * Performs an HTTP request on an URL
	 * 
	 * @param URL
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	private void performRequest(String URL) throws KeyManagementException, NoSuchAlgorithmException {
		Client client = JerseyHttpsClientFactory.getJerseyHTTPSClientWithValidation();
		WebTarget resource = client.target(URL);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		request.get(String.class);
	}


}
