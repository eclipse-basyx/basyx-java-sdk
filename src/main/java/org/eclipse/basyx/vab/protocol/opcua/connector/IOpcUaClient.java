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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.protocol.opcua.connector.milo.MiloOpcUaClient;
import org.eclipse.basyx.vab.protocol.opcua.exception.AmbiguousBrowsePathException;
import org.eclipse.basyx.vab.protocol.opcua.exception.OpcUaException;
import org.eclipse.basyx.vab.protocol.opcua.types.NodeId;
import org.eclipse.basyx.vab.protocol.opcua.types.UnsignedByte;
import org.eclipse.basyx.vab.protocol.opcua.types.UnsignedInteger;
import org.eclipse.basyx.vab.protocol.opcua.types.UnsignedLong;
import org.eclipse.basyx.vab.protocol.opcua.types.UnsignedShort;

/**
 * Very simplified OPC UA client interface for reading and writing node values
 * and invoking methods.
 *
 * <h2>How to use</h2>
 *
 * This interface features a set of methods to call common OPC UA services on a
 * server (e.g. to read a node). Each comes in a synchronous and an asynchronous
 * variant, returning {@link CompletableFuture}s. When any of these methods is
 * first called, a connection to the configured endpoint URL is automatically
 * opened.
 * <p>
 * Users can configure the client through
 * {@link #setConfiguration(ClientConfiguration)} until the first connection
 * attempt is made. After that point, that method will throw an exception. <br>
 * {@link #hasConnected()} lets users check whether such a connection attempt
 * has already been made.
 *
 * <h2>Regarding types</h2>
 *
 * This interface must necessarily translate between two distinct type systems.
 * There is the {@link ValueType BaSyx type system} on the one hand, itself a
 * mapping of XML Schema types to standard JDK types with some additional custom
 * classes. And on the other hand, there is
 * <a href="https://reference.opcfoundation.org/v104/Core/docs/Part5/12.2">OPC
 * UA's type system</a>. The two aren't fully compatible, meaning that there
 * isn't a simple one-to-one mapping of types.
 *
 * <p>
 * This implementation makes no attempt at complete coverage of all OPC UA
 * types. Only a subset of the most common data types are supported. Below table
 * shows which Java types can be used and what OPC UA type they map to. Callers
 * can pass these types to any of the interface methods and expect these types
 * to be returned.
 *
 * <p>
 * Please note that if the OPC UA server returns a value of a different type
 * than those listed in the table, e.g. as the result of a read operation or
 * method invocation, then that type will be returned from the API as is. It
 * would likely be a class from the underlying OPC UA library and the caller
 * would have to know how to handle that type.
 *
 * <table>
 * <caption>Java / OPC UA type mapping</caption>
 * <tr>
 * <th>Java type</th>
 * <th>OPC UA type</th>
 * </tr>
 * <tbody>
 * <tr>
 * <td><code>boolean</code> / {@link Boolean}</td>
 * <td>Boolean</td>
 * <tr>
 * <td><code>byte</code> / {@link Byte}</td>
 * <td>SByte</td>
 * <tr>
 * <td><code>short</code> / {@link Short}</td>
 * <td>Int16</td>
 * <tr>
 * <td><code>int</code> / {@link Integer}</td>
 * <td>Int32</td>
 * <tr>
 * <td><code>long</code> / {@link Long}</td>
 * <td>Int64</td>
 * <tr>
 * <td><code>float</code> / {@link Float}</td>
 * <td>Float</td>
 * <tr>
 * <td><code>double</code> / {@link Double}</td>
 * <td>Double</td>
 * <tr>
 * <td>{@link UnsignedByte}</td>
 * <td>Byte</td>
 * </tr>
 * <tr>
 * <td>{@link UnsignedShort}</td>
 * <td>UInt16</td>
 * </tr>
 * <tr>
 * <td>{@link UnsignedInteger}</td>
 * <td>UInt32</td>
 * </tr>
 * <tr>
 * <td>{@link UnsignedLong}</td>
 * <td>UInt64</td>
 * </tr>
 * <tr>
 * <td>{@link XMLGregorianCalendar}</td>
 * <td>DateTime</td>
 * </tr>
 * <tr>
 * <td>{@link String}</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>{@link UUID}</td>
 * <td>GUID</td>
 * </tr>
 * <tr>
 * <td>Array of the aforementioned types<br>
 * (Single- or multi-dimensional)</td>
 * <td>Array of equivalent OPC UA type</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * <h3>Date &amp; time types</h3>
 *
 * Special care must be taken when dealing with the {@link XMLGregorianCalendar}
 * type. It allows for incomplete date or time specifications, such as <i>March
 * 13</i>, without providing a year. Or <i>5 minutes</i> without any information
 * on the hour or seconds. Such date/time specifications are not supported by
 * the OPC UA DateTime type. It functions like the {@link java.time.Instant}
 * type and must always specify a precise moment in time.
 */
