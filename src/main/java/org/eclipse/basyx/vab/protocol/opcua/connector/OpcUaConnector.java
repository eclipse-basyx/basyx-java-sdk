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
package org.eclipse.basyx.vab.protocol.opcua.connector;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.opcua.exception.OpcUaException;
import org.eclipse.basyx.vab.protocol.opcua.types.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The OpcUaConnector can be used to connect to remote models over OPC UA.
 *
 * <p>
 * When this class is instantiated with an endpoint URL, an {@link IOpcUaClient} is automatically
 * created with a default configuration. The default is an insecure, anonymous connection without a
 * client certificate.
 *
 * <p>
 * The configuration can be changed as long as no connection has been established. A connection is
 * established automatically the first time a node is read or written or an operation is invoked,
 * either through this connector or directly through the associated {@link IOpcUaClient}. <br>
 * To change the configuration, do the following:
 *
 * <pre>
 * ClientConfiguration config = opcUaConnector.getClient().getConfiguration();
 * // Modify configuration
 * opcUaConnector.getClient().setConfiguration(config);
 * </pre>
 *
 * <h2>Using browse paths</h2>
 *
 * This class uses browses paths to identify OPC UA nodes for read, write and invoke requests. That
 * has two noteworthy caveats:
 * <ol>
 * <li>Browse paths aren't necessarily unambiguous. In other words, a browse path might match more
 * than one node. This would lead to an exception being returned from this connector. <br>
 * It is up to the user to make sure their server's address space doesn't have that problem.
 * <li>The connector must translate the browse path to a unique node id in the background. That
 * necessitates an additional network request to the server, every time a read, write or invoke
 * request is made. <br>
 * To address this issue, this class contains a cache for translated browse paths.
 * </ol>
 *
 * <h3>Node id cache</h3>
 *
 * Using the cache is entirely optional and it is disabled be default. It can be enabled using
 * {@link #setNodeIdCacheDuration(Duration)}.
 *
 * <p>
 * If it is enabled, it will cache any browse path passed to {@link #getValue(String)},
 * {@link #setValue(String, Object)} or {@link #invokeOperation(String, Object...)} and the matching
 * node id for the specified duration. Subsequent requests using the same browse path within the
 * configured timespan would omit the additional network request for browse path resolution.
 *
 * <p>
 * <b>Caution:</b> An OPC UA server can dynamically reconfigure their address space during runtime.
 * This could even be done remotely from clients, if the server allows it. <br>
 * Such changes would render this cache invalid, but there is no way for this connector to get
 * notified of them. Only use the cache with servers where you can be sure the address space doesn't
 * change.
 */
public class OpcUaConnector implements IModelProvider {
    /**
     * {@link TimerTask} which removes an entry from a map.
     */
    private static class RemoveEntryFromMapTimerTask<TKey, TValue> extends TimerTask {
        Map<TKey, TValue> map;
        TKey key;

        public RemoveEntryFromMapTimerTask(Map<TKey, TValue> map, TKey key) {
            this.map = map;
            this.key = key;
        }

        @Override
        public void run() {
            map.remove(key);
        }
    }

    private static final Timer cacheTimer = new Timer(true);
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Duration cacheDuration = Duration.ZERO;
    private IOpcUaClient client;
    private Map<String, NodeId> nodeIdCache = new ConcurrentHashMap<>();
    private Map<String, List<NodeId>> operationNodeIdsCache = new ConcurrentHashMap<>();

    public OpcUaConnector(String endpointUrl) {
        client = IOpcUaClient.create(endpointUrl);
    }

    /**
     * Gets the OPC UA client used for communication to the server.
     *
     * @return The OPC UA client object.
     */
    public IOpcUaClient getClient() {
        return client;
    }

    /**
     * Sets the duration for the NodeId cache.
     *
     * <p>
     * When a {@link OpcUaConnector} resolves a browse path to a NodeId, it can cache the result to make
     * future access to that same browse path more efficient. <br>
     * This setting controls how long looked up NodeIds remain cached. A value of {@link Duration#ZERO}
     * disables the cache.
     *
     * <p>
     * See {@link OpcUaConnector} for more information on the caching feature.
     *
     * @param cacheDuration The cache duration. {@link Duration#ZERO} disable the cache.
     *
     * @throws IllegalArgumentException if cacheDuration is <code>null</code> or negative.
     */
    public void setNodeIdCacheDuration(Duration cacheDuration) {
        if (cacheDuration == null || cacheDuration.isNegative()) {
            throw new IllegalArgumentException("cacheDuration must not be negative.");
        }

        this.cacheDuration = cacheDuration;
    }

    @Override
    public Object getValue(String path) throws OpcUaException {
        try {
            NodeId nodeId = getNodeIdForBrowsePath(path);
            return client.readValue(nodeId);
        } catch (OpcUaException e) {
            logger.error("Failed to get node value.");
            throw e;
        }
    }

    @Override
    public void setValue(String path, Object newValue) throws OpcUaException {
        try {
            NodeId nodeId = getNodeIdForBrowsePath(path);
            client.writeValue(nodeId, newValue);
        } catch (OpcUaException e) {
            logger.error("Failed to set node value.");
            throw e;
        }
    }

    @Override
    public void createValue(String path, Object newEntity) throws ProviderException {
        throw new UnsupportedOperationException("Cannot create values through OPC UA.");
    }

    @Override
    public void deleteValue(String path) throws ProviderException {
        throw new UnsupportedOperationException("Cannot delete values through OPC UA.");
    }

    @Override
    public void deleteValue(String path, Object obj) throws ProviderException {
        throw new UnsupportedOperationException("Cannot delete values through OPC UA.");
    }

    @Override
    public Object invokeOperation(String path, Object... parameters) throws OpcUaException {
        try {
            List<NodeId> nodeIds = getNodeIdsForOperationBrowsePath(path);
            return client.invokeMethod(nodeIds.get(1), nodeIds.get(0), parameters);
        } catch (OpcUaException e) {
            logger.error("Failed to invoke operation.");
            throw e;
        }
    }

    private NodeId getNodeIdForBrowsePath(String browsePath) {
        if (nodeIdCache.containsKey(browsePath)) {
            logger.debug("Using cached NodeId for browse path '{}'.", browsePath);
            return nodeIdCache.get(browsePath);
        }

        NodeId nodeId = client.translateBrowsePathToNodeId(browsePath);

        if (!cacheDuration.isZero()) {
            nodeIdCache.put(browsePath, nodeId);
            cacheTimer.schedule(
                    new RemoveEntryFromMapTimerTask<>(nodeIdCache, browsePath),
                    cacheDuration.toMillis());
        }
        return nodeId;
    }

    private List<NodeId> getNodeIdsForOperationBrowsePath(String browsePath) {
        if (operationNodeIdsCache.containsKey(browsePath)) {
            logger.debug("Using cached NodeIds for operation at browse path '{}'.", browsePath);
            return operationNodeIdsCache.get(browsePath);
        }

        List<NodeId> nodeIds = client.translateBrowsePathToParentAndTargetNodeId(browsePath);

        if (!cacheDuration.isZero()) {
            operationNodeIdsCache.put(browsePath, nodeIds);
            cacheTimer.schedule(
                    new RemoveEntryFromMapTimerTask<>(operationNodeIdsCache, browsePath),
                    cacheDuration.toMillis());
        }
        return nodeIds;
    }
}
