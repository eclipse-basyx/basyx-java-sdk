/*******************************************************************************
 * Copyright (C) 2021 Festo Didactic SE
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
package org.eclipse.basyx.vab.protocol.opcua.connector.milo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.protocol.opcua.connector.ClientConfiguration;
import org.eclipse.basyx.vab.protocol.opcua.connector.IOpcUaClient;
import org.eclipse.basyx.vab.protocol.opcua.exception.AmbiguousBrowsePathException;
import org.eclipse.basyx.vab.protocol.opcua.exception.OpcUaException;
import org.eclipse.basyx.vab.protocol.opcua.types.MessageSecurityMode;
import org.eclipse.basyx.vab.protocol.opcua.types.NodeId;
import org.eclipse.basyx.vab.protocol.opcua.types.SecurityPolicy;
import org.eclipse.basyx.vab.protocol.opcua.types.UnsignedByte;
import org.eclipse.basyx.vab.protocol.opcua.types.UnsignedInteger;
import org.eclipse.basyx.vab.protocol.opcua.types.UnsignedLong;
import org.eclipse.basyx.vab.protocol.opcua.types.UnsignedShort;
import org.eclipse.milo.opcua.sdk.client.AddressSpace;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowsePath;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowsePathResult;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowsePathTarget;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.TranslateBrowsePathsToNodeIdsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a wrapper around the eclipse Milo OPC UA client that works in the BaSyx environment.
 */
public class MiloOpcUaClient implements IOpcUaClient {
    private static final Logger logger = LoggerFactory.getLogger(MiloOpcUaClient.class);
    private static DatatypeFactory xmlDatatypeFactory;

    private ClientConfiguration configuration = new ClientConfiguration();
    private OpcUaClientConfigBuilder miloConfiguration;
    private CompletableFuture<UaClient> futureClient;
    private String endpointUrl;

    static {
        try {
            xmlDatatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            logger.error(
                    "Failed to instantiate XML DatatypeFactory. This will lead to NullPointerExceptions, if DateTime values are received from the OPC UA server.",
                    e);
        }
    }

    /**
     * Creates a new OPC UA client for the given endpoint URL with a default configuration.
     *
     * <p>
     * The client will attempt to discover available endpoint at this URL. The one to use will be
     * selected by looking at the security policy and message security mode. These can be configured
     * using {@link #setConfiguration(ClientConfiguration)}.
     *
     * @param endpointUrl The client will attempt to discover available endpoints at this URL. Among all
     *                    of these the one that matches the configured security policy and message
     *                    security mode will be selected.
     *
     * @throws IllegalArgumentException if endpointUrl is <code>null</code>.
     */
    public MiloOpcUaClient(String endpointUrl) {
        if (endpointUrl == null || endpointUrl.isEmpty()) {
            throw new IllegalArgumentException("endpointUrl must not be null.");
        }

        this.endpointUrl = endpointUrl;
    }

    /**
     * Gets the current client configuration.
     *
     * <p>
     * See the documentation of {@link IOpcUaClient#getConfiguration()} for more information.
     *
     * @return A copy of the current configuration.
     */
    @Override
    public synchronized ClientConfiguration getConfiguration() {
        // Return a copy to protect from external changes.
        return configuration.clone();
    }

    /**
     * Sets the client configuration.
     *
     * <p>
     * See the documentation of {@link IOpcUaClient#setConfiguration(ClientConfiguration)} for more
     * information.
     */
    @Override
    public synchronized void setConfiguration(ClientConfiguration configuration) {
        if (hasConnected()) {
            throw new IllegalStateException("Cannot change security configuration after opening the connection.");
        }

        // Create a local copy to protect from external changes.
        this.configuration = (configuration != null) ? configuration.clone() : new ClientConfiguration();
    }

