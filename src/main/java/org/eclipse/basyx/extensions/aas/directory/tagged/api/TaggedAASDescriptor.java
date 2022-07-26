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
package org.eclipse.basyx.extensions.aas.directory.tagged.api;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Extension of {@link AASDescriptor} that allows to add tags to the descriptor
 * 
 * @author schnicke
 *
 */
public class TaggedAASDescriptor extends AASDescriptor {
	public static final String MODELTYPE = "TaggedAASDescriptor";
	public static final String TAGS = "tags";

	/**
	 * Create a new aas descriptor that retrieves the necessary information from a
	 * passend AssetAdministrationShell
	 * 
	 * @param assetAdministrationShell
	 * @param endpoint
	 */
	public TaggedAASDescriptor(IAssetAdministrationShell assetAdministrationShell, String endpoint) {
		super(assetAdministrationShell, endpoint);
		initialize();
	}

	protected TaggedAASDescriptor() {
		super();
	}

	@SuppressWarnings("unchecked")
	public static TaggedAASDescriptor createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		Collection<String> tags = (Collection<String>) map.get(TAGS);
		if (tags instanceof List<?>) {
			map.put(TAGS, new HashSet<String>(tags));
		}

		TaggedAASDescriptor desc = new TaggedAASDescriptor();
		desc.putAll(map);
		return desc;
	}

	/**
	 * Create a new descriptor with minimal information
	 */
	public TaggedAASDescriptor(String idShort, IIdentifier id, String httpEndpoint) {
		super(idShort, id, httpEndpoint);
		initialize();
	}

	private void initialize() {
		put(TAGS, new HashSet<>());
	}

	/**
	 * Adds a tag to the AAS descriptor
	 * 
	 * @param tag
	 */
	public void addTag(String tag) {
		getTags().add(tag);
	}

	/**
	 * Adds a list of tags to the AAS descriptor
	 * 
	 * @param tags
	 */
	public void addTags(List<String> tags) {
		tags.stream().forEach(this::addTag);
	}

	/**
	 * Retrieves the Tags associated with the AAS descriptor
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<String> getTags() {
		return (Collection<String>) get(TAGS);
	}

	@Override
	protected String getModelType() {
		return MODELTYPE;
	}
}
