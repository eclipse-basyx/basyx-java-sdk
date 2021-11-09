/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.registration.memory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Implements a preconfigured registry based on the Map interface
 */
public class MapRegistryHandler implements IRegistryHandler {
	protected Map<String, AASDescriptor> shellDescriptorMap;
	protected Map<String, SubmodelDescriptor> submodelDescriptorMap;

	/**
	 * Constructor that takes a reference to a map as a base for the registry
	 * entries
	 */
	public MapRegistryHandler(Map<String, AASDescriptor> shellRootMap, Map<String, SubmodelDescriptor> submodelRootMap) {
		shellDescriptorMap = shellRootMap;
		submodelDescriptorMap = submodelRootMap;
	}

	@Override
	public boolean containsShell(IIdentifier shellIdentifier) {
		return shellDescriptorMap.containsKey(shellIdentifier.getId());
	}

	@Override
	public boolean containsSubmodel(IIdentifier submodelIdentifier) {
		return submodelDescriptorMap.containsKey(submodelIdentifier.getId());
	}

	@Override
	public void removeShell(IIdentifier shellIdentifier) {
		shellDescriptorMap.remove(shellIdentifier.getId());
	}

	@Override
	public void removeSubmodel(IIdentifier submodelIdentifier) {
		submodelDescriptorMap.remove(submodelIdentifier.getId());
	}

	@Override
	public void insertShell(AASDescriptor shellDescriptor) {
		String id = shellDescriptor.getIdentifier().getId();
		shellDescriptorMap.put(id, shellDescriptor);
	}

	@Override
	public void insertSubmodel(SubmodelDescriptor submodelDescriptor) {
		String id = submodelDescriptor.getIdentifier().getId();
		submodelDescriptorMap.put(id, submodelDescriptor);
	}

	@Override
	public void updateShell(AASDescriptor modelDescriptor) {
		insertShell(modelDescriptor); // has no semantic difference for hashmaps
	}

	@Override
	public void updateSubmodel(SubmodelDescriptor modelDescriptor) {
		insertSubmodel(modelDescriptor); // has no semantic difference for hashmaps
	}

	@Override
	public AASDescriptor getShell(IIdentifier shellIdentifier) {
		return shellDescriptorMap.get(shellIdentifier.getId());
	}

	@Override
	public List<AASDescriptor> getAllShells() {
		return new ArrayList<>(new HashSet<>(shellDescriptorMap.values()));
	}

	@Override
	public SubmodelDescriptor getSubmodel(IIdentifier submodelIdentifier) {
		return submodelDescriptorMap.get(submodelIdentifier.getId());
	}

	@Override
	public List<SubmodelDescriptor> getAllSubmodels() {
		return new ArrayList<>(new HashSet<>(submodelDescriptorMap.values()));
	}
}
