/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.restapi.observing;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.observer.Observable;

/**
 * Implementation of {@link IAASAPI} that calls back registered
 * {@link IAASAPIObserver} when changes on Submodel References occur
 * 
 * @author fried
 *
 */
public class ObservableAASAPI extends Observable<IAASAPIObserver> implements IAASAPI {

	IAASAPI aasAPI;

	public ObservableAASAPI(IAASAPI observedAPI) {
		aasAPI = observedAPI;
	}

	@Override
	public IAssetAdministrationShell getAAS() {
		return aasAPI.getAAS();
	}

	@Override
	public void addSubmodel(IReference submodel) {
		for (IKey key : submodel.getKeys()) {
			if (key.getType().name().equalsIgnoreCase("Submodel")) {
				aasAPI.addSubmodel(submodel);
				observers.stream().forEach(o -> o.submodelAdded(submodel));
			}
		}
	}

	@Override
	public void removeSubmodel(String id) {
		aasAPI.removeSubmodel(id);
		observers.stream().forEach(o -> o.submodelRemoved(id));
	}

}
