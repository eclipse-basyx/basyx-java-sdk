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
package org.eclipse.basyx.aas.bundle;

import java.util.Objects;
import java.util.Set;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;

/**
 * Helper class to bundle an AAS with its corresponding submodels, e.g. for
 * passing them to a server environment
 * 
 * @author schnicke
 *
 */
public class AASBundle {
	private IAssetAdministrationShell aas;
	private Set<ISubmodel> submodels;

	public AASBundle(IAssetAdministrationShell aas, Set<ISubmodel> submodels) {
		super();
		this.aas = aas;
		this.submodels = submodels;
	}

	public IAssetAdministrationShell getAAS() {
		return aas;
	}

	public Set<ISubmodel> getSubmodels() {
		return submodels;
	}

	@Override
	public int hashCode() {
		return Objects.hash(aas, submodels);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AASBundle other = (AASBundle) obj;
		return Objects.equals(this.aas, other.aas) && Objects.equals(this.submodels, other.submodels);
	}

	@Override
	public String toString() {
		return "AASBundle [aas=" + aas + ", submodels=" + submodels + "]";
	}

}
