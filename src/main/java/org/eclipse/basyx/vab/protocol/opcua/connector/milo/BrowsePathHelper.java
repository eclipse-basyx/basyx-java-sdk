/*******************************************************************************
 * Copyright (C) 2021 Festo Didactic SE
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.opcua.connector.milo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.basyx.vab.protocol.opcua.exception.OpcUaException;
import org.eclipse.basyx.vab.protocol.opcua.types.NodeId;
import org.eclipse.milo.opcua.stack.core.BuiltinReferenceType;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowsePath;
import org.eclipse.milo.opcua.stack.core.types.structured.RelativePath;
import org.eclipse.milo.opcua.stack.core.types.structured.RelativePathElement;

/**
 * Converts between string-formatted browse path and a {@link BrowsePath} object.
 *
 * <p>
 * The string must be formatted according to the
 * <a href="https://reference.opcfoundation.org/v104/Core/docs/Part4/A.2/">BNF format of relative
 * paths</a>.
 *
 * <p>
 * Note that this helper doesn't currently support browse paths which directly specify a reference
 * type to follow, using the <code>&lt;...&gt;</code> syntax. Only the more general hierarchical
 * (<code>/</code>) and aggregate (<code>.</code>) reference types are supported.
 */
public final class BrowsePathHelper {
    private static final String REFERENCE_TYPE_SPLIT = "(?<!&)[\\/\\.\\<]";
    private static final String NAMESPACE_SPLIT = "(?<!&):";

    private String path;
    private int index;

    private BrowsePathHelper(String path) {
        this.path = path;
        index = 0;
    }

    /**
     * Creates a browse path starting at the root node from the given string.
     *
     * @param s The relative browse path starting at the root node.
     *
     * @return The {@link BrowsePath} object represented by the given string.
     *
     * @throws OpcUaException                if the browse path is not valid.
     * @throws UnsupportedOperationException if the browse path contains directly specified reference
     *                                       types using the <code>&lt;...&gt;</code> syntax.
     * @throws IllegalArgumentException      if <code>s</code> is <code>null</code> or an empty string.
     */
    public static BrowsePath parse(String s) throws OpcUaException {
        NodeId root = new NodeId(Identifiers.RootFolder);
        return parse(root, s);
    }

    /**
     * Creates a browse path starting at the given node from the given string.
     *
     * @param startingNode The node from where to resolve the browse path.
     * @param s            The relative browse path starting at the root node.
     *
     * @return The {@link BrowsePath} object represented by the given string.
     *
     * @throws OpcUaException                if the browse path is not valid.
     * @throws UnsupportedOperationException if the browse path contains directly specified reference
     *                                       types using the <code>&lt;...&gt;</code> syntax.
     * @throws IllegalArgumentException      if startingNode or s are <code>null</code> or if s is an
     *                                       empty string.
     */
    public static BrowsePath parse(NodeId startingNode, String s) throws OpcUaException {
        if (startingNode == null || s == null) {
            throw new IllegalArgumentException("startingNode and s must not be null.");
        }
        if (s.isEmpty()) {
            throw new IllegalArgumentException("s must not be empty.");
        }

        BrowsePathHelper helper = new BrowsePathHelper(s);

        List<RelativePathElement> elements = new LinkedList<>();
        RelativePathElement elem;
        while ((elem = helper.next()) != null) {
            elements.add(elem);
        }

        RelativePathElement[] arr = elements.toArray(new RelativePathElement[0]);
        RelativePath relativePath = new RelativePath(arr);

        return new BrowsePath(startingNode.getInternalId(), relativePath);
    }

    /**
     * Creates a new browse path pointing to the second to last element of the given path.
     *
     * @param browsePath The original browse path.
     *
     * @return A new browse path which points to the second to last node in <code>browsePath</code>.
     *
     * @throws IllegalArgumentException if <code>browsePath</code> is <code>null</code> or it's relative
     *                                  path is empty.
     */
    public static BrowsePath getParent(BrowsePath browsePath) {
        if (browsePath == null) {
            throw new IllegalArgumentException("browsePath must not be null.");
        }

        RelativePathElement[] targetElements = browsePath.getRelativePath().getElements();

        if (targetElements.length == 0) {
            throw new IllegalArgumentException("Can't generate browse path to parent of an empty path.");
        }

        // Copy all but the last element from the original relative path to a new array and
        // create a new relative path from that.
        RelativePathElement[] parentElements = Arrays.copyOf(targetElements, targetElements.length - 1);
        RelativePath parentPath = new RelativePath(parentElements);

        return new BrowsePath(browsePath.getStartingNode(), parentPath);
    }

