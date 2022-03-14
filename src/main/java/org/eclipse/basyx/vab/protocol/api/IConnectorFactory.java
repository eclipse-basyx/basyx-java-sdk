/**
 * 
 */
package org.eclipse.basyx.vab.protocol.api;

import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Base interface for factories that return IModelProviders that connects to
 * specific addresses
 * 
 * @author schnicke
 *
 */
public interface IConnectorFactory {

	/**
	 * Gets an IModelProvider connecting the specific address.
	 * @deprecated This method is deprecated please use {@link #create(Submodel)}
	 *
	 * @param addr 
	 * 		The address for which a provider is returned. Must be an address limited to one included endpoint.
	 * 		For example, it does NOT support basyx://localhost:6998//http://localhost/a/b/c, but http://localhost/a/b/c
	 * @return
	 * 		A provider that directly points to the element referenced by the given address. 
	 * 		E.g. the returned model provider for http://localhost/a/b/c directly points to the element c. Therefore, 
	 * 		getConnector("http://localhost/a/b/c").getModelPropertyValue(""); returns the value of the element c.  
	 */
	@Deprecated
	public IModelProvider getConnector(String addr);
	
	public default IModelProvider create(String addr) {
		return getConnector(addr);
	}
}
