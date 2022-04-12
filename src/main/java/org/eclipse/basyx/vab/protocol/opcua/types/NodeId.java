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
package org.eclipse.basyx.vab.protocol.opcua.types;

import java.util.Objects;
import java.util.UUID;

import org.eclipse.basyx.vab.protocol.opcua.exception.OpcUaException;
import org.eclipse.milo.opcua.stack.core.UaRuntimeException;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

/**
 * A node id unique identifies a node within an OPC UA server's address space.
 */
public final class NodeId {
    private final org.eclipse.milo.opcua.stack.core.types.builtin.NodeId internalId;
    private String cachedStringRepresentation;

    /**
     * Creates a numeric node id with the given namespace.
     *
     * <p>
     * Numeric node ids technically use an unsigned 32 bit integer type. Since that type doesn't exist
     * in Java, this method accepts a long, instead. The identifier is, however, not allowed to be
     * negative.
     *
     * @param namespaceIndex The index of the node's namespace.
     * @param identifier     The numeric id. Must be greater or equal 0.
     *
     * @throws IllegalArgumentException if the identifier is negative.
     */
    public NodeId(int namespaceIndex, long identifier) {
        if (identifier < 0) {
            throw new IllegalArgumentException("Numeric identifier must not be negative.");
        }

        internalId = new org.eclipse.milo.opcua.stack.core.types.builtin.NodeId(namespaceIndex, UInteger.valueOf(
                identifier));
    }

    /**
     * Creates a string node id with the given namespace.
     *
     * @param namespaceIndex The index of the node's namespace.
     * @param identifier     The string id. Maximum length is 4096 characters.
     *
     * @throws IllegalArgumentException if identifier is <code>null</code> or exceeds the maximum
     *                                  length.
     */
    public NodeId(int namespaceIndex, String identifier) {
        Objects.requireNonNull(identifier);

        if (identifier.length() > 4096) {
            throw new IllegalArgumentException("String identifier must not be longer than 4096 characters.");
        }

        internalId = new org.eclipse.milo.opcua.stack.core.types.builtin.NodeId(namespaceIndex, identifier);
    }

    /**
     * Creates a GUID node id with the given namespace.
     *
     * @param namespaceIndex The index of the node's namespace.
     * @param identifier     The GUID id.
     *
     * @throws IllegalArgumentException if identifier is <code>null</code>.
     */
    public NodeId(int namespaceIndex, UUID identifier) {
        Objects.requireNonNull(identifier);

        internalId = new org.eclipse.milo.opcua.stack.core.types.builtin.NodeId(namespaceIndex, identifier);
    }

    /**
     * Creates a ByteString node id with the given namespace.
     *
     * <p>
     * ByteString is a special OPC UA type that loosely corresponds to a Java array of bytes.
     *
     * @param namespaceIndex The index of the node's namespace.
     * @param identifier     The ByteString id. Maximum length is 4096 bytes.
     *
     * @throws IllegalArgumentException if identifier is <code>null</code> or exceeds the maximum
     *                                  length.
     */
    public NodeId(int namespaceIndex, byte[] identifier) {
        Objects.requireNonNull(identifier);

        if (identifier.length > 4096) {
            throw new IllegalArgumentException("ByteString identifier must not be longer than 4096 bytes.");
        }

        internalId = new org.eclipse.milo.opcua.stack.core.types.builtin.NodeId(namespaceIndex, ByteString.of(
                identifier));
    }

    public NodeId(org.eclipse.milo.opcua.stack.core.types.builtin.NodeId miloId) {
        internalId = miloId;
    }

    /**
     * Creates a node id from a string representation in the standard string format (e.g.
     * <code>ns=1;i=1234</code>).
     *
     * <p>
     * For a variant of this method which doesn't throw exceptions, see {@link #tryParse(String)}.
     *
     * @param s String representation of the NodeId.
     *
     * @return The created node id.
     *
     * @throws OpcUaException if the string doesn't represent a valid node id.
     */
    public static NodeId parse(String s) {
        try {
            org.eclipse.milo.opcua.stack.core.types.builtin.NodeId miloId;
            miloId = org.eclipse.milo.opcua.stack.core.types.builtin.NodeId.parse(s);
            return new NodeId(miloId);
        } catch (UaRuntimeException e) {
            throw new OpcUaException(e);
        }
    }

    /**
     * Creates a node id from a string representation in the standard string format (e.g.
     * <code>ns=1;i=1234</code>).
     *
     * <p>
     * This method is similar to {@link #parse(String)} but returns <code>null</code> instead of
     * throwing an exception if the given string doesn't represent a node id.
     *
     * @param s String representation of the NodeId.
     *
     * @return The created node id or <code>null</code> if s doesn't represent a node id.
     */
    public static NodeId tryParse(String s) {
        org.eclipse.milo.opcua.stack.core.types.builtin.NodeId miloId;
        miloId = org.eclipse.milo.opcua.stack.core.types.builtin.NodeId.parseOrNull(s);
        return (miloId != null) ? new NodeId(miloId) : null;
    }

    /**
     * Gets the wrapped Milo NodeId.
     *
     * @return The wrapped {@link org.eclipse.milo.opcua.stack.core.types.builtin.NodeId}.
     */
    public org.eclipse.milo.opcua.stack.core.types.builtin.NodeId getInternalId() {
        return internalId;
    }

    /**
     * Gets the namespace index of this node id.
     *
     * @return The namespace part of this node id.
     */
    public int getNamespaceIndex() {
        return internalId.getNamespaceIndex().intValue();
    }

    /**
     * Gets the identifier of this node id.
     *
     * <p>
     * The returned type depends on the type of the node id.
     * <ul>
     * <li>Numeric id: {@link Long}
     * <li>String id: {@link String}
     * <li>GUID id: {@link UUID}
     * <li>Opaque id: byte[]
     * </ul>
     *
     * @return The identifier part of the node id.
     */
    public Object getIdentifier() {
        switch (internalId.getType()) {
        case Opaque:
            return ((ByteString) internalId.getIdentifier()).bytes();
        case Numeric:
            return ((UInteger) internalId.getIdentifier()).longValue();
        default:
            return internalId.getIdentifier();
        }
    }

    /**
     * Returns a machine-parseable string representation of this node id.
     *
     * <p>
     * The returned string is formatted according to the machine-parseable formatting rules outlined in
     * <a href="https://reference.opcfoundation.org/v104/Core/docs/Part6/5.3.1/#5.3.1.10">part 6</a> of
     * the specification.
     *
     * <p>
     * During the first call to this method the string is initially generated and cached. Subsequent
     * calls come at no additional computation cost.
     *
     * @return A machine-parseable string representation of the node id.
     */
    @Override
    public String toString() {
        if (cachedStringRepresentation == null) {
            cachedStringRepresentation = internalId.toParseableString();
        }

        return cachedStringRepresentation;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NodeId)) {
            return false;
        }

        NodeId other = (NodeId) obj;
        return internalId.equals(other.internalId);
    }

    @Override
    public int hashCode() {
        return internalId.hashCode();
    }
}