    /**
     * Sets advanced configuration settings not available in {@link ClientConfiguration}.
     *
     * <p>
     * This method allows setting a Milo-specific configuration object which gives access to the full
     * range of options that Milo supports as opposed to the limited selection in
     * {@link ClientConfiguration}.
     *
     * <p>
     * Be aware, that the <i>security policy</i> and <i>message security mode</i> for endpoint selection
     * can only be set using {@link #setConfiguration(ClientConfiguration)}. <br>
     * Additionally, for settings which are available in both configuration objects,
     * {@link ClientConfiguration} will take precedence over {@link OpcUaClientConfigBuilder}.
     *
     * <p>
     * The order in which you call {@link #setConfiguration(ClientConfiguration)} and
     * {@link #setConfiguration(OpcUaClientConfigBuilder)} is not important.
     *
     * <b>Caution:</b> Use this method at your own risk. Your code might break if BaSyx switches to a
     * newer version of Milo or a different OPC UA implementation altogether.
     *
     * @param configuration The Milo client configuration object.
     *
     * @throws IllegalStateException if this method is called after a connection has been established.
     */
    public synchronized void setConfiguration(OpcUaClientConfigBuilder configuration) {
        if (hasConnected()) {
            throw new IllegalStateException("Cannot change security configuration after opening the connection.");
        }

        miloConfiguration = configuration;
    }

    /**
     * Gets the endpoint URL that this client connects to.
     *
     * @return The endpoint URL of the server to connect to.
     */
    @Override
    public String getEndpointUrl() {
        return endpointUrl;
    }

    /**
     * Gets a value signifying whether this client has already attempted to establish a connection to
     * the server endpoint.
     *
     * <p>
     * See the documentation of {@link IOpcUaClient#hasConnected()} for more information.
     */
    @Override
    public boolean hasConnected() {
        synchronized (this) {
            return futureClient != null;
        }
    }

    /**
     * Gets the client object from the underlying OPC UA library.
     *
     * <p>
     * If the client hasn't been created, yet, it will be created automatically, making any subsequent
     * changes to the configuration impossible.
     *
     * <b>Caution:</b> Use this method at your own risk. The underlying client API might change without
     * notice, making any user code relying on this method fragile.
     *
     * @return The underlying client object.
     */
    public synchronized CompletableFuture<UaClient> getClient() {
        if (futureClient != null) {
            return futureClient;
        } else {
            OpcUaClient client = createClient();
            futureClient = client.connect();
            return futureClient;
        }
    }

    /**
     * Creates an instance of the underlying client using the current configuration.
     *
     * @return The created client.
     *
     * @throws OpcUaException as a wrapper exception around any Milo exceptions thrown during client
     *                        creation.
     */
    private OpcUaClient createClient() {
        // Set up a filter which determines the correct endpoint by its security settings.
        SecurityPolicy securityPolicy = configuration.getSecurityPolicy();
        MessageSecurityMode messageSecurityMode = configuration.getMessageSecurityMode();
        Predicate<EndpointDescription> endpointFilter = ep -> {
            boolean securityPolicyMatches = ep.getSecurityPolicyUri()
                    .equals(mapSecurityPolicy(securityPolicy).getUri());
            boolean messageSecurityModeMatches = (ep.getSecurityMode() == mapMessageSecurityMode(messageSecurityMode));
            return securityPolicyMatches && messageSecurityModeMatches;
        };

        EndpointDescription endpoint = discoverEndpoint(endpointUrl, endpointFilter);

        logger.debug("Using endpoint: {} [{}/{}]", endpoint.getEndpointUrl(), securityPolicy,
                messageSecurityMode);

        try {
            OpcUaClientConfig config = buildMiloConfiguration(endpoint);
            return OpcUaClient.create(config);
        } catch (UaException e) {
            throw new OpcUaException(e);
        }
    }

