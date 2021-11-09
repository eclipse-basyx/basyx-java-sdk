/**
 *
 */
package org.eclipse.basyx.aas.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.aas.aggregator.AASAggregatorAPIHelper;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.manager.api.IAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.endpoint.Endpoint;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedSubmodel;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.vab.exception.FeatureNotImplementedException;
import org.eclipse.basyx.vab.factory.java.ModelProxyFactory;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;

/**
 * Implement a AAS manager backend that communicates via HTTP/REST<br>
 * <br>
 *
 * @author kuhn, schnicke
 *
 */
public class ConnectedAssetAdministrationShellManager implements IAssetAdministrationShellManager {

	protected IAASRegistry aasDirectory;
	protected IConnectorFactory connectorFactory;
	protected ModelProxyFactory proxyFactory;

	/**
	 * Creates a manager assuming an HTTP connection
	 *
	 * @param directory
	 */
	public ConnectedAssetAdministrationShellManager(IAASRegistry directory) {
		this(directory, new HTTPConnectorFactory());
	}

	/**
	 * @param directory
	 * @param provider
	 */
	public ConnectedAssetAdministrationShellManager(IAASRegistry directory,
			IConnectorFactory provider) {
		this.aasDirectory = directory;
		this.connectorFactory = provider;
		this.proxyFactory = new ModelProxyFactory(provider);
	}

	@Override
	public ISubmodel retrieveSubmodel(IIdentifier aasId, IIdentifier smId) {
		// look up SM descriptor in the registry
		SubmodelDescriptor smDescriptor = aasDirectory.lookupSubmodel(aasId, smId);

		// get address of the submodel descriptor
		String addr = smDescriptor.getFirstEndpoint().getProtocolInformation().getEndpointAddress();

		// Return a new VABElementProxy
		return new ConnectedSubmodel(proxyFactory.createProxy(addr));
	}

	@Override
	public ConnectedAssetAdministrationShell retrieveAAS(IIdentifier aasId) {
		VABElementProxy proxy = getAASProxyFromId(aasId);
		return new ConnectedAssetAdministrationShell(proxy);
	}

	@Override
	public Map<String, ISubmodel> retrieveSubmodels(IIdentifier aasId) {
		AASDescriptor aasDesc = aasDirectory.lookupShell(aasId);
		Collection<SubmodelDescriptor> smDescriptors = aasDesc.getSubmodelDescriptors();
		Map<String, ISubmodel> submodels = new HashMap<>();
		for (SubmodelDescriptor smDesc : smDescriptors) {
			String smEndpoint = smDesc.getFirstEndpoint().getProtocolInformation().getEndpointAddress();
			String smIdShort = smDesc.getIdShort();
			VABElementProxy smProxy = proxyFactory.createProxy(smEndpoint);
			ConnectedSubmodel connectedSM = new ConnectedSubmodel(smProxy);
			submodels.put(smIdShort, connectedSM);
		}
		return submodels;
	}

	private VABElementProxy getAASProxyFromId(IIdentifier aasId) {
		// Lookup AAS descriptor
		AASDescriptor aasDescriptor = aasDirectory.lookupShell(aasId);

		// Get AAS address from AAS descriptor
		String addr = aasDescriptor.getFirstEndpoint().getProtocolInformation().getEndpointAddress();

		// Return a new VABElementProxy
		return proxyFactory.createProxy(addr);
	}

	@Override
	public Collection<IAssetAdministrationShell> retrieveAASAll() {
		throw new FeatureNotImplementedException();
	}

	@Override
	public void deleteAAS(IIdentifier id) {
		// Lookup AAS descriptor
		AASDescriptor aasDescriptor = aasDirectory.lookupShell(id);

		// Get AAS address from AAS descriptor
		String addr = aasDescriptor.getFirstEndpoint().getProtocolInformation().getEndpointAddress();

		// Address ends in "/aas", has to be stripped for removal
		addr = VABPathTools.stripSlashes(addr);
		addr = addr.substring(0, addr.length() - "/aas".length());

		// Delete from server
		proxyFactory.createProxy(addr).deleteValue("");

		// Delete from Registry
		aasDirectory.deleteShell(id);

		// TODO: How to handle submodels -> Lifecycle needs to be clarified
	}

	@Override
	public void createSubmodel(IIdentifier aasId, Submodel submodel) {

		// Push the SM to the server using the ConnectedAAS

		retrieveAAS(aasId).addSubmodel(submodel);

		// Lookup AAS descriptor
		AASDescriptor aasDescriptor = aasDirectory.lookupShell(aasId);

		// Get aas endpoint
		String addr = aasDescriptor.getFirstEndpoint().getProtocolInformation().getEndpointAddress();

		// Register the SM
		String smEndpoint = VABPathTools.concatenatePaths(addr, AssetAdministrationShell.SUBMODELS, submodel.getIdShort(), SubmodelProvider.SUBMODEL);
		aasDirectory.registerSubmodelForShell(aasId, new SubmodelDescriptor(submodel, new Endpoint(smEndpoint)));
	}

	@Override
	public void deleteSubmodel(IIdentifier aasId, IIdentifier submodelId) {
		IAssetAdministrationShell shell = retrieveAAS(aasId);
		shell.removeSubmodel(submodelId);

		aasDirectory.deleteSubmodelFromShell(aasId, submodelId);
	}

	@Override
	public void createAAS(AssetAdministrationShell aas, String endpoint) {
		endpoint = VABPathTools.stripSlashes(endpoint);

		IModelProvider provider = connectorFactory.getConnector(endpoint);
		AASAggregatorProxy proxy = new AASAggregatorProxy(provider);
		proxy.createAAS(aas);
		String combinedEndpoint = VABPathTools.concatenatePaths(endpoint, AASAggregatorAPIHelper.getAASAccessPath(aas.getIdentification()));
		aasDirectory.register(new AASDescriptor(aas, new Endpoint(combinedEndpoint)));

	}
}