public interface IOpcUaClient {
	/**
	 * Static factory method for creating a client using the default implementation
	 * based on eclipse Milo.
	 * 
	 * @param endpointUrl
	 *            The server endpoint to which the client connects.
	 *
	 * @return The new {@link MiloOpcUaClient}.
	 */
	static IOpcUaClient create(String endpointUrl) {
		return new MiloOpcUaClient(endpointUrl);
	}

	/**
	 * Gets the current client configuration.
	 * 
	 * <p>
	 * A copy of the configuration is made and returned. Subsequent changes to the
	 * returned object won't be reflected on this {@link IOpcUaClient} until the
	 * changed configuration is applied with
	 * {@link #setConfiguration(ClientConfiguration)}.
	 * 
	 * @return A copy of the current client configuration.
	 */
	ClientConfiguration getConfiguration();

	/**
	 * Sets the client configuration.
	 * 
	 * <p>
	 * A copy of the configuration is made and stored. Modifications done to the
	 * original object after invoking this method have no effect on this
	 * {@link IOpcUaClient}.
	 * 
	 * @param configuration
	 *            The configuration to set or <code>null</code> to use a default
	 *            configuration.
	 * 
	 * @throws IllegalStateException
	 *             if this method is called after a connection has been established.
	 */
	void setConfiguration(ClientConfiguration configuration);

	/**
	 * Gets the endpoint URL that this client connects to.
	 * 
	 * @return The endpoint URL to connect to.
	 */
	String getEndpointUrl();

	/**
	 * Gets a value signifying whether this client has already attempted to
	 * establish a connection to the server endpoint.
	 * 
	 * <p>
	 * Note that the returned value does not say whether there is currently an
	 * active connection to the server. Only whether a connection attempt has been
	 * made. This is significant because changes to client configuration are no
	 * longer possible once this method returns <code>true</code>.
	 * 
	 * @return <code>true</code> if a connection attempt has been made,
	 *         <code>false</code> otherwise.
	 */
	boolean hasConnected();

	/**
	 * Gets the id of the node pointed to by when resolving the given path against
	 * the starting node.
	 * 
	 * <p>
	 * Browse paths in OPC UA are comprised of a starting node (identified by it's
	 * node id) and a relative path to the target. This relative path is a sequence
	 * of pairs of a reference type and a browse name. <br>
	 * The relative path must be formatted according to the
	 * <a href="https://reference.opcfoundation.org/v104/Core/docs/Part4/A.2/">BNF
	 * format of relative paths</a>.
	 * 
	 * <p>
	 * The relative path is resolved against the starting node by following a
	 * reference of the type specified in the first pair from the starting node to a
	 * target node with the browse name from that same pair. From the target node
	 * thus reached the second pair is resolved and so on until the end of the path.
	 * The node id of the final node reached this way is returned.
	 * 
	 * <p>
	 * Unfortunately, browse paths can be ambiguous in that they can lead to more
	 * than one final target. That's because browse names of nodes are not required
	 * to be unique, even within the same namespace. So from any given node, several
	 * references of the same type can lead to several other nodes with the same
	 * browse name.
	 * 
	 * <p>
	 * This is a blocking call which returns only after the request to the server
	 * has been completed. For a non-blocking variant, see
	 * {@link #translateBrowsePathToNodeIdAsync(NodeId, String)}.
	 * 
	 * @param startingNode
	 *            The starting node from where to start resolving
	 *            <code>relativePath</code>.
	 * @param relativePath
	 *            The string representation of the relative path to resolve against
	 *            <code>startingNode</code>.
	 *
	 * @return The id of the node matching the browse path.
	 * 
	 * @throws ResourceNotFoundException
	 *             if the path doesn't lead to a node.
	 * @throws AmbiguousBrowsePathException
	 *             if the path cannot be unambiguously resolved.
	 * @throws OpcUaException
	 *             if an OPC UA related error occurs. This is a generic wrapper type
	 *             for exceptions thrown by the client library.
	 * @throws IllegalArgumentException
	 *             if <code>startingNode</code> or <code>relativePath</code> is
	 *             <code>null</code> or if <code>relativePath</code> is an empty
	 *             string.
	 */
	NodeId translateBrowsePathToNodeId(NodeId startingNode, String relativePath);

