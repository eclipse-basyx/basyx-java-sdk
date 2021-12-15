/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.opcua.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.opcua.server.BaSyxOpcUaClientRunner;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowsePath;
import org.eclipse.milo.opcua.stack.core.types.structured.CallResponse;
import org.eclipse.milo.opcua.stack.core.types.structured.RelativePath;
import org.eclipse.milo.opcua.stack.core.types.structured.RelativePathElement;
import org.eclipse.milo.opcua.stack.core.types.structured.TranslateBrowsePathsToNodeIdsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OPC UA connector class
 * 
 * @author kdorofeev
 *
 */
public class OpcUaConnector implements IModelProvider {
	
	private Logger logger = LoggerFactory.getLogger(OpcUaConnector.class);
	
    private String address;
    private BaSyxOpcUaClientRunner clientRunner;

    /**
     * Invoke a BaSys get operation via OPC UA
     * 
     * @param servicePath
     *            requested node path
     * @return the requested value
     */
    @Override
    public String getValue(String servicePath) {
        try {
            clientRunner = new BaSyxOpcUaClientRunner(address);
            clientRunner.run();
        } catch (Exception e) {
        	logger.error("Exception in getModelPropertyValue", e);
        	throw new RuntimeException(e);
        }
        return opcUaRead(translateBrowsePathToNodeId(servicePath)[1]);
    }

    @Override
	public void setValue(String servicePath, Object newValue) throws ProviderException {
        try {
            clientRunner = new BaSyxOpcUaClientRunner(address);
            clientRunner.run();
        } catch (Exception e) {
        	logger.error("Exception in setModelPropertyValue", e);
        	throw new RuntimeException(e);
        }
        opcUaWrite(translateBrowsePathToNodeId(servicePath)[1], newValue);
    }

    @Override
	public void createValue(String path, Object newEntity) throws ProviderException {

    }

    @Override
	public void deleteValue(String path) throws ProviderException {

    }

    @Override
	public void deleteValue(String path, Object obj) throws ProviderException {

    }

    @Override
	public Object invokeOperation(String servicePath, Object... parameters) throws ProviderException {
        try {
            clientRunner = new BaSyxOpcUaClientRunner(address);
            clientRunner.run();
        } catch (Exception e) {
        	logger.error("Exception in invokeOperation", e);
        	throw new RuntimeException(e);
        }
        return opcUaMethodCall(translateBrowsePathToNodeId(servicePath), parameters);
    }

    public OpcUaConnector(String address) {
        this.address = address;
    }

    /**
     * Perform a OPC UA read request
     * 
     * @param servicePath
     * @return
     */
    private String opcUaRead(NodeId servicePath) {
        try {
            List<NodeId> nodes = new ArrayList<NodeId>();
            nodes.add(servicePath);
            CompletableFuture<List<DataValue>> result = clientRunner.read(nodes);

            return result.get().get(0).getValue().getValue().toString();

        } catch (Exception e) {
        	logger.error("Exception in opcUaRead", e);
        }
        return null;
    }

    private String opcUaMethodCall(NodeId[] methodNodes, Object[] inputParameters) {
        try {
            Variant[] inputs = new Variant[inputParameters.length];
            for (int i = 0; i < inputParameters.length; i++) {
                inputs[i] = new Variant(inputParameters[i]);
            }
            CompletableFuture<CallResponse> result = clientRunner.callMethod(methodNodes[0], methodNodes[1], inputs);
            Variant[] outputs = result.get().getResults()[0].getOutputArguments();
            String ret = "";
            for (Variant var : outputs) {
                ret += var.getValue() + " ";
            }
            return ret;
        } catch (Exception e) {
        	logger.error("Exception in opcUaMethodCall", e);
        }
        return null;
    }

    private String opcUaWrite(NodeId servicePath, Object parameter) throws ProviderException {
        try {
            List<NodeId> nodes = new ArrayList<NodeId>();
            nodes.add(servicePath);
            List<DataValue> parameters = new ArrayList<DataValue>();
            parameters.add(new DataValue(new Variant(parameter), null, null, null));
            CompletableFuture<List<StatusCode>> result = clientRunner.write(nodes, parameters);

            return result.get().get(0).toString();
        } catch (Exception e) {
        	logger.error("Exception in opcUaWrite", e);
        }
        return null;
    }

