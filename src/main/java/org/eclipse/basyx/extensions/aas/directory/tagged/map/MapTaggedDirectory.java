/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.directory.tagged.map;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.registration.memory.AASRegistry;
import org.eclipse.basyx.aas.registration.memory.MapRegistryHandler;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Map implementation of a tagged directory. It extends {@link AASRegistry} by
 * additionally managing a map of tags
 * 
 * @author schnicke
 *
 */
public class MapTaggedDirectory extends AASRegistry implements IAASTaggedDirectory {
	private Map<String, Set<TaggedAASDescriptor>> tagMap;

	/**
	 * Constructor that takes a reference to a map as a base for the registry
	 * entries
	 * 
	 * @param rootMap
	 * @param tagMap
	 */
	public MapTaggedDirectory(Map<String, AASDescriptor> rootMap, Map<String, Set<TaggedAASDescriptor>> tagMap) {
		super(new MapRegistryHandler(rootMap));
		this.tagMap = tagMap;
	}

	@Override
	public void register(TaggedAASDescriptor descriptor) {
		// Let MapRegistry take care of the registry part and only manage the tags
		super.register(descriptor);
		addTags(descriptor.getTags(), descriptor);
	}

	@Override
	public Set<TaggedAASDescriptor> lookupTag(String tag) {
		if (tagMap.containsKey(tag)) {
			return new HashSet<>(tagMap.get(tag));
		} else {
			return new HashSet<>();
		}
	}

	@Override
	public Set<TaggedAASDescriptor> lookupTags(Set<String> tags) {
		Set<TaggedAASDescriptor> result = new HashSet<>();
		Set<Set<TaggedAASDescriptor>> descriptors = tags.stream().map(t -> lookupTag(t)).collect(Collectors.toSet());

		if (descriptors.size() > 0) {
			// Iterate through set of sets and use retainAll() to find intersection
			Iterator<Set<TaggedAASDescriptor>> it = descriptors.iterator();
			result = it.next();

			while (it.hasNext()) {
				result.retainAll(it.next());
			}
		}

		return result;
	}

	@Override
	public void delete(IIdentifier aasIdentifier) {
		// Let MapRegistry take care of the registry part and only manage the tags
		AASDescriptor desc = super.lookupAAS(aasIdentifier);
		super.delete(aasIdentifier);

		if (desc instanceof TaggedAASDescriptor) {
			((TaggedAASDescriptor) desc).getTags().stream().forEach(t -> tagMap.get(t).remove(desc));
		}
	}

	private void addTags(Set<String> tags, TaggedAASDescriptor descriptor) {
		tags.stream().forEach(t -> addTag(t, descriptor));
	}

	private synchronized void addTag(String tag, TaggedAASDescriptor descriptor) {
		if (!tagMap.containsKey(tag)) {
			tagMap.put(tag, new HashSet<>());
		}

		tagMap.get(tag).add(descriptor);
	}

}