    private OpcUaClientConfig buildMiloConfiguration(EndpointDescription endpoint) {
        OpcUaClientConfigBuilder builder = (miloConfiguration != null) ? miloConfiguration
                : createDefaultMiloConfigBuilder();
        builder.setApplicationName(LocalizedText.english(configuration.getApplicationName()))
                .setApplicationUri(configuration.getApplicationUri())
                .setCertificate(configuration.getCertificate())
                .setKeyPair(configuration.getKeyPair())
                .setEndpoint(endpoint);

        return builder.build();
    }

    private OpcUaClientConfigBuilder createDefaultMiloConfigBuilder() {
        return OpcUaClientConfig.builder()
                .setIdentityProvider(new AnonymousProvider());
    }

    /**
     * Returns the first endpoint matching the filter predicate discovered at the endpointUrl.
     *
     * @param endpointUrl The discovery endpoint to query.
     * @param filter      A predicate that returns 'true' for any endpoint that can be used.
     *
     * @return The first endpoint at the URL which matches the filter.
     *
     * @throws OpcUaException if no endpoint matching the filter can be found at the url or if the
     *                        discovery thread is interrupted.
     */
    private EndpointDescription discoverEndpoint(String endpointUrl, Predicate<EndpointDescription> filter)
            throws OpcUaException {
        try {
            return discoverEndpoints(endpointUrl)
                    .thenApply(list -> list.stream()
                            .filter(filter)
                            .findFirst()
                            .orElseThrow(() -> new OpcUaException("No endpoint found at " + endpointUrl)))
                    .get();
        } catch (ExecutionException e) {
            throw (OpcUaException) e.getCause();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OpcUaException("Endpoint discovery interrupted", e);
        }
    }

    /**
     * Discovers all OPC UA endpoints at the given URL.
     *
     * @param endpointUrl The URL where to discover endpoints.
     *
     * @return A future which will either result in a list of endpoints or completes with an
     *         {@link OpcUaException}.
     */
    private CompletableFuture<List<EndpointDescription>> discoverEndpoints(String endpointUrl) {
        return DiscoveryClient.getEndpoints(endpointUrl)
                .handleAsync((list, ex) -> {
                    if (ex != null) {
                        return retryDiscovery(endpointUrl);
                    } else {
                        return list;
                    }
                });
    }

    private List<EndpointDescription> retryDiscovery(String endpointUrl) {
        String discoveryUrl = createExplicitDiscoveryUrl(endpointUrl);
        logger.debug("Discovery failed at original endpoint URL. Trying with explicit discovery URL: {}", discoveryUrl);

        try {
            return DiscoveryClient.getEndpoints(discoveryUrl).get();
        } catch (InterruptedException e) {
            logger.error("Endpoint discovery failed because thread was interrupted.");
            Thread.currentThread().interrupt();
            throw new OpcUaException(e);
        } catch (ExecutionException e) {
            logger.error("Endpoint discovery failed.");
            throw makeOpcUaExceptionFromCause(e);
        }
    }

    private String createExplicitDiscoveryUrl(String discoveryUrl) {
        discoveryUrl = discoveryUrl.endsWith("/") ? discoveryUrl : discoveryUrl + "/";
        return discoveryUrl + "discovery";
    }

    /** Maps the BaSyx SecurityPolicy enum to the matching Milo enum */
    private org.eclipse.milo.opcua.stack.core.security.SecurityPolicy mapSecurityPolicy(SecurityPolicy securityPolicy) {
        return org.eclipse.milo.opcua.stack.core.security.SecurityPolicy.valueOf(securityPolicy.toString());
    }

    /** Maps the BaSyx MessageSecurityMode enum to the matching Milo enum */
    private org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode mapMessageSecurityMode(
            MessageSecurityMode messageSecurityMode) {
        return org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode.valueOf(messageSecurityMode
                .toString());
    }

    /**
     * Gets the id of the node matching the given browse path when resolved against the root node.
     *
     * <p>
     * See the documentation of {@link IOpcUaClient#translateBrowsePathToNodeId(String)} for more
     * information.
     */
    @Override
    public NodeId translateBrowsePathToNodeId(String browsePath) {
        try {
            return translateBrowsePathToNodeIdAsync(browsePath).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OpcUaException(e);
        } catch (ExecutionException e) {
            throw makeOpcUaExceptionFromCause(e);
        }
    }

