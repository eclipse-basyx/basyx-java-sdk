/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.directory.tagged.api;

import java.util.Set;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;

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
	 * @param tag
	 * @return
	 */
	public Set<TaggedAASDescriptor> lookupTags(Set<String> tags);

}
