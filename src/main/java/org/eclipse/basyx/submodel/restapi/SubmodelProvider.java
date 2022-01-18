/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.restapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;

/**
 * Additional VAB provider specific for providing submodels together
 * with other SDKs by implementing the submodel API.
 *
 * The VAB provides generic models without considering high-level information.
 * For example, Operation submodel elements are realized as a Map. The function
 * is contained and can be accessed at /operations/opId/invokable. Therefore
 * invoking the Operation directly at /operations/opId/ attempts to invoke the
 * Map, which does not have any effect for the VABModelProvider.
 *
 * This provider maps these requests to the VAB primitives to generate the
 * desired behavior as it is described in the BaSys documentation.
 *
 * @author espen, schnicke
 *
 */
public class SubmodelProvider implements IModelProvider {

	public static final String VALUES = "values";
	public static final String SUBMODEL = "submodel";
	
	ISubmodelAPI submodelAPI;

	/**
	 * Default constructor - based on an empty submodel with a lambda provider
	 */
	public SubmodelProvider() {
		this(new Submodel());
	}

	/**
	 * Creates a SubmodelProvider based on the VAB API, wrapping the passed provider
	 * 
	 * @param provider
	 *            to be wrapped by submodel API
	 */
	public SubmodelProvider(IModelProvider provider) {
		submodelAPI = new VABSubmodelAPI(provider);
	}

	/**
	 * Creates a SubmodelProvider based on a lambda provider and a given model
	 */
	public SubmodelProvider(Submodel model) {
		submodelAPI = new VABSubmodelAPI(new VABLambdaProvider(model));
	}

	/**
	 * Creates a SubmodelProvider based on a given ISubmodelAPI.
	 */
	public SubmodelProvider(ISubmodelAPI submodelAPI) {
		this.submodelAPI = submodelAPI;
	}

	/**
	 * Strips submodel prefix if it exists
	 *
	 * @param path
	 * @return
	 */
	private String removeSubmodelPrefix(String path) {
		path = VABPathTools.stripSlashes(path);
		String submodelWithSlash = SUBMODEL + "/";
		if (path.startsWith(submodelWithSlash)) {
			path = path.replaceFirst(submodelWithSlash, "");
		} else if (path.equals(SUBMODEL)) {
			path = "";
		} else {
			throw new MalformedRequestException("The request " + path + " is not allowed for this endpoint. /" + SUBMODEL + " is missing");
		}
		path = VABPathTools.stripSlashes(path);
		return path;
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		VABPathTools.checkPathForNull(path);
		path = removeSubmodelPrefix(path);
		if (path.isEmpty()) {
			ISubmodel sm = submodelAPI.getSubmodel();

			// Change internal map representation to set
			if (sm instanceof Submodel) {
				return SubmodelElementMapCollectionConverter.smToMap((Submodel) sm);
			} else {
				return sm;
			}
		} else {
			String[] splitted = VABPathTools.splitPath(path);
			// Request for submodelElements
			if (splitted.length == 1 && splitted[0].equals(VALUES)) {
				// Request for values of all submodelElements
				return submodelAPI.getSubmodel().getValues();
			} else if (splitted.length == 1 && splitted[0].equals(MultiSubmodelElementProvider.ELEMENTS)) {
				return submodelAPI.getSubmodelElements();
			} else if (splitted.length >= 2 && isQualifier(splitted[0])) { // Request for element with specific idShort
				// Remove initial "/submodelElements"
				path = removeSMElementPrefix(path);

                if (endsWithValue(splitted)) { // Request for the value of an property
                    String idShortPath = removeValueSuffix(path);
                    return submodelAPI.getSubmodelElementValue(idShortPath);
                } else if (isInvocationListPath(splitted)) {
                    List<String> idShorts = getIdShorts(splitted);

                    // Remove invocationList/{requestId} from the idShorts
                    idShorts.remove(idShorts.size() - 1);
                    idShorts.remove(idShorts.size() - 1);
                    return submodelAPI.getOperationResult(idShorts.get(0), splitted[splitted.length - 1]);
                } else {
                    return submodelAPI.getSubmodelElement(path);
                }
            }
        }
        throw new MalformedRequestException("Unknown path " + path + " was requested");
	}