    /**
     * Gets the id of the node matching the given browse path when resolved against the root node.
     *
     * <p>
     * See the documentation of {@link IOpcUaClient#translateBrowsePathToNodeIdAsync(String)} for more
     * information.
     */
    @Override
    public CompletableFuture<NodeId> translateBrowsePathToNodeIdAsync(String browsePath) {
        BrowsePath bp = BrowsePathHelper.parse(browsePath);
        return translateBrowsePathToNodeId(bp);
    }

    /**
     * Gets the id of the node pointed to by when resolving the given path against the starting node.
     *
     * <p>
     * See the documentation of {@link IOpcUaClient#translateBrowsePathToNodeId(NodeId, String)} for
     * more information.
     */
    @Override
    public NodeId translateBrowsePathToNodeId(NodeId startingNode, String relativePath) {
        try {
            return translateBrowsePathToNodeIdAsync(startingNode, relativePath).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OpcUaException(e);
        } catch (ExecutionException e) {
            throw makeOpcUaExceptionFromCause(e);
        }
    }

    /**
     * Gets the id of the node pointed to by when resolving the given path against the starting node.
     *
     * <p>
     * See the documentation of {@link IOpcUaClient#translateBrowsePathToNodeId(NodeId, String)} for
     * more information.
     */
    @Override
    public CompletableFuture<NodeId> translateBrowsePathToNodeIdAsync(NodeId startingNode, String relativePath) {
        if (!(startingNode instanceof NodeId)) {
            throw new IllegalArgumentException();
        }

        BrowsePath bp = BrowsePathHelper.parse(startingNode, relativePath);
        return translateBrowsePathToNodeId(bp);
    }

    /**
     * Gets the id of the last two nodes pointed to by when resolving the given path against the
     * starting node.
     *
     * <p>
     * See the documentation of {@link IOpcUaClient#translateBrowsePathToParentAndTargetNodeId(String)}
     * for more information.
     */
    @Override
    public List<NodeId> translateBrowsePathToParentAndTargetNodeId(String browsePath) {
        try {
            return translateBrowsePathToParentAndTargetNodeIdAsync(browsePath).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OpcUaException(e);
        } catch (ExecutionException e) {
            throw makeOpcUaExceptionFromCause(e);
        }
    }

    /**
     * Gets the id of the last two nodes pointed to by when resolving the given path against the
     * starting node.
     *
     * <p>
     * See the documentation of
     * {@link IOpcUaClient#translateBrowsePathToParentAndTargetNodeIdAsync(String)} for more
     * information.
     */
    @Override
    public CompletableFuture<List<NodeId>> translateBrowsePathToParentAndTargetNodeIdAsync(String browsePath) {
        BrowsePath targetPath = BrowsePathHelper.parse(browsePath);
        BrowsePath parentPath = BrowsePathHelper.getParent(targetPath);

        List<BrowsePath> browsePaths = new ArrayList<>(2);
        browsePaths.add(0, targetPath);
        browsePaths.add(1, parentPath);

        return translateBrowsePathsToNodeIds(browsePaths);
    }

