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

import java.util.Set;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * A tagged directory is a registry that allows to register AAS and associate
 * tags with them. It is possible to retrieve AAS based on tags
 * 
 * @author schnicke
 *
 */
public interface IAASTaggedDirectory extends IAASRegistry {
	public void register(TaggedAASDescriptor descriptor);

	/**
	 * Looks up all AAS that are tagged with <i>tag</i>
	 * 
	 * @param tag
	 * @return
	 */
	public Set<TaggedAASDescriptor> lookupTag(String tag);

	/**
	 * Looks up all AAS that are tagged with all <i>tags</i>
	 * 
	 * @param tags
	 * @return
	 */
	public Set<TaggedAASDescriptor> lookupTags(Set<String> tags);

	/**
	 * Registers SM descriptor with tags in the registry, deletes the old registration if exists.
	 *
	 * @param aas identifier for the Asset Administration Shell.
	 * @param descriptor with information of tags.
	 */
	public default void registerSubmodel(IIdentifier aas, TaggedSubmodelDescriptor descriptor) {
		throw new UnsupportedOperationException("The method registerSubmodel has not been implemented!");
	}

	/**
	 * Looks up all SM that are tagged with <i>submodelTag</i>
	 *
	 * @param submodelTag
	 * @return
	 */
	public default Set<TaggedSubmodelDescriptor> lookupSubmodelTag(String submodelTag) {
		throw new UnsupportedOperationException("The method lookupSubmodelTag has not been implemented!");
	}

	/**
	 * Looks up all SM that are tagged with <i>submodelTags</i>
	 *
	 * @param submodelTags
	 * @return
	 */
	public default Set<TaggedSubmodelDescriptor> lookupSubmodelTags(Set<String> submodelTags) {
		throw new UnsupportedOperationException("The method lookupSubmodelTags has not been implemented!");
	}

	/**
	 * Looks up all SM that are tagged with <i>submodelTags</i> and that belongs to an AssetAdministrationShell 
	 * tagged with <i>aasTags</i>. If a tag is given with a wildcard character asterisk (*), the tag will have the effect to match
	 * all other tags.
	 *
	 * @param aasTags
	 * @param submodelTags
	 * @return
	 */
	public default Set<TaggedSubmodelDescriptor> lookupBothAasAndSubmodelTags(Set<String> aasTags,
			Set<String> submodelTags) {
		throw new UnsupportedOperationException("The method lookupBothAasAndSubmodelTags has not been implemented!");
	}

}