	/**
	 * Gets the id of the node matching the given browse path when resolved against
	 * the root node.
	 * 
	 * <p>
	 * See {@link #translateBrowsePathToNodeId(NodeId, String)} for details on how
	 * browse paths are resolved.
	 * 
	 * <p>
	 * This overload doesn't take an explicit starting node. It implicitly starts at
	 * the root node. Use {@link #translateBrowsePathToNodeId(NodeId, String)} to
	 * specify a different starting node.
	 * 
	 * <p>
	 * This is a blocking call which returns only after the request to the server
	 * has been completed. For a non-blocking variant, see
	 * {@link #translateBrowsePathToNodeIdAsync(String)}.
	 * 
	 * @param browsePath
	 *            The string representation of the browse path.
	 *
	 * @return The id of the node matching the browse path.
	 * 
	 * @throws ResourceNotFoundException
	 *             if the path doesn't lead to a node.
	 * @throws AmbiguousBrowsePathException
	 *             if the path cannot be unambiguously resolved.
	 * @throws OpcUaException
	 *             if an OPC UA related error occurs. This is a generic wrapper type
	 *             for exceptions thrown by the client library.
	 * @throws IllegalArgumentException
	 *             if <code>browsePath</code> is <code>null</code> or an empty
	 *             string.
	 */
	NodeId translateBrowsePathToNodeId(String browsePath);

	/**
	 * Gets the id of the node pointed to by when resolving the given path against
	 * the starting node.
	 * 
	 * <p>
	 * See {@link #translateBrowsePathToNodeId(NodeId, String)} for details on how
	 * browse paths are resolved.
	 * 
	 * <p>
	 * This is an asynchronous call returning a {@link CompletableFuture}. For more
	 * details about the parameter, the value returned by the future and possible
	 * exceptions, see {@link #translateBrowsePathToNodeId(NodeId, String)}.
	 * 
	 * @param startingNode
	 *            The starting node from where to start resolving
	 *            <code>relativePath</code>.
	 * @param relativePath
	 *            The string representation of the relative path to resolve against
	 *            <code>startingNode</code>.
	 *
	 * @return A {@link CompletableFuture} for the target node's id.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>startingNode</code> or <code>relativePath</code> is
	 *             <code>null</code> or if <code>relativePath</code> is an empty
	 *             string.
	 */
	CompletableFuture<NodeId> translateBrowsePathToNodeIdAsync(NodeId startingNode, String relativePath);

	/**
	 * Gets the id of the node matching the given browse path when resolved against
	 * the root node.
	 * 
	 * <p>
	 * See {@link #translateBrowsePathToNodeId(NodeId, String)} for details on how
	 * browse paths are resolved.
	 * 
	 * <p>
	 * This overload doesn't take an explicit starting node. It implicitly starts at
	 * the root node. Use {@link #translateBrowsePathToNodeIdAsync(NodeId, String)}
	 * to specify a different starting node.
	 * 
	 * <p>
	 * This is an asynchronous call returning a {@link CompletableFuture}. For more
	 * details about the parameter, the value returned by the future and possible
	 * exceptions, see {@link #translateBrowsePathToNodeId(String)}.
	 * 
	 * @param browsePath
	 *            The string representation of the browse path.
	 *
	 * @return A {@link CompletableFuture} for the target node's id.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>browsePath</code> is <code>null</code> or an empty
	 *             string.
	 */
	CompletableFuture<NodeId> translateBrowsePathToNodeIdAsync(String browsePath);

