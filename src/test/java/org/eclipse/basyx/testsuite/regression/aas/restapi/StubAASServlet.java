/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.aas.restapi;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.registry.descriptor.ModelUrn;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.testsuite.regression.submodel.restapi.SimpleAASSubmodel;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;

public class StubAASServlet extends VABHTTPInterface<MultiSubmodelProvider> {
	private static final long serialVersionUID = 8859337501045845823L;

	// Used short ids
	public static final String AASIDSHORT = "StubAAS";
	public static final String SMIDSHORT = "StubSM";

	// Used URNs
	public static final ModelUrn AASURN = new ModelUrn("urn:fhg:es.iese:aas:1:1:myAAS#001");
	public static final ModelUrn SMURN = new ModelUrn("urn:fhg:es.iese:aas:1:1:mySM#001");

	public StubAASServlet() {
		super(new MultiSubmodelProvider());

		Submodel sm = new Submodel();
		sm.setIdentification(SMURN.getIdType(), SMURN.getId());
		sm.setIdShort(SMIDSHORT);
		AssetAdministrationShell aas = new AssetAdministrationShell();
		aas.addSubmodel(sm);
		aas.setIdShort(AASIDSHORT);
		aas.setIdentification(AASURN);

		getModelProvider().setAssetAdministrationShell(new AASModelProvider(aas));
		getModelProvider().addSubmodel(new SubmodelProvider(new SimpleAASSubmodel(SMIDSHORT)));
	}

}
