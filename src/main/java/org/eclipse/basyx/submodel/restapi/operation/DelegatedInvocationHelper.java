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
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;

/**
 * A helper class for operation invocation delegation
 * @author haque
 *
 */
public class DelegatedInvocationHelper {
	public static final String DELEGATION_TYPE = "invocationDelegation";
	
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
	public static Object invokeDelegatedOperation(Operation operation, Object... parameters) {
		String delegatedUrl = getDelegatedURL(operation);	
		return new HTTPConnectorFactory()
				.getConnector(delegatedUrl)
				.invokeOperation("", parameters);
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
