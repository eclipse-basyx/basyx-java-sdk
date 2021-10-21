/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.opcua.server;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.basyx.vab.protocol.opcua.connector.IOpcUaClient;
import org.eclipse.basyx.vab.protocol.opcua.connector.milo.MiloOpcUaClient;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowsePath;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.CallResponse;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.TranslateBrowsePathsToNodeIdsResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated As of version 1.1. Replaced by {@link IOpcUaClient} and {@link MiloOpcUaClient}.
 */
@Deprecated
public class BaSyxOpcUaClientRunner {

    private static Logger logger = LoggerFactory.getLogger(BaSyxOpcUaClientRunner.class);

    static {
        // Required for SecurityPolicy.Aes256_Sha256_RsaPss
        Security.addProvider(new BouncyCastleProvider());
    }

    private final CompletableFuture<OpcUaClient> future = new CompletableFuture<>();

    private OpcUaClient client;

    private String endpointUrl;

    public BaSyxOpcUaClientRunner(String endpointUrl) throws Exception {
        this.endpointUrl = endpointUrl;
    }

    private OpcUaClient createClient() throws Exception {
        Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "security");
        Files.createDirectories(securityTempDir);
        if (!Files.exists(securityTempDir)) {
            throw new Exception("unable to create security dir: " + securityTempDir);
        }
        logger.trace("security temp dir: {}", securityTempDir.toAbsolutePath());

        KeyStoreLoaderClient loader = new KeyStoreLoaderClient().load(securityTempDir);

        SecurityPolicy securityPolicy = SecurityPolicy.None;

        List<EndpointDescription> endpoints;

        try {
            endpoints = DiscoveryClient.getEndpoints(endpointUrl).get();
        } catch (Throwable ex) {
            // try the explicit discovery endpoint as well
            String discoveryUrl = endpointUrl;

            if (!discoveryUrl.endsWith("/")) {
                discoveryUrl += "/";
            }
            discoveryUrl += "discovery";

            logger.trace("Trying explicit discovery URL: {}", discoveryUrl);
            endpoints = DiscoveryClient.getEndpoints(discoveryUrl).get();
        }

        EndpointDescription endpoint = endpoints.stream()
                .filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getUri())).filter(e -> true).findFirst()
                .orElseThrow(() -> new Exception("no desired endpoints returned"));

        logger.trace("Using endpoint: {} [{}/{}]", endpoint.getEndpointUrl(), securityPolicy,
                endpoint.getSecurityMode());

        OpcUaClientConfig config = OpcUaClientConfig.builder()
                .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
                .setApplicationUri("urn:eclipse:milo:examples:client").setCertificate(loader.getClientCertificate())
                .setKeyPair(loader.getClientKeyPair()).setEndpoint(endpoint)
                .setIdentityProvider(new AnonymousProvider()).setRequestTimeout(uint(5000)).build();

        return OpcUaClient.create(config);
    }

    public void run() {
        try {
            client = createClient();

            future.whenCompleteAsync((c, ex) -> {
                if (ex != null) {
                    logger.error("Error running example: {}", ex.getMessage(), ex);
                }

                try {
                    client.disconnect().get();
                    Stack.releaseSharedResources();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error disconnecting:", e.getMessage(), e);
                }

                try {
                    Thread.sleep(1000);
                    throw new RuntimeException("Could not disconnect from server '" + endpointUrl + "'");
                } catch (InterruptedException e) {
                	logger.error("Exception in run", e);
                }
            });
            client.connect().get();
        } catch (Throwable t) {
            logger.error("Error getting client: {}", t.getMessage(), t);

            future.completeExceptionally(t);

            try {
                Thread.sleep(1000);
                throw new RuntimeException("Could not connect to server '" + endpointUrl + "'");
            } catch (InterruptedException e) {
            	logger.error("Exception in run", e);
            }
        }
    }

    public CompletableFuture<List<DataValue>> read(List<NodeId> nodeIds) {
        return client.readValues(0, TimestampsToReturn.Both, nodeIds);
    }

    public CompletableFuture<List<StatusCode>> write(List<NodeId> nodeIds, List<DataValue> values) {
        return client.writeValues(nodeIds, values);
    }

    public CompletableFuture<CallResponse> callMethod(NodeId objectId, NodeId methodId, Variant[] inputArguments) {
        List<CallMethodRequest> cmr = new ArrayList<CallMethodRequest>();
        cmr.add(new CallMethodRequest(objectId, methodId, inputArguments));
        return client.call(cmr);
    }

    public CompletableFuture<TranslateBrowsePathsToNodeIdsResponse> translate(List<BrowsePath> browsePaths) {
        return client.translateBrowsePaths(browsePaths);
    }

}