    /**
     * Translates a list of browse paths to a list of associated node ids.
     *
     * @param browsePaths A list of browse paths to resolve.
     *
     * @return A future for a list of node ids. Ids are ordered in the same way as their matching browse
     *         paths.
     *
     * @throws ResourceNotFoundException    if any of the browse paths doesn't lead to any node at all.
     * @throws AmbiguousBrowsePathException if any of the browse paths is ambiguous, i.e., it leads to
     *                                      multiple nodes.
     * @throws OpcUaException               if any other OPC UA related error occurs. This is a generic
     *                                      wrapper type for exceptions thrown by the client library.
     */
    private CompletableFuture<List<NodeId>> translateBrowsePathsToNodeIds(List<BrowsePath> browsePaths) {
        // Prepare this 'address space' for later when we need to convert an expanded node id
        // to a regular one. That requires a round-trip with the server.
        CompletableFuture<AddressSpace> futureAddressSpace = getClient()
                .thenApplyAsync(UaClient::getAddressSpace);

        // This function gets the results from the response, unless the service failed in some way.
        Function<TranslateBrowsePathsToNodeIdsResponse, BrowsePathResult[]> getResults = response -> {
            if (!response.getResponseHeader().getServiceResult().isGood()) {
                throw new OpcUaException("TranslateBrowsePaths failed with status code: "
                        + response.getResponseHeader().getServiceResult());
            } else {
                return response.getResults();
            }
        };

        // This function gets the expanded node id matching each browse path, unless at least one
        // of them failed to resolve or was ambiguous.
        Function<BrowsePathResult[], List<ExpandedNodeId>> extractExpandedNodeIds = results -> {
            List<ExpandedNodeId> exNodeIds = new ArrayList<>(results.length);
            for (int i = 0; i < results.length; i++) {
                BrowsePathResult r = results[i];

                if (!r.getStatusCode().isGood()) {
                    String exceptionMessage = String.format("Browse path [%s] failed to resolve with status code: %s",
                            browsePaths.get(i), r.getStatusCode());
                    throw new ResourceNotFoundException(exceptionMessage);
                } else {
                    BrowsePathTarget[] targets = r.getTargets();
                    if (targets.length > 1) {
                        String exceptionMessage = String.format("Browse path [%s] leads to multiple targets.",
                                browsePaths.get(i));
                        throw new AmbiguousBrowsePathException(exceptionMessage);
                    }
                    exNodeIds.add(i, targets[0].getTargetId());
                }
            }
            return exNodeIds;
        };

        // This function maps the expanded node ids to regular node ids, using a provided
        // address space object.
        BiFunction<List<ExpandedNodeId>, AddressSpace, List<NodeId>> mapToNodeIds = (expandedNodeIds,
                addressSpace) -> expandedNodeIds.stream()
                        .map(addressSpace::toNodeId)
                        .map(NodeId::new)
                        .collect(Collectors.toList());

        // This function logs the result and returns it without any changes.
        UnaryOperator<List<NodeId>> log = nodeIds -> {
            List<String> bpStrings = browsePaths.stream()
                    .map(bp -> BrowsePathHelper.toString(bp.getRelativePath()))
                    .collect(Collectors.toList());
            logger.debug("Translated browse paths {} to node ids {}", bpStrings, nodeIds);

            return nodeIds;
        };

        // Prepare the future which returns the node ids.
        CompletableFuture<List<NodeId>> future = getClient()
                .thenCompose(client -> client.translateBrowsePaths(browsePaths))
                .thenApply(getResults)
                .thenApply(extractExpandedNodeIds)
                .thenCombine(futureAddressSpace, mapToNodeIds);

        // Add the logger stage only if required because it is computationally rather expensive.
        return logger.isDebugEnabled() ? future.thenApply(log) : future;
    }

    private CompletableFuture<NodeId> translateBrowsePathToNodeId(BrowsePath browsePath) {
        List<BrowsePath> browsePaths = Collections.singletonList(browsePath);

        return translateBrowsePathsToNodeIds(browsePaths)
                .thenApply(nodeIds -> nodeIds.get(0));
    }

    /**
     * Reads the current value from an OPC UA node.
     *
     * <p>
     * See the documentation of {@link IOpcUaClient#readValue(NodeId)} for more information.
     */
    @Override
    public Object readValue(NodeId nodeId) throws OpcUaException {
        try {
            return readValueAsync(nodeId).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OpcUaException(e);
        } catch (ExecutionException e) {
            throw makeOpcUaExceptionFromCause(e);
        }
    }