    /**
     * Returns a string representation of the given relative path.
     *
     * @param relativePath The relative path of a browse path.
     *
     * @return The string representation in accordance with the
     *         <a href="https://reference.opcfoundation.org/v104/Core/docs/Part4/A.2/">BNF format of
     *         relative paths</a>.
     *
     * @throws IllegalArgumentException if relativePath contains references, which don't map to either
     *                                  '/' or '.' or if it is invalid.
     */
    public static String toString(RelativePath relativePath) {
        StringBuilder sb = new StringBuilder();

        RelativePathElement[] elems = relativePath.getElements();

        for (int i = 0; i < elems.length; i++) {
            if (isAllHierarchicalReferences(elems[i])) {
                sb.append('/');
            } else if (isAllAggregatesReferences(elems[i])) {
                sb.append('.');
            } else {
                throw new IllegalArgumentException(
                        "relativePath contains directly specified references which aren't supported.");
            }

            if (elems[i].getTargetName() != null) {
                sb.append(qualifiedNameToString(elems[i].getTargetName()));
            } else if (i != elems.length - 1) {
                throw new IllegalArgumentException(
                        "Only the last element in a relative path is allowed to have no name.");
            }
        }

        return sb.toString();
    }

    private static boolean isAllHierarchicalReferences(RelativePathElement rpe) {
        return rpe.getReferenceTypeId().equals(BuiltinReferenceType.HierarchicalReferences.getNodeId())
                && !rpe.getIsInverse()
                && rpe.getIncludeSubtypes();
    }

    private static boolean isAllAggregatesReferences(RelativePathElement rpe) {
        return rpe.getReferenceTypeId().equals(BuiltinReferenceType.Aggregates.getNodeId())
                && !rpe.getIsInverse()
                && rpe.getIncludeSubtypes();
    }

    private static String qualifiedNameToString(QualifiedName qn) {
        return qn.getNamespaceIndex().toString() + ':' + qn.getName();
    }

    private RelativePathElement next() {
        if (index >= path.length()) {
            return null;
        }

        org.eclipse.milo.opcua.stack.core.types.builtin.NodeId referenceType = parseReferenceType();
        QualifiedName qualifiedName = parseQualifiedName();
        return new RelativePathElement(referenceType, false, true, qualifiedName);
    }

    private org.eclipse.milo.opcua.stack.core.types.builtin.NodeId parseReferenceType() {
        switch (path.charAt(index)) {
        case '/':
            index++;
            return BuiltinReferenceType.HierarchicalReferences.getNodeId();

        case '.':
            index++;
            return BuiltinReferenceType.Aggregates.getNodeId();

        case '<':
            throw new IllegalArgumentException("This helper doesn't supported directly specified reference types.");

        default:
            throw new OpcUaException(String.format("Invalid browse path at index %d: %s", index, path));
        }
    }

    private QualifiedName parseQualifiedName() {
        if (index == path.length()) {
            // The last element in a browse path is allowed to have no qualified name.
            return null;
        }

        // Returns only the part of the path between the current index and the next separator.
        String pathElement = path.substring(index).split(REFERENCE_TYPE_SPLIT, 2)[0];

        String[] s = pathElement.split(NAMESPACE_SPLIT, 3);
        QualifiedName qn;
        if (s.length == 1) {
            // RelativePathElement has no namespace index -> Uses default namespace '0'.
            qn = parseQualifiedName(s[0]);
        } else if (s.length == 2) {
            // RelativePathElement has a namespace index.
            qn = parseQualifiedName(s[0], s[1]);
        } else {
            // RelativePathElement contains more than one colon.
            throw new OpcUaException(String.format("Not a valid relative path element starting at index %d: %s", index,
                    path));
        }

        index += pathElement.length();
        return qn;
    }

    private QualifiedName parseQualifiedName(String browseName) {
        String unescaped = unescape(browseName);
        if (unescaped.isEmpty()) {
            throw new OpcUaException(String.format("Browse path contains invalid browse name at index %d: %s", index,
                    path));
        }

        return new QualifiedName(0, unescaped);
    }

    private QualifiedName parseQualifiedName(String namespaceIndex, String browseName) {
        int ns;
        try {
            ns = Integer.parseUnsignedInt(namespaceIndex);
        } catch (NumberFormatException e) {
            throw new OpcUaException(String.format("Browse path contains invalid namespace index at index %d: %s",
                    index, path));
        }

        String escaped = unescape(browseName);
        if (escaped.isEmpty()) {
            throw new OpcUaException(String.format("Browse path contains invalid browse name at index %d: %s", index,
                    path));
        }
        return new QualifiedName(ns, escaped);
    }

    private String unescape(String s) {
        return s.replace("&", "");
    }
}
