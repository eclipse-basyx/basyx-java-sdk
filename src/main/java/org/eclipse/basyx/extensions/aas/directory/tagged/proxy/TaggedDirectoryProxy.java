/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.directory.tagged.proxy;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.restapi.TaggedDirectoryProvider;
import org.eclipse.basyx.registry.proxy.RegistryProxy;
import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnector;

public class TaggedDirectoryProxy extends RegistryProxy implements IAASTaggedDirectory {

	private IModelProvider taggedProvider;

	public TaggedDirectoryProxy(String registryUrl) {
		super(registryUrl);
		taggedProvider = createTaggedProxy(new JSONConnector(new HTTPConnector(registryUrl)));
	}

	public TaggedDirectoryProxy(IModelProvider provider) {
		super(provider);
		taggedProvider = createTaggedProxy(provider);
	}

	private static VABElementProxy createTaggedProxy(IModelProvider provider) {
		return new VABElementProxy(TaggedDirectoryProvider.PREFIX, provider);
	}

	@Override
	public void register(TaggedAASDescriptor descriptor) {
		taggedProvider.createValue("", descriptor);
	}

	@Override
	public Set<TaggedAASDescriptor> lookupTag(String tag) {
		return performTagRequest(tag);
	}

	@Override
	public Set<TaggedAASDescriptor> lookupTags(Set<String> tags) {
		StringJoiner joiner = new StringJoiner(",");
		tags.stream().forEach(t -> joiner.add(t));

		return performTagRequest(joiner.toString());
	}

	@SuppressWarnings("unchecked")
	private Set<TaggedAASDescriptor> performTagRequest(String tagList) {
		Collection<Map<String, Object>> desc = (Collection<Map<String, Object>>) taggedProvider.getValue(TaggedDirectoryProvider.API_ACCESS + tagList);
		return desc.stream().map(m -> TaggedAASDescriptor.createAsFacade(m)).collect(Collectors.toSet());
	}

}
