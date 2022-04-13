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
package org.eclipse.basyx.aas.restapi.observing;

import java.util.stream.Stream;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.observer.Observable;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;

/**
 * Implementation of {@link IAASAPI} that calls back registered
 * {@link IAASAPIObserver} when changes on Submodel References occur
 * 
 * @author fried
 *
 */
public class ObservableAASAPI extends Observable<IAASAPIObserver> implements IAASAPI {

	private IAASAPI aasAPI;

	public ObservableAASAPI(IAASAPI observedAPI) {
		aasAPI = observedAPI;
	}

	@Override
	public IAssetAdministrationShell getAAS() {
		return aasAPI.getAAS();
	}

	@Override
	public void addSubmodel(IReference submodel) {
		if (!containsSubmodelReference(submodel))
			throw new MalformedRequestException("Reference has to contain a submodel");

		aasAPI.addSubmodel(submodel);
		observers.stream().forEach(o -> o.submodelAdded(submodel));
	}

	@Override
	public void removeSubmodel(String id) {
		aasAPI.removeSubmodel(id);
		observers.stream().forEach(o -> o.submodelRemoved(id));
	}

	private boolean containsSubmodelReference(IReference submodel) {
		Stream<IKey> filtered = submodel.getKeys().stream().filter(o -> o.getType().name().equalsIgnoreCase(KeyElements.SUBMODEL.getStandardizedLiteral()));
		return filtered.count() > 0;
	}

}
