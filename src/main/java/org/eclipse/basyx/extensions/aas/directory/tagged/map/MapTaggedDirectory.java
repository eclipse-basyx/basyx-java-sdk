/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.directory.tagged.map;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.memory.AASRegistry;
import org.eclipse.basyx.aas.registration.memory.IRegistryHandler;
import org.eclipse.basyx.aas.registration.memory.MapRegistryHandler;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedSubmodelDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Map implementation of a tagged directory. It extends {@link AASRegistry} by
 * additionally managing a map of tags
 * 
 * @author schnicke
 *
 */
public class MapTaggedDirectory extends AASRegistry implements IAASTaggedDirectory {
	protected Map<String, Set<TaggedAASDescriptor>> tagMap;
	private Map<String, Set<TaggedSubmodelDescriptor>> submodelTagMap = new LinkedHashMap<>();

	private static final String WILDCARD = "*";

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

	public MapTaggedDirectory(IRegistryHandler registryHandler, Map<String, Set<TaggedAASDescriptor>> tagMap) {
		super(registryHandler);
		this.tagMap = tagMap;
	}

	@Override
	public void register(TaggedAASDescriptor descriptor) {
		// Let MapRegistry take care of the registry part and only manage the tags
		super.register(descriptor);
		addTags(descriptor);

		Collection<SubmodelDescriptor> submodelDescriptors = descriptor.getSubmodelDescriptors();
		for(SubmodelDescriptor smDesc : submodelDescriptors) {
			TaggedSubmodelDescriptor taggedSmDesc = TaggedSubmodelDescriptor.createAsFacade(smDesc);
			Set<String> tags = taggedSmDesc.getTags();
			if (tags != null && !tags.isEmpty()) {
				addSubmodelTags(tags, taggedSmDesc);
			}
		}
	}

	@Override
	public void registerSubmodel(IIdentifier aas, TaggedSubmodelDescriptor descriptor) {
		super.register(aas, descriptor);
		addSubmodelTags(descriptor.getTags(), descriptor);
	}

	protected void addSubmodelTags(Set<String> submodelTags, TaggedSubmodelDescriptor descriptor) {
		submodelTags.stream().forEach(t -> addSubmodelTag(t, descriptor));
	}

	private synchronized void addSubmodelTag(String submodelTag, TaggedSubmodelDescriptor descriptor) {
		submodelTagMap.computeIfAbsent(submodelTag, key -> new LinkedHashSet<TaggedSubmodelDescriptor>()).add(descriptor);
	}

	@SuppressWarnings("unchecked")
	public void deleteSubmodelTag(IIdentifier aasIdentifier, IIdentifier smIdentifier) {
		SubmodelDescriptor submodelDescriptor = super.lookupSubmodel(aasIdentifier, smIdentifier);
		super.delete(aasIdentifier, smIdentifier);

		Set<String> tags = (Set<String>) submodelDescriptor.get("tags");
		tags.stream().forEach(t -> submodelTagMap.get(t).remove(submodelDescriptor));
	}

	@Override
	public Set<TaggedSubmodelDescriptor> lookupBothAasAndSubmodelTags(Set<String> aasTags, Set<String> submodelTags) {
		if (aasTags.isEmpty() || submodelTags.isEmpty() || (aasTags.contains(WILDCARD) && submodelTags.contains(WILDCARD))) {
			return Collections.emptySet();
		} else if (aasTags.contains(WILDCARD)) {
			return lookupSubmodelTags(submodelTags);
		} else if (submodelTags.contains(WILDCARD)) {
			return lookupAllSubmodelDescriptorsForAasTags(aasTags);
		} else {
			return lookupCombinedTags(aasTags, submodelTags);
		}
	}

	private Set<TaggedSubmodelDescriptor> lookupCombinedTags(Set<String> aasTags, Set<String> submodelTags) {
		Set<TaggedSubmodelDescriptor> result = new LinkedHashSet<>();
		Set<TaggedAASDescriptor> aasDescriptors = lookupTags(aasTags);
		Set<TaggedSubmodelDescriptor> smDescriptors = lookupSubmodelTags(submodelTags);

		for (TaggedAASDescriptor aasDesc : aasDescriptors) {
			for (TaggedSubmodelDescriptor smDesc : smDescriptors) {
				if (aasDesc.getSubmodelDescriptorFromIdentifierId(smDesc.getIdentifier().getId()) != null) {
					result.add(smDesc);
				}
			}
		}

		return result;
	}

	private Set<TaggedSubmodelDescriptor> lookupAllSubmodelDescriptorsForAasTags(Set<String> aasTags) {
		Set<TaggedSubmodelDescriptor> result = new LinkedHashSet<>();

		Set<TaggedAASDescriptor> desc = lookupTags(aasTags);

		for (TaggedAASDescriptor descriptor : desc) {
			for (SubmodelDescriptor smDesc : descriptor.getSubmodelDescriptors()) {
				result.add(TaggedSubmodelDescriptor.createAsFacade(smDesc));
			}
		}

		return result;
	}

	@Override
	public Set<TaggedSubmodelDescriptor> lookupSubmodelTag(String submodelTag) {
		if (submodelTagMap.containsKey(submodelTag)) {
			return new LinkedHashSet<>(submodelTagMap.get(submodelTag));
		} else {
			return new LinkedHashSet<>();
		}
	}

	@Override
	public Set<TaggedSubmodelDescriptor> lookupSubmodelTags(Set<String> submodelTags) {
		Set<TaggedSubmodelDescriptor> result = new LinkedHashSet<>();
		result.addAll(getMatchingSubmodelDescriptors(submodelTags));

		return result;
	}

	private Set<TaggedSubmodelDescriptor> getMatchingSubmodelDescriptors(Set<String> submodelTags) {
		Set<TaggedSubmodelDescriptor> result = new LinkedHashSet<>();
		Set<TaggedSubmodelDescriptor> smDescriptors = new LinkedHashSet<>();

		for (String s : submodelTags) {
			smDescriptors.addAll(lookupSubmodelTag(s));
		}

		for (TaggedSubmodelDescriptor desc : smDescriptors) {
			if (desc.getTags().containsAll(submodelTags)) {
				result.add(desc);
			}
		}

		return result;
	}

	@Override
	public Set<TaggedAASDescriptor> lookupTag(String tag) {
		if (tagMap.containsKey(tag)) {
			return new LinkedHashSet<>(tagMap.get(tag));
		} else {
			return new LinkedHashSet<>();
		}
	}

	@Override
	public Set<TaggedAASDescriptor> lookupTags(Set<String> tags) {
		Set<TaggedAASDescriptor> result = new LinkedHashSet<>();
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

	protected void addTags(TaggedAASDescriptor descriptor) {
		(descriptor.getTags()).stream().forEach(t -> addTag(t, descriptor));
	}

	private synchronized void addTag(String tag, TaggedAASDescriptor descriptor) {
		if (!tagMap.containsKey(tag)) {
			tagMap.put(tag, new LinkedHashSet<>());
		}

		tagMap.get(tag).add(descriptor);
	}

}
