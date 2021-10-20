/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.Ignore;

@Ignore
public class ConstantinTest extends TestSubmodelSuite {
	private final static Reference testSemanticIdRef = new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "testVal", IdentifierType.CUSTOM));
	private final static String PROP = "prop1";
	private final static String ID = "TestId";

	@Override
	protected ISubmodel getSubmodel() {

		IAASAggregator aggregator = new AASAggregatorProxy("http://localhost:5080");

		AssetAdministrationShell aas = new AssetAdministrationShell("aasIdShort", new CustomId("AASCustomId"), new Asset("assetIdShort", new CustomId("AssetCustomId"), AssetKind.INSTANCE));

		try {
			aggregator.deleteAAS(aas.getIdentification());
		} catch (ResourceNotFoundException e) {
			System.out.println("AAS not on server");
		}

		aggregator.createAAS(aas);

		IAssetAdministrationShell shell = aggregator.getAAS(aas.getIdentification());

		// Create the Submodel using the created property and operation
		IIdentifier submodelId = new ModelUrn("testUrn");
		Submodel localSubmodel = new Submodel(ID, submodelId);
		localSubmodel.setSemanticId(testSemanticIdRef);

		shell.addSubmodel(localSubmodel);

		// Create a simple value property
		Property propertyMeta = new Property(PROP, ValueType.Integer);
		propertyMeta.setValue(100);

		ISubmodel connectedSm = shell.getSubmodel(localSubmodel.getIdentification());
		connectedSm.addSubmodelElement(propertyMeta);

		return connectedSm;
	}

}