	private List<String> getIdShorts(String[] splitted) {
		// Create list from array and wrap it in ArrayList to ensure modifiability
		List<String> idShorts = new ArrayList<>(Arrays.asList(splitted));

		// Drop inital "submodelElements"
		idShorts.remove(0);

		// If value is contained in path, remove it
		if (splitted[splitted.length - 1].equals(Property.VALUE)) {
			idShorts.remove(idShorts.size() - 1);
		}
		return idShorts;
	}


	private boolean endsWithValue(String[] splitted) {
		return splitted[splitted.length - 1].equals(Property.VALUE);
	}

	/**
     * Removes a trailing <code>/value</code> from the path if it exists.
     *
     * @param path The original path, which might or might not end on {@value Property.VALUE}.
     * @return The new path
     */
    private String removeValueSuffix(String path) {
        String suffix = "/" + Property.VALUE;
        if (path.endsWith(suffix)) {
            path = path.substring(0, path.length() - suffix.length());
        }

        return path;
    }

	private boolean isInvocationListPath(String[] splitted) {
		return splitted.length > 2 && splitted[splitted.length - 2].equals(OperationProvider.INVOCATION_LIST);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		path = removeSubmodelPrefix(path);
		if (path.isEmpty()) {
			throw new MalformedRequestException("Set on \"" + SUBMODEL + "\" not supported");
		} else {
			String[] splitted = VABPathTools.splitPath(path);
			path = removeSMElementPrefix(path);
			String idshortPath = removeValueSuffix(path);
			if (endsWithValue(splitted)) {
				submodelAPI.updateSubmodelElement(idshortPath, newValue);
			} else {
				
				SubmodelElement element = SubmodelElement.createAsFacade((Map<String, Object>) newValue);
				
				if(!path.endsWith(element.getIdShort())) {
					throw new MalformedRequestException("The idShort of given Element '"
							+ element.getIdShort() + "' does not match the ending of the given path '" + path + "'");
				}
				
				submodelAPI.addSubmodelElement(idshortPath, element);
			}
		}
	}

	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		throw new MalformedRequestException("POST (create) on '" + path + "' not allowed. Use PUT (set) instead.");
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		path = removeSubmodelPrefix(path);
		if (!path.isEmpty()) {
			String[] splitted = VABPathTools.splitPath(path);
			if (isQualifier(splitted[0])) {
				if (splitted.length > 2) {
					path = removeSMElementPrefix(path);
					submodelAPI.deleteSubmodelElement(path);
				} else {
					submodelAPI.deleteSubmodelElement(splitted[1]);
				}
			} else {
				throw new MalformedRequestException("Path " + path + " not supported for delete");
			}
		} else {
			throw new MalformedRequestException("Path \"" + SUBMODEL + "\" not supported for delete");
		}
	}

	private boolean isQualifier(String str) {
		return str.equals(MultiSubmodelElementProvider.ELEMENTS);
	}

	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {
		throw new MalformedRequestException("Delete with a passed argument not allowed");
	}

	@Override
	public Object invokeOperation(String path, Object... parameters) throws ProviderException {
		path = removeSubmodelPrefix(path);
		if (path.isEmpty()) {
			throw new MalformedRequestException("Given path must not be empty");
		} else {
			if (VABPathTools.isOperationInvokationPath(path)) {
				if(path.endsWith(OperationProvider.ASYNC)) {
					path = removeSMElementPrefix(path);
					path = path.replaceFirst(Pattern.quote(Operation.INVOKE + OperationProvider.ASYNC), "");
					return submodelAPI.invokeAsync(path, parameters);
				} else {
					path = removeSMElementPrefix(path);
					return submodelAPI.invokeOperation(path, parameters);
				}
			} else {
				throw new MalformedRequestException("Given path '" + path + "' does not end in /" + Operation.INVOKE);
			}
		}
	}

	public ISubmodelAPI getAPI() {
		return this.submodelAPI;
	}
	
	protected void setAPI(ISubmodelAPI api) {
		this.submodelAPI = api;
	}
	
	private String removeSMElementPrefix(String path) {
		return  path.replaceFirst(MultiSubmodelElementProvider.ELEMENTS, "");
	}
}