	/**
	 * Gets the id of the last two nodes pointed to by when resolving the given path
	 * against the starting node.
	 * 
	 * <p>
	 * This is not a typical use case for OPC UA applications but it is a useful
	 * helper for invoking BaSyx operations through OPC UA. That's because BaSyx
	 * identifies all elements in its information model only through a single path,
	 * represented as a string. These strings map directly to OPC UA browse paths in
	 * the case of {@link OpcUaConnector}).
	 * 
	 * <p>
	 * For BaSyx properties (i.e., variable nodes in OPC UA) that's all well and
	 * good. But for BaSyx operations (which correspond to OPC UA methods) it poses
	 * a problem. <br>
	 * OPC UA methods must be invoked using their own node id as well as that of the
	 * owner object on which to invoke the method. See
	 * {@link #invokeMethod(NodeId, NodeId, Object...)} for more information. <br>
	 * That's two independent identifiers necessary, while BaSyx only provides the
	 * one string.
	 * 
	 * <p>
	 * BaSyx solves this conundrum by assuming that the final node targeted by a
	 * browse path is the method node itself, while the second to last node in the
	 * path is the owner. <br>
	 * For any BaSyx model directly mapped to OPC UA this assumption is guaranteed
	 * to hold true. The same can not be said for general purpose OPC UA servers.
	 * This should be kept in mind when utilizing this method with a server not
	 * provided by a BaSyx application.
	 * 
	 * <p>
	 * For general information on how browse paths are resolved, please refer to
	 * {@link #translateBrowsePathToNodeId(NodeId, String)}.
	 * 
	 * <p>
	 * Note that this method has no overload accepting an explicit starting node.
	 * That's because it's use case is specific to the {@link OpcUaConnector} which
	 * has no way of ever specifying an starting node.
	 * 
	 * <p>
	 * This is a blocking call which returns only after the request to the server
	 * has been completed. For a non-blocking variant, see
	 * {@link #translateBrowsePathToParentAndTargetNodeIdAsync(String)}.
	 * 
	 * @param browsePath
	 *            The string representation of the browse path.
	 *
	 * @return A list of two node ids, where the first is the method node and the
	 *         second is its parent.
	 * 
	 * @throws ResourceNotFoundException
	 *             if the path doesn't lead to a node.
	 * @throws AmbiguousBrowsePathException
	 *             if the path cannot be unambiguously resolved.
	 * @throws OpcUaException
	 *             if an OPC UA related error occurs. This is a generic wrapper type
	 *             for exceptions thrown by the client library.
	 * @throws IllegalArgumentException
	 *             if <code>browsePath</code> is <code>null</code> or an empty
	 *             string.
	 */
	List<NodeId> translateBrowsePathToParentAndTargetNodeId(String browsePath);

	/**
	 * Gets the id of the last two nodes pointed to by when resolving the given path
	 * against the starting node.
	 * 
	 * <p>
	 * This is an asynchronous call returning a {@link CompletableFuture}. For more
	 * details on this method's purpose, returned value and possible exceptions, see
	 * {@link #translateBrowsePathToParentAndTargetNodeId(String)}.
	 * 
	 * @param browsePath
	 *            The string representation of the browse path.
	 *
	 * @return A {@link CompletableFuture} for a list of parent and method node id.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>browsePath</code> is <code>null</code> or an empty
	 *             string.
	 */
	CompletableFuture<List<NodeId>> translateBrowsePathToParentAndTargetNodeIdAsync(String browsePath);

	/**
	 * Invokes an OPC UA method on an object.
	 * 
	 * <p>
	 * OPC UA methods must always be invoked on an owner object which acts as the
	 * method's scope. That's because the same method (with the same node id) can be
	 * assigned to multiple objects or even object types. <br>
	 * The owner must have a <code>HasComponent</code> reference to the method. For
	 * more details refer to the
	 * <a href="https://reference.opcfoundation.org/v104/Core/docs/Part4/5.11.2/">
	 * specification (part 4)</a>.
	 * 
	 * <p>
	 * The type of the input parameters and returned values depends on the method
	 * configuration on the server and the caller is expected to know these types.
	 * {@link IOpcUaClient} gives more information on types.
	 * 
	 * <p>
	 * This is a blocking call which returns only after the request to the server
	 * has been completed. For a non-blocking variant, see
	 * {@link #invokeMethodAsync(NodeId, NodeId, Object...)}.
	 * 
	 * @param ownerId
	 *            The node id of the object (or object type) on which to invoke the
	 *            method.
	 * @param methodId
	 *            The node id of the method to invoke.
	 * @param parameters
	 *            The input parameters of the operation.
	 *
	 * @return The outputs from the operation.
	 * 
	 * @throws OpcUaException
	 *             if an OPC UA related error occurs. This is a generic wrapper type
	 *             for exceptions thrown by the client library.
	 * @throws IllegalArgumentException
	 *             if <code>ownerId</code> or <code>methodId</code> is
	 *             <code>null</code>.
	 */
	List<Object> invokeMethod(NodeId ownerId, NodeId methodId, Object... parameters) throws OpcUaException;

