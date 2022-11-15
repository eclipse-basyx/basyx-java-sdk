/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.extensions.shared.delegation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IQualifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.AASLambdaPropertyHelper;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.ConnectorFactory;

/**
 * Checks and manages Property with delegation
 * 
 * @author danish
 */
public class PropertyDelegationManager {
	private static final String DELEGATION_TYPE = "delegatedTo";

	private ConnectorFactory connectorProvider;

	public PropertyDelegationManager(ConnectorFactory connectorProvider) {
		this.connectorProvider = connectorProvider;
	}

	public void handleSubmodel(ISubmodel submodel) {
		submodel.getSubmodelElements().values().stream().map(SubmodelElement.class::cast).forEach(this::handleSubmodelElement);
	}

	public static IQualifier createDelegationQualifier(String delegationUrl) {
		Qualifier qualifier = new Qualifier(DELEGATION_TYPE, ValueType.String);
		qualifier.setValue(delegationUrl);

		return qualifier;
	}
	
	public void handleSubmodelElement(SubmodelElement element) {
		if (Property.isProperty(element)) {
			handleProperty(Property.createAsFacade(element));
		} else if (SubmodelElementCollection.isSubmodelElementCollection(element)) {
			handeSubmodelElementCollection(SubmodelElementCollection.createAsFacade(element));
		}
	}
	
	public boolean isDelegationQualifier(IConstraint iConstraint) {
		return iConstraint instanceof Qualifier && ((Qualifier) iConstraint).getType().equals(DELEGATION_TYPE);
	}

	private void handeSubmodelElementCollection(SubmodelElementCollection smc) {
		smc.getSubmodelElements().values().stream().map(SubmodelElement.class::cast)
				.forEach(this::handleSubmodelElement);
	}

	private void handleProperty(Property property) {
		Collection<IConstraint> qualifiers = property.getQualifiers();
		Optional<IConstraint> optionalConstraint = qualifiers.stream().filter(this::isDelegationQualifier).findAny();

		if (optionalConstraint.isEmpty())
			return;

		Qualifier delegationQualifier = (Qualifier) optionalConstraint.get();
		configureDelegationProperty(property, delegationQualifier);
	}

	private void configureDelegationProperty(SubmodelElement submodelElement, IConstraint iConstraint) {
		String delegatedTo = ((Qualifier) iConstraint).getValue().toString();

		AASLambdaPropertyHelper.setLambdaValue((Property) submodelElement, () -> getPropertyValue(delegatedTo), null);
	}

	private Object getPropertyValue(String delegatedTo) {
		try {
			URL url = new URL(delegatedTo);
			String address = createAddressFromURL(url);
			IModelProvider connector = connectorProvider.create(address);
			return connector.getValue(url.getPath());
		} catch (MalformedURLException e) {
			throw new ProviderException(e);
		}
	}

	private String createAddressFromURL(URL url) {
		return url.getProtocol() + "://" + url.getHost() + ":"
				+ (url.getPort() != -1 ? Integer.toString(url.getPort()) : "");
	}
}
