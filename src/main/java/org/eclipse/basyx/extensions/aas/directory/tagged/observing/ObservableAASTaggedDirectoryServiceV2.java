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

package org.eclipse.basyx.extensions.aas.directory.tagged.observing;

import java.util.Set;

import org.eclipse.basyx.aas.registration.observing.IAASRegistryServiceObserver;
import org.eclipse.basyx.aas.registration.observing.ObservableAASRegistryService;
import org.eclipse.basyx.aas.registration.observing.ObservableAASRegistryServiceV2;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedSubmodelDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 *
 * Implementation of {@link IAASTaggedDirectory} that calls back registered
 * {@link IAASRegistryServiceObserver} when changes on Registry occur
 *
 * @author espen
 *
 */
public class ObservableAASTaggedDirectoryServiceV2 extends ObservableAASRegistryServiceV2 implements IAASTaggedDirectory {
	private IAASTaggedDirectory taggedDirectory;

	public ObservableAASTaggedDirectoryServiceV2(IAASTaggedDirectory taggedDirectory) {
		super(taggedDirectory);
		this.taggedDirectory = taggedDirectory;
	}

	@Override
	public void register(TaggedAASDescriptor descriptor) {
		taggedDirectory.register(descriptor);
		observers.stream().forEach(o -> o.aasRegistered(descriptor, aasRegistry.getRegistryId()));
	}
	
	@Override
	public void registerSubmodel(IIdentifier aas, TaggedSubmodelDescriptor descriptor) {
	    taggedDirectory.register(aas, descriptor);
	    observers.stream().forEach(o -> o.submodelRegistered(aas, descriptor, aasRegistry.getRegistryId()));
	}

	@Override
	public Set<TaggedAASDescriptor> lookupTag(String tag) {
		return taggedDirectory.lookupTag(tag);
	}

	@Override
	public Set<TaggedAASDescriptor> lookupTags(Set<String> tags) {
		return taggedDirectory.lookupTags(tags);
	}
	
	@Override
	public Set<TaggedSubmodelDescriptor> lookupSubmodelTag(String submodelTag) {
	    return taggedDirectory.lookupSubmodelTag(submodelTag);
	}
	
	@Override
	public Set<TaggedSubmodelDescriptor> lookupSubmodelTags(Set<String> submodelTags) {
	    return taggedDirectory.lookupSubmodelTags(submodelTags);
	}
	
	@Override
	public Set<TaggedSubmodelDescriptor> lookupBothAasAndSubmodelTags(Set<String> aasTags, Set<String> submodelTags) {
	    return taggedDirectory.lookupBothAasAndSubmodelTags(aasTags, submodelTags);
	}
}