	/**
	 * Invokes an OPC UA method on an object.
	 * 
	 * <p>
	 * OPC UA methods must always be invoked on an owner object which acts as the
	 * method's scope. That's because the same method (with the same node id) can be
	 * assigned to multiple objects or even object types. <br>
	 * The owner must have a <code>HasComponent</code> reference to the method. For
	 * more details refer to the
	 * <a href="https://reference.opcfoundation.org/v104/Core/docs/Part4/5.11.2/">
	 * specification (part 4)</a>.
	 * 
	 * <p>
	 * This is an asynchronous call returning a {@link CompletableFuture} for the
	 * value returned by the method. For more details about possible exceptions, see
	 * {@link #invokeMethod(NodeId, NodeId, Object...)}.
	 * 
	 * @param ownerId
	 *            The node id of the object (or object type) on which to invoke the
	 *            method.
	 * @param methodId
	 *            The node id of the method to invoke.
	 * @param parameters
	 *            The input parameters of the operation.
	 *
	 * @return A {@link CompletableFuture} for the method's outputs.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>ownerId</code> or <code>methodId</code> is
	 *             <code>null</code>.
	 */
	CompletableFuture<List<Object>> invokeMethodAsync(NodeId ownerId, NodeId methodId, Object... parameters);

	/**
	 * Reads the current value from an OPC UA node.
	 * 
	 * <p>
	 * The returned type depends on the node's data type on the server and the
	 * caller is expected to know which type to expect. {@link IOpcUaClient} gives
	 * more information on types.
	 * 
	 * <p>
	 * This is a blocking call which returns only after the request to the server
	 * has been completed. For a non-blocking variant, see
	 * {@link #readValueAsync(NodeId)}.
	 * 
	 * @param nodeId
	 *            The id of the node to read.
	 *
	 * @return The node's current value.
	 * 
	 * 
	 * @throws OpcUaException
	 *             if an OPC UA related error occurs or if the server can't provide
	 *             a valid value at this time. This is a generic wrapper type for
	 *             exceptions thrown by the client library.
	 * @throws IllegalArgumentException
	 *             if <code>nodeId</code> is <code>null</code>.
	 */
	Object readValue(NodeId nodeId) throws OpcUaException;

	/**
	 * Reads the current value from an OPC UA node.
	 * 
	 * <p>
	 * This is an asynchronous call returning a {@link CompletableFuture}. For more
	 * details about the value returned by the future and possible exceptions, see
	 * {@link #readValue(NodeId)}.
	 * 
	 * @param nodeId
	 *            The id of the node to read.
	 *
	 * @return A {@link CompletableFuture} for the node's current value.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>nodeId</code> is <code>null</code>.
	 */
	CompletableFuture<Object> readValueAsync(NodeId nodeId);

	/**
	 * Writes the value of an OPC UA node.
	 * 
	 * <p>
	 * The type of the value to write depends on the node's data type on the server
	 * and the caller is expected to know which type to pass. {@link IOpcUaClient}
	 * gives more information on types.
	 * 
	 * <p>
	 * This is a blocking call which returns only after the request to the server
	 * has been completed. For a non-blocking variant, see
	 * {@link #writeValueAsync(NodeId, Object)}.
	 * 
	 * @param nodeId
	 *            The id of the node to write.
	 * @param value
	 *            The new value to write. Can be <code>null</code>.
	 * 
	 * @throws OpcUaException
	 *             if an OPC UA related error occurs. This is a generic wrapper type
	 *             for exceptions thrown by the client library.
	 * @throws IllegalArgumentException
	 *             if <code>nodeId</code> is <code>null</code>.
	 */
	void writeValue(NodeId nodeId, Object value) throws OpcUaException;

	/**
	 * Writes the value of an OPC UA node.
	 * 
	 * <p>
	 * This is an asynchronous call returning a {@link CompletableFuture}. The
	 * future doesn't supply a value but can be used to wait for completion and to
	 * receive exceptions thrown during the write procedure. For more details about
	 * possible exceptions, see {@link #writeValue(NodeId, Object)}.
	 * 
	 * @param nodeId
	 *            The id of the node to write.
	 * @param value
	 *            The new value to write. Can be <code>null</code>.
	 *
	 * @return A {@link CompletableFuture}.
	 * 
	 * @throws IllegalArgumentException
	 *             if MiloNodeIdWrapper is <code>null</code>.
	 */
	CompletableFuture<Void> writeValueAsync(NodeId nodeId, Object value);
}
