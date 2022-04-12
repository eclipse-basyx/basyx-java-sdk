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
package org.eclipse.basyx.extensions.aas.directory.tagged.proxy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelDescriptor;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedSubmodelDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.restapi.TaggedDirectoryProvider;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnector;

public class TaggedDirectoryProxy extends AASRegistryProxy implements IAASTaggedDirectory {

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
		taggedProvider.createValue(VABPathTools.encodePathElement(descriptor.getIdentifier().getId()), descriptor);
	}

	@Override
	public void registerSubmodel(IIdentifier aas, TaggedSubmodelDescriptor smDescriptor) {
		taggedProvider.createValue(VABPathTools.encodePathElement(aas.getId() + "/submodels/" + smDescriptor.getIdentifier().getId()), smDescriptor);
	}

	@Override
	public Set<TaggedSubmodelDescriptor> lookupBothAasAndSubmodelTags(Set<String> aasTags, Set<String> submodelTags) {
		String aasTagsList = joinTagsAsString(aasTags);
		String submodelTagsList = joinTagsAsString(submodelTags);

		return performCombinedTagRequest(aasTagsList, submodelTagsList);
	}

	@SuppressWarnings("unchecked")
	private Set<TaggedSubmodelDescriptor> performCombinedTagRequest(String aasTagList, String submodelTagList) {
		Collection<Map<String, Object>> desc = (Collection<Map<String, Object>>) taggedProvider.getValue("?" + TaggedDirectoryProvider.API_ACCESS + aasTagList + "&" + TaggedDirectoryProvider.SUBMODEL_API_ACCESS + submodelTagList);

		return desc.stream().map(TaggedSubmodelDescriptor::createAsFacade).collect(Collectors.toSet());
	}

	@Override
	public Set<TaggedAASDescriptor> lookupTag(String tag) {
		return performTagRequest(tag);
	}

	@Override
	public Set<TaggedAASDescriptor> lookupTags(Set<String> tags) {
		return performTagRequest(joinTagsAsString(tags));
	}

	@SuppressWarnings("unchecked")
	private Set<TaggedAASDescriptor> performTagRequest(String tagList) {
		Collection<Map<String, Object>> desc = (Collection<Map<String, Object>>) taggedProvider.getValue("?" + TaggedDirectoryProvider.API_ACCESS + tagList);
		return desc.stream().map(m -> TaggedAASDescriptor.createAsFacade(m)).collect(Collectors.toSet());
	}

	public Set<TaggedSubmodelDescriptor> lookupSubmodelTag(String submodelTag) {
		return performSubmodelTagRequest(submodelTag);
	}

	public Set<TaggedSubmodelDescriptor> lookupSubmodelTags(Set<String> tags) {
		return performSubmodelTagRequest(joinTagsAsString(tags));
	}

	@SuppressWarnings("unchecked")
	private Set<TaggedSubmodelDescriptor> performSubmodelTagRequest(String submodelTagList) {
		Collection<Map<String, Object>> desc = (Collection<Map<String, Object>>) taggedProvider.getValue("?" + TaggedDirectoryProvider.SUBMODEL_API_ACCESS + submodelTagList);
		return desc.stream().map(m -> TaggedSubmodelDescriptor.createAsFacade(m)).collect(Collectors.toSet());
	}

	private String joinTagsAsString(Set<String> tags) {
		StringJoiner joiner = new StringJoiner(",");
		tags.stream().forEach(joiner::add);

		return joiner.toString();
	}

}
