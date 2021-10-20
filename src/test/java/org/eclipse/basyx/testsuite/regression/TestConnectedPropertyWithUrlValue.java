/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression;

import java.util.Arrays;
import java.util.function.Function;

import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.AdministrativeInformation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.junit.Test;

/**
 * Test JSONConnector against JSONProvider
 * 
 * @author pschorn
 *
 */
public class TestConnectedPropertyWithUrlValue {

	@Test
	public void test() {
		AssetAdministrationShell aas = new AssetAdministrationShell();
		aas.setIdShort("PersistenceAccess");
		aas.setIdentification(new Identifier(IdentifierType.CUSTOM, "op.basyx.controller/PersistenceAccess:1"));
		aas.setAdministration(new AdministrativeInformation("1", "1.0"));

		Submodel submodel = new Submodel();
		submodel.setIdShort("PersistenceAccess");
		submodel.setIdentification(
				new Identifier(IdentifierType.CUSTOM, "op.basyx.controller/PersistenceAccess/PersistenceAccess:1"));
		submodel.setAdministration(new AdministrativeInformation("1", "1.0"));

		aas.addSubmodel(submodel);
		aas.addSubmodelReference(submodel.getReference());

		Operation opPersistAasEnv = new Operation();
		opPersistAasEnv.setIdShort("persistAasEnv");
		Property pAasEnv = new Property();
		pAasEnv.setIdShort("aasEnv");
		pAasEnv.setModelingKind(ModelingKind.TEMPLATE);
		pAasEnv.setValueType(ValueType.String);
		Property pPersistentIds = new Property();
		pPersistentIds.setIdShort("persistentIds");
		pPersistentIds.setModelingKind(ModelingKind.TEMPLATE);
		pPersistentIds.setValueType(ValueType.String);
		opPersistAasEnv.setInputVariables(
				Arrays.asList(new OperationVariable(pAasEnv), new OperationVariable(pPersistentIds)));
		opPersistAasEnv.setInvokable((Function<Object[], Object>) params -> {
			return null;
		});
		submodel.addSubmodelElement(opPersistAasEnv);

		AASAggregator aggregator = new AASAggregator();
		aggregator.createAAS(aas);
		MultiSubmodelProvider aasProvider = (MultiSubmodelProvider) aggregator.getAASProvider(aas.getIdentification());
		aasProvider.addSubmodel(new SubmodelProvider(submodel));

		AASAggregatorProvider provider = new AASAggregatorProvider(aggregator);
		VABElementProxy providerWrapper = new VABElementProxy("/shells", provider);
		AASAggregatorProxy proxy = new AASAggregatorProxy(providerWrapper);
		System.out.println(proxy.getAAS(aas.getIdentification()).getSubmodel(submodel.getIdentification()));
		String path = "/shells/" + VABPathTools.encodePathElement(aas.getIdentification().getId()) + "/aas/submodels/"+ submodel.getIdShort() + "/submodel";
		System.out.println(provider.getValue(path));
	}

}