    private NodeId[] translateBrowsePathToNodeId(String path) {
        String[] nodes = path.split("/");
        List<RelativePathElement> rpe_list = new ArrayList<RelativePathElement>();
        for (String node : nodes) {
            if (node.split(":").length != 2) {
            	logger.warn("OpcUaName should be in form namespaceIdx:identifier");
            }
            int nsIdx = Integer.valueOf(node.split(":")[0]);
            String name = node.split(":")[1];
            rpe_list.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true,
                    new QualifiedName(nsIdx, name)));
        }
        RelativePathElement[] rpe_node_arr = new RelativePathElement[rpe_list.size()];
        RelativePathElement[] rpe_parent_arr = new RelativePathElement[rpe_list.size() - 1];
        rpe_node_arr = rpe_list.toArray(rpe_node_arr);

        // get list for parent (all but the last one)
        rpe_list.remove(rpe_list.size() - 1);
        rpe_parent_arr = rpe_list.toArray(rpe_parent_arr);

        BrowsePath bp_node = new BrowsePath(Identifiers.RootFolder, new RelativePath(rpe_node_arr));
        BrowsePath bp_parent = new BrowsePath(Identifiers.RootFolder, new RelativePath(rpe_parent_arr));
        List<BrowsePath> bp_node_list = new ArrayList<BrowsePath>();
        List<BrowsePath> bp_parent_list = new ArrayList<BrowsePath>();
        bp_node_list.add(bp_node);
        bp_parent_list.add(bp_parent);
        try {
            CompletableFuture<TranslateBrowsePathsToNodeIdsResponse> result_node = clientRunner.translate(bp_node_list);
            CompletableFuture<TranslateBrowsePathsToNodeIdsResponse> result_parent = clientRunner
                    .translate(bp_parent_list);
            if (result_node.get().getResults().length == 0) {
                logger.warn("TranslateBrowsePathsToNodeIdsResponse result size = 0, check the browse path!");
                return null;
            }
            if (result_node.get().getResults().length > 1) {
            	logger.warn("TranslateBrowsePathsToNodeIdsResponse result size > 1, the method returns only the first one!");
            }
			if (result_node.get().getResults()[0].getTargets() == null || result_node.get().getResults()[0].getTargets().length == 0) {
            	logger.warn("TranslateBrowsePathsToNodeIdsResponse targets size = 0, check the browse path!");
				logger.trace(result_node.get().getResults()[0].getStatusCode().toString());
				return null;
            }
            if (result_node.get().getResults()[0].getTargets().length > 1) {
            	logger.warn("TranslateBrowsePathsToNodeIdsResponse targets size > 1, the method returns only the first one!");
            }
            Object nodeIdentifier = result_node.get().getResults()[0].getTargets()[0].getTargetId().getIdentifier();
            Object parentIdentifier = result_parent.get().getResults()[0].getTargets()[0].getTargetId().getIdentifier();
            UShort nodeNsIdx = result_node.get().getResults()[0].getTargets()[0].getTargetId().getNamespaceIndex();
            UShort parentNsIdx = result_parent.get().getResults()[0].getTargets()[0].getTargetId().getNamespaceIndex();
            if (nodeIdentifier instanceof String && parentIdentifier instanceof String) {
                return new NodeId[] { new NodeId(parentNsIdx, (String) parentIdentifier),
                        new NodeId(nodeNsIdx, (String) nodeIdentifier) };
            }
            if (nodeIdentifier instanceof UInteger && parentIdentifier instanceof UInteger) {
                return new NodeId[] { new NodeId(parentNsIdx, (UInteger) parentIdentifier),
                        new NodeId(nodeNsIdx, (UInteger) nodeIdentifier) };
            }
            if (nodeIdentifier instanceof UInteger && parentIdentifier instanceof String) {
                return new NodeId[] { new NodeId(parentNsIdx, (String) parentIdentifier),
                        new NodeId(nodeNsIdx, (UInteger) nodeIdentifier) };
            }
            if (nodeIdentifier instanceof String && parentIdentifier instanceof UInteger) {
                return new NodeId[] { new NodeId(parentNsIdx, (UInteger) parentIdentifier),
                        new NodeId(nodeNsIdx, (String) nodeIdentifier) };
            } else {
            	logger.error("NodeId identifier is not neither String, nor int");
                return null;
            }
        } catch (Exception e) {
        	logger.error("Exception in translateBrowsePathToNodeId", e);
        }
        return null;
    }

}
