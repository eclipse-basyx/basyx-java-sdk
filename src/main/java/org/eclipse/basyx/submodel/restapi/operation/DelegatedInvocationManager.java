/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
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

package org.eclipse.basyx.submodel.restapi.operation;

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;

/**
 * A helper class for operation invocation delegation
 * 
 * @author haque
 *
 */
public class DelegatedInvocationManager {
	public static final String DELEGATION_TYPE = "invocationDelegation";

	private IConnectorFactory connectorFactory;

	/**
	 * Constructs the DelegatedInvocationHelper using the passed connectorFactory
	 * for call delegation
	 * 
	 * @param connectorFactory
	 *            IConnectorFactory to be used for delegation
	 */
	public DelegatedInvocationManager(IConnectorFactory connectorFactory) {
		this.connectorFactory = connectorFactory;
	}

	/**
	 * Checks whether the given operation is delegated invocation
	 * 
	 * @param operation
	 * @return
	 */
	public static boolean isDelegatingOperation(Operation operation) {
		return getDelegatedQualifier(operation) != null;
	}

	/**
	 * Invokes delegated operation using delegated URL
	 * 
	 * @param operation
	 * @param parameters
	 * @return
	 */
	public Object invokeDelegatedOperation(Operation operation, Object... parameters) {
		String delegatedUrl = getDelegatedURL(operation);
		return connectorFactory.create(delegatedUrl).invokeOperation("", parameters);
	}

	/**
	 * Creates the Qualifier used to tag an Operation as delegating operation
	 * 
	 * @param delegationURL
	 *            the URL the Operation delegates to
	 * @return the delegation Qualifier
	 */
	public static Qualifier createDelegationQualifier(String delegationURL) {
		return new Qualifier(DelegatedInvocationManager.DELEGATION_TYPE, delegationURL, "string", null);
	}

	/**
	 * Retrieves the delegated URL of the operation invoke
	 * 
	 * @param operation
	 * @return
	 * @throws RuntimeException
	 *             if delegated qualifier does not exist
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
	 * 
	 * @param operation
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Qualifier getDelegatedQualifier(Operation operation) {
		Collection<IConstraint> constraints = operation.getQualifiers();
		for (IConstraint constraint : constraints) {
			Qualifier qualifier = Qualifier.createAsFacade((Map<String, Object>) constraint);
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