    /**
     * Reads the current value from an OPC UA node.
     *
     * <p>
     * See the documentation of {@link IOpcUaClient#readValueAsync(NodeId)} for more information.
     */
    @Override
    public CompletableFuture<Object> readValueAsync(NodeId nodeId) {
        if (nodeId == null) {
            throw new IllegalArgumentException("nodeId must not be null.");
        }

        logger.debug("Reading node '{}'.", nodeId);

        return getClient()
                .thenCompose(client -> client.readValue(0, TimestampsToReturn.Neither, nodeId.getInternalId()))
                .thenApply(dv -> {
                    if (dv.getStatusCode().isGood()) {
                        return dv.getValue();
                    } else {
                        throw new OpcUaException("Read failed with: " + dv.getStatusCode());
                    }
                })
                .thenApply(this::unwrapVariant)
                .exceptionally(e -> {
                	if (e instanceof CompletionException) {
                		throw makeOpcUaExceptionFromCause(e);
                	} else {
                		throw ensureOpcUaException(e);
                	}
                });
    }

    /**
     * Writes the value of an OPC UA node.
     *
     * <p>
     * See the documentation of {@link IOpcUaClient#writeValue(NodeId, Object)} for more information.
     */
    @Override
    public void writeValue(NodeId nodeId, Object value) throws OpcUaException {
        try {
            writeValueAsync(nodeId, value).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OpcUaException(e);
        } catch (ExecutionException e) {
            throw makeOpcUaExceptionFromCause(e);
        }
    }

    /**
     * Writes the value of an OPC UA node.
     *
     * <p>
     * See the documentation of {@link IOpcUaClient#writeValueAsync(NodeId, Object)} for more
     * information.
     */
    @Override
    public CompletableFuture<Void> writeValueAsync(NodeId nodeId, Object value) {
        if (nodeId == null) {
            throw new IllegalArgumentException("nodeId must not be null.");
        }

        logger.debug("Writing node '{}' with value {}.", nodeId, value);

        DataValue dv = new DataValue(wrapVariant(value));

        return getClient()
                .thenCompose(client -> client.writeValue(nodeId.getInternalId(), dv))
                .thenAccept(status -> {
                    if (!status.isGood()) {
                        throw new OpcUaException("Write failed with: " + status);
                    }
                }).exceptionally(e -> {
                	if (e instanceof CompletionException) {
                		throw makeOpcUaExceptionFromCause(e);
                	} else {
                		throw ensureOpcUaException(e);
                	}
                });
    }

    /**
     * Invokes an OPC UA method on an object.
     *
     * <p>
     * See the documentation of {@link IOpcUaClient#invokeMethod(NodeId, NodeId, Object...)} for more
     * information.
     */
    @Override
    public List<Object> invokeMethod(NodeId ownerId, NodeId methodId, Object... parameters) throws OpcUaException {
        try {
            return invokeMethodAsync(ownerId, methodId, parameters).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OpcUaException(e);
        } catch (ExecutionException e) {
            throw makeOpcUaExceptionFromCause(e);
        }
    }

    /**
     * Invokes an OPC UA method on an object.
     *
     * <p>
     * See the documentation of {@link IOpcUaClient#invokeMethodAsync(NodeId, NodeId, Object...)} for
     * more information.
     */
    @Override
    public CompletableFuture<List<Object>> invokeMethodAsync(NodeId ownerId, NodeId methodId, Object... parameters) {
        if (ownerId == null || methodId == null) {
            throw new IllegalArgumentException("ownerId and methodId must not be null.");
        }

        logger.debug("Invoking method '{}' on node '{}' with arguments {}.", methodId, ownerId, parameters);

        Variant[] inputs = new Variant[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            inputs[i] = wrapVariant(parameters[i]);
        }

        CallMethodRequest req = new CallMethodRequest(ownerId.getInternalId(), methodId.getInternalId(), inputs);

        return getClient()
                .thenCompose(client -> client.call(req))
                .thenApply(result -> {
                    if (!result.getStatusCode().isGood()) {
                        throw new OpcUaException("Method invocation failed with: "
                                + result.getStatusCode());
                    }
                    return Arrays.stream(result.getOutputArguments())
                            .map(this::unwrapVariant)
                            .collect(Collectors.toList());
                }).exceptionally(e -> {
                	if (e instanceof CompletionException) {
                		throw makeOpcUaExceptionFromCause(e);
                	} else {
                		throw ensureOpcUaException(e);
                	}
                });
    }

