/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.submodel.restapi.operation;

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;

/**
 * A helper class for operation invocation delegation
 * @author haque
 *
 */
public class DelegatedInvocationManager {
	public static final String DELEGATION_TYPE = "invocationDelegation";
	
	private IConnectorFactory connectorFactory;
	
	/**
	 * Constructs the DelegatedInvocationHelper using the passed connectorFactory for call delegation
	 * @param connectorFactory IConnectorFactory to be used for delegation
	 */
	public DelegatedInvocationManager(IConnectorFactory connectorFactory) {
		this.connectorFactory = connectorFactory;
	}
	
	/**
	 * Checks whether the given operation is delegated invocation
	 * @param operation
	 * @return
	 */
	public static boolean isDelegatingOperation(Operation operation) {
		return getDelegatedQualifier(operation) != null;
	}
	
	/**
	 * Invokes delegated operation using delegated URL
	 * @param operation
	 * @param parameters
	 * @return
	 */
	public Object invokeDelegatedOperation(Operation operation, Object... parameters) {
		String delegatedUrl = getDelegatedURL(operation);	
		return connectorFactory
				.create(delegatedUrl)
				.invokeOperation("", parameters);
	}
	
	/**
	 * Creates the Qualifier used to tag an Operation as delegating operation
	 * @param delegationURL the URL the Operation delegates to
	 * @return the delegation Qualifier
	 */
	public static Qualifier createDelegationQualifier(String delegationURL) {
		return new Qualifier(DelegatedInvocationManager.DELEGATION_TYPE, delegationURL, "string", null);
	}
	
	/**
	 * Retrieves the delegated URL of the operation invoke
	 * @param operation
	 * @return
	 * @throws RuntimeException if delegated qualifier does not exist
	 */
	private static String getDelegatedURL(Operation operation) throws RuntimeException {
		Qualifier qualifier = getDelegatedQualifier(operation);
		if (qualifier != null) {
			return qualifier.getValue().toString();
		} else {
			throw new RuntimeException("Qualifier with Delegated type does not exist");	
		}
	}
	
	/**
	 * Gets the delegated qualifier if exists. Otherwise null is returned
	 * @param operation
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Qualifier getDelegatedQualifier(Operation operation) {
		Collection<IConstraint> constraints = operation.getQualifiers();
		for (IConstraint constraint : constraints) {
			Qualifier qualifier = Qualifier.createAsFacade((Map<String, Object>)constraint);
			if (isDelegationQualifier(qualifier)) {
				return qualifier;
			}
		}
		return null;
	}

	private static boolean isDelegationQualifier(Qualifier qualifier) {
		return qualifier.getType() != null && qualifier.getType().equalsIgnoreCase(DELEGATION_TYPE);
	}
}
