/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.directory.tagged.restapi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.registration.restapi.AASRegistryModelProvider;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.map.MapTaggedDirectory;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

public class TaggedDirectoryProvider extends AASRegistryModelProvider {
	private MapTaggedDirectory directory;
	public static final String PREFIX = "api/v1/directory";
	public static final String API_ACCESS = "?tags=";

	public TaggedDirectoryProvider() {
		this(new MapTaggedDirectory(new HashMap<>(), new HashMap<>(), new HashMap<>()));
	}

	public TaggedDirectoryProvider(MapTaggedDirectory directory) {
		super(directory);
		this.directory = directory;
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		path = VABPathTools.stripSlashes(path);
		if (path.startsWith(PREFIX)) {
			return directory.lookupTags(extractTags(path));
		} else {
			return super.getValue(path);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		path = VABPathTools.stripSlashes(path);
		if (path.startsWith(PREFIX)) {
			directory.register(TaggedAASDescriptor.createAsFacade((Map<String, Object>) newEntity));
		} else {
			super.createValue(path, newEntity);
		}
	}

	private Set<String> extractTags(String path) {
		path = VABPathTools.stripSlashes(path);
		path = path.replaceFirst(PREFIX, "");

		// Paths now does only contain ?tags=a,b,c
		path = path.replaceFirst(Pattern.quote(API_ACCESS), "");
		return Arrays.stream(path.split(",")).collect(Collectors.toSet());
	}

}
