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
package org.eclipse.basyx.aas.registration.memory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Implements a preconfigured registry based on the Map interface
 */
public class MapRegistryHandler implements IRegistryHandler {
	protected Map<String, AASDescriptor> descriptorMap;

	/**
	 * Constructor that takes a reference to a map as a base for the registry
	 * entries
	 */
	public MapRegistryHandler(Map<String, AASDescriptor> rootMap) {
		descriptorMap = rootMap;
	}

	@Override
	public boolean contains(IIdentifier id) {
		return descriptorMap.containsKey(id.getId());
	}

	@Override
	public void remove(IIdentifier id) {
		AASDescriptor removed = descriptorMap.remove(id.getId());

		IIdentifier aasId = removed.getIdentifier();
		if (!aasId.getId().equals(id.getId())) {
			// id is an assetId => also remove the aasId-mapping
			descriptorMap.remove(aasId.getId());
		} else {
			IAsset asset = removed.getAsset();
			if (asset != null) {
				IIdentifier assetId = asset.getIdentification();
				descriptorMap.remove(assetId.getId());
			}
		}
	}

	@Override
	public void insert(AASDescriptor descriptor) {
		// insert with descriptor id
		String id = descriptor.getIdentifier().getId();
		descriptorMap.put(id, descriptor);

		// insert with asset id if present
		IAsset asset = descriptor.getAsset();
		if (asset != null) {
			String assetId = asset.getIdentification().getId();
			descriptorMap.put(assetId, descriptor);
		}
	}

	@Override
	public void update(AASDescriptor descriptor) {
		insert(descriptor); // has no semantic difference for hashmaps
	}

	@Override
	public AASDescriptor get(IIdentifier id) {
		return descriptorMap.get(id.getId());
	}

	@Override
	public List<AASDescriptor> getAll() {
		return new ArrayList<>(new HashSet<>(descriptorMap.values()));
	}
}