    /**
     * Wraps a data value in a {@link Variant}.
     *
     * <p>
     * While wrapping, value types for which equivalent types exist in Milo's type system are
     * automatically converted.
     *
     * @param value The value to wrap in a <code>Variant</code>.
     *
     * @return A new <code>Variant</code> wrapping <code>value</code>.
     */
    private Variant wrapVariant(Object value) {
        return new Variant(mapBaSyxToMiloTypes(value));
    }

    /**
     * Unwraps a data value from a {@link Variant}.
     *
     * <p>
     * While unwrapping, data values with Milo-specific types will be converted to their respective
     * BaSyx equivalents.
     *
     * @param variant The <code>Variant</code> to unwrap.
     *
     * @return The raw data value contained in <code>variant</code>.
     */
    private Object unwrapVariant(Variant variant) {
        if (variant == null || variant.getValue() == null) {
            return null;
        }

        ExpandedNodeId typeId = variant.getDataType().orElse(null);
        if (typeId == null) {
            return null;
        }

        return mapMiloToBaSyxTypes(variant.getValue());
    }

    /**
     * Converts from OPC UA data types implemented in Milo to types used in BaSyx.
     *
     * @param value The data value coming from Milo.
     *
     * @return The corresponding BaSyx object.
     */
    private Object mapMiloToBaSyxTypes(Object value) {
        if (value instanceof DateTime) {
            long millis = ((DateTime) value).getJavaTime();
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(millis);
            return xmlDatatypeFactory.newXMLGregorianCalendar(cal);
        } else if (value instanceof UByte) {
            return new UnsignedByte((UByte) value);
        } else if (value instanceof UShort) {
            return new UnsignedShort((UShort) value);
        } else if (value instanceof UInteger) {
            return new UnsignedInteger((UInteger) value);
        } else if (value instanceof ULong) {
            return new UnsignedLong((ULong) value);
        } else {
            return value;
        }
    }

    /**
     * Converts from BaSyx types to OPC UA data types implemented in Milo.
     *
     * @param value The value coming from BaSyx.
     *
     * @return The corresponding Milo object.
     */
    private Object mapBaSyxToMiloTypes(Object value) {
        if (value instanceof XMLGregorianCalendar) {
            XMLGregorianCalendar v = (XMLGregorianCalendar) value;
            if (v.getXMLSchemaType() != DatatypeConstants.DATETIME) {
                throw new OpcUaException(
                        "The OPC UA DateTime type doesn't support incomplete date/time specifications. Illegal value: "
                                + v);
            }
            Instant i = Instant.ofEpochMilli(v.toGregorianCalendar().getTimeInMillis());
            return new DateTime(i);
        } else if (value instanceof UnsignedByte) {
            return ((UnsignedByte) value).getInternalValue();
        } else if (value instanceof UnsignedShort) {
            return ((UnsignedShort) value).getInternalValue();
        } else if (value instanceof UnsignedInteger) {
            return ((UnsignedInteger) value).getInternalValue();
        } else if (value instanceof UnsignedLong) {
            return ((UnsignedLong) value).getInternalValue();
        } else {
            return value;
        }
    }

    private OpcUaException makeOpcUaExceptionFromCause(Throwable e) {
        return ensureOpcUaException(e.getCause());
    }

    private OpcUaException ensureOpcUaException(Throwable e) {
        return (e instanceof OpcUaException) ? (OpcUaException) e : new OpcUaException(e);
    }
}
