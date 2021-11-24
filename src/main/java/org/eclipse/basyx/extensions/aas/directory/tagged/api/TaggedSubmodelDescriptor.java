/* Copyright 2021 objective partner AG, all rights reserved */
package org.eclipse.basyx.extensions.aas.directory.tagged.api;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Extension of {@link SubmodelDescriptor} that allows to add tags to the
 * descriptor
 * 
 * @author msiebert
 *
 */
public class TaggedSubmodelDescriptor extends SubmodelDescriptor {
	public static final String MODELTYPE = "TaggedSubmodelDescriptor";
	public static final String TAGS = "tags";

	public TaggedSubmodelDescriptor(ISubmodel submodel, String httpEndpoint) {
		super(submodel, httpEndpoint);
		initialize();
	}

	public TaggedSubmodelDescriptor(String idShort, IIdentifier id, String httpEndpoint) {
		super(idShort, id, httpEndpoint);
		initialize();
	}

	protected TaggedSubmodelDescriptor() {
		super();
	}

	@SuppressWarnings("unchecked")
	public static TaggedSubmodelDescriptor createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		Collection<String> tags = (Collection<String>) map.get(TAGS);
		if (tags instanceof List<?>) {
			map.put(TAGS, new HashSet<String>(tags));
		}

		TaggedSubmodelDescriptor descriptor = new TaggedSubmodelDescriptor();
		descriptor.putAll(map);

		return descriptor;
	}

	private void initialize() {
		this.put(TAGS, new HashSet<>());
	}

	public void addTag(String tag) {
		getTags().add(tag);
	}

	public void addTags(List<String> tags) {
		tags.stream().forEach(this::addTag);
	}

	@SuppressWarnings("unchecked")
	public Set<String> getTags() {
		return (Set<String>) this.get(TAGS);
	}

	@Override
	protected String getModelType() {
		return MODELTYPE;
	}
}
